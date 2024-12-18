package com.jabub;

import com.jabub.exception.MixedVersionsException;
import com.jabub.exception.NoScriptsException;
import com.jabub.exception.ScriptHasIncorrectName;
import com.jabub.exception.VersionPropertyFileNotCreated;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Properties;
import java.util.stream.Stream;

import static com.jabub.EnvVar.*;
import static com.jabub.Utils.*;
import static java.lang.ProcessBuilder.Redirect.to;
import static java.nio.file.Files.walk;
import static java.util.stream.Collectors.toList;
import static lombok.AccessLevel.PRIVATE;

@Slf4j
@FieldDefaults(makeFinal = true, level = PRIVATE)
public class Migration {

    File baseFolder;
    List<Path> scripts;
    boolean semanticVersioned;
    Properties versionProperties;
    SemanticVersionsComparator semanticVersionsComparator;
    NumberVersionsComparator numberVersionsComparator;

    public Migration(File serviceFolder) throws IOException, MixedVersionsException, NoScriptsException, ScriptHasIncorrectName, VersionPropertyFileNotCreated {
        this.baseFolder = serviceFolder;
        this.scripts = this.getAllScriptsForServiceFolder(serviceFolder);

        if (scripts == null || scripts.isEmpty()) {
            throw new NoScriptsException();
        }
        for (Path script : scripts) {
            if (!hasCorrectFormat(script.getFileName().toString())) {
                throw new ScriptHasIncorrectName(script);
            }
        }
        semanticVersioned = throwExceptionIfContainsMixedSemanticAndNumberedVersioning();
        versionProperties = new Properties();
        loadPropertiesWithLastExecutedVersion();
        this.semanticVersionsComparator = new SemanticVersionsComparator();
        this.numberVersionsComparator = new NumberVersionsComparator();
    }

    private void loadPropertiesWithLastExecutedVersion() throws IOException, VersionPropertyFileNotCreated {
        File versionPropertiesFile = getVersionPropertiesFile(this.baseFolder.getName());
        if (versionPropertiesFile.exists()) {
            versionProperties.load(new FileInputStream(versionPropertiesFile));
        } else {
            boolean success = versionPropertiesFile.createNewFile();
            if (success) {
                log.debug("New version properties file created: '{}'", versionPropertiesFile.getAbsolutePath());
            } else {
                throw new VersionPropertyFileNotCreated(versionPropertiesFile.getAbsolutePath());
            }
        }
    }


    private List<Path> getAllScriptsForServiceFolder(File serviceFolder) throws IOException {
        List<Path> result;
        try (Stream<Path> walk = walk(serviceFolder.toPath())) {
            result = walk.filter(Files::isRegularFile).collect(toList());
        }
        return result;
    }

    public boolean throwExceptionIfContainsMixedSemanticAndNumberedVersioning() throws MixedVersionsException {
        List<String> scriptFileNames = scripts.stream().map(path -> path.getFileName().toString()).toList();

        List<String> schemanticVersioned = scriptFileNames.stream().filter(name -> name.startsWith("v")).toList();
        List<String> numberedVersioned = scriptFileNames.stream().filter(name -> !name.startsWith("v")).toList();

        if (!schemanticVersioned.isEmpty() && !numberedVersioned.isEmpty()) {
            throw new MixedVersionsException(schemanticVersioned, numberedVersioned);
        }
        return numberedVersioned.isEmpty();
    }

    public int executeScript(Path script) throws IOException, InterruptedException {
        String scriptPath = script.toAbsolutePath().toString();

        ProcessBuilder processBuilder;

        if (scriptPath.endsWith(".py")) {
            processBuilder = new ProcessBuilder("python3", scriptPath);
        } else if (scriptPath.endsWith(".sh")) {
            processBuilder = new ProcessBuilder("bash", scriptPath);
        } else if (scriptPath.endsWith(".js")) {
            processBuilder = new ProcessBuilder("node", scriptPath);
        } else if (scriptPath.endsWith(".java")) {
            String className = script.getFileName().toString().replace(".java", "");
            Path parentDir = script.getParent();

            // Compilation of Java file
            ProcessBuilder compileProcess = new ProcessBuilder("javac", scriptPath);
            compileProcess.directory(parentDir.toFile());
            compileProcess.inheritIO();
            Process compile = compileProcess.start();
            if (compile.waitFor() != 0) {
                throw new IOException("Failed to compile Java file: " + scriptPath);
            }

            // Running a Java file
            processBuilder = new ProcessBuilder("java", "-cp", parentDir.toString(), className);
        } else {
            // check the first line (shebang) for scripts without the extension
            try (Stream<String> lines = Files.lines(script)) {
                String firstLine = lines.findFirst().orElse("");
                if (firstLine.startsWith("#!/usr/bin/env python3")) {
                    processBuilder = new ProcessBuilder("python3", scriptPath);
                } else if (firstLine.startsWith("#!/bin/bash")) {
                    processBuilder = new ProcessBuilder("bash", scriptPath);
                } else if (firstLine.startsWith("#!/usr/bin/env node")) {
                    processBuilder = new ProcessBuilder("node", scriptPath);
                } else {
                    throw new IOException("Unsupported script type: " + scriptPath);
                }
            }
        }

        // Logs settings
        String logFilePath = scriptPath.replace(MIGRATION_DIRECTORY.toString(), MIGRATION_OUTPUT_DIRECTORY.toString());
        File logFile = new File(logFilePath + ".stdout");
        File errorFile = new File(logFilePath + ".stderr");

        // If file exist for log
        File parentDir = logFile.getParentFile();
        if (!parentDir.exists() && !parentDir.mkdirs()) {
            throw new IOException("Failed to create directories for logs: " + parentDir.getAbsolutePath());
        }

        processBuilder.redirectOutput(to(logFile));
        processBuilder.redirectError(to(errorFile));

        // Executed script
        Process process = processBuilder.start();
        return process.waitFor();
    }


    public List<Path> getAllScriptsSorted() {
        if (scripts.getFirst().getFileName().toString().startsWith(SEMANTIC_VERSION_PREFIX.toString())) {
            scripts.sort(this.semanticVersionsComparator);
        } else {
            scripts.sort(this.numberVersionsComparator);
        }
        return scripts;
    }

    public boolean isHigher(Path script, String lastExecutedVersion) {

        if (this.semanticVersioned) {
            return semanticVersionsComparator.isHigher(script, lastExecutedVersion);
        } else {
            return numberVersionsComparator.isHigher(script, lastExecutedVersion);
        }
    }

    public String getLastExecutedVersion() {
        String initialVersion = semanticVersioned ? "v0.0.0" : "0";
        return versionProperties.getProperty("version", initialVersion);
    }

    public void updateVersion(Path lastExecutedScript) {
        if (lastExecutedScript != null) {
            versionProperties.setProperty("version", extractVersion(lastExecutedScript));
        }
    }
}
