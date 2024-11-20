package com.jabub;

import com.jabub.exception.MixedVersionsException;
import com.jabub.exception.NoScriptsException;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

import static com.jabub.EnvVar.SCHEMANTIC_VERSION_PREFIX;
import static java.nio.file.Files.walk;
import static java.util.stream.Collectors.toList;

@Slf4j
public class Migration {

    private final File baseFolder;
    private List<Path> scripts;
    private final boolean semanticVersioned;
//    private final Properties appProps;

    public Migration(File serviceFolder) throws IOException, MixedVersionsException, NoScriptsException {
        this.baseFolder = serviceFolder;
        this.scripts = this.getAllScriptsForServiceFolder(serviceFolder);

        if (scripts == null || scripts.isEmpty()) {
            throw new NoScriptsException();
        }
        if(scripts.stream().allMatch(script -> ))
        semanticVersioned = throwExceptionIfContainsMixedSemanticAndNumberedVersioning();
        loadPropertiesWithLastExecutedVersion();
    }

    private void loadPropertiesWithLastExecutedVersion() {

//        appProps.load(new FileInputStream(baseFolder.getAbsolutePath() + MIGRATION_OUTPUT_DIRECTORY);
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
        String lastRunVersion = "v1.1.1";
        return lastRunVersion;
    }
}
