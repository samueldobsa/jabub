package com.jabub;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

import static java.lang.String.join;
import static java.nio.file.Files.walk;

public class Migration {

    public static final String GIT_REPO_PATH = "C:\\Users\\mt2560\\IdeaProjects\\jabub-test-repo";
    public static final String MIGRATION_DIRECTORY = "\\MIGRATION";

    Git git;

    public Migration() throws IOException, GitAPIException {

        File cloneDirectoryPath = new File(GIT_REPO_PATH);

        if (cloneDirectoryPath.exists()) {
            git = Git.open(cloneDirectoryPath);
            git.pull();
        } else {
            git = Git.cloneRepository()
                    .setURI("https://github.com/tlachy/jabub.git")
                    .setDirectory(cloneDirectoryPath)
                    .call();
        }
    }



    public List<Path> getAllScriptsForServiceFolder(File serviceFolder) throws IOException {
        List<Path> result;
        try (Stream<Path> walk = walk(serviceFolder.toPath())) {
            result = walk.filter(Files::isRegularFile).toList();
        }
        return result;
    }

    public boolean checkIfContainsBothNumberedAndVersioned(File serviceFolder, List<Path> allScriptsForServiceFolder) {
        List<String> scriptFileNames = allScriptsForServiceFolder.stream().map(path -> path.getFileName().toString()).toList();

        List<String> versioned = scriptFileNames.stream().filter(name -> name.startsWith("v")).toList();
        List<String> numbered = scriptFileNames.stream().filter(name -> !name.startsWith("v")).toList();

        if (!versioned.isEmpty() && !numbered.isEmpty()) {
            System.out.println("Numbered scripts: " + join(", \n", numbered));
            System.out.println("Versioned scripts: " + join(", \n", versioned));

            return true;
        }
        return true;
    }

    public void executeScript(Path script) {
        //TODO
    }

    public void updateVersion(Path lastExecutedScript) {
        //TODO
    }
}

