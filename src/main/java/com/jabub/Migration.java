package com.jabub;

import com.jabub.exception.MixedVersionsException;
import com.jabub.exception.NoScriptsException;
import com.jabub.exception.ScriptHasIncorrectName;
import com.jabub.exception.VersionPropertyFileNotCreated;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Properties;
import java.util.stream.Stream;

import static com.jabub.EnvVar.SCHEMANTIC_VERSION_PREFIX;
import static com.jabub.Utils.getVersionPropertiesFile;
import static com.jabub.Utils.hasCorrectFormat;
import static java.nio.file.Files.walk;
import static java.util.stream.Collectors.toList;

@Slf4j
public class Migration {

    private final File baseFolder;
    private List<Path> scripts;
    private final boolean semanticVersioned;
    private final Properties versionProperties;

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

    public void executeScript(Path script) {
        //TODO
    }

    public void updateVersion(Path lastExecutedScript) {
        //TODO
    }


    public List<Path> getAllScriptsSorted() {
        if (scripts.getFirst().getFileName().toString().startsWith(SCHEMANTIC_VERSION_PREFIX.toString())) {
            scripts.sort(new SemanticVersionsComparator());
        } else {
            scripts.sort(new NumberVersionsComparator());
        }
        return scripts;
    }

    public String getLastExecutedVersion() {
        String initialVersion = semanticVersioned ? "v0.0.0" : "0";
        return versionProperties.getProperty("version", initialVersion);
    }
}
