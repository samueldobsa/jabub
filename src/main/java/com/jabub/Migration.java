package com.jabub;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

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

    public File[] getAllServiceFolders() {
        return new File(GIT_REPO_PATH + MIGRATION_DIRECTORY).listFiles(File::isDirectory);
    }

    public List<Path> getAllScriptsForServiceFolder(File serviceFolder) throws IOException {
        List<Path> result;
        try (Stream<Path> walk = walk(serviceFolder.toPath())) {
            result = walk.filter(Files::isRegularFile).toList();
        }
        return result;
    }

    public boolean checkAllScriptsAreNumberedOrSchemanticVersioned(List<Path> allScriptsForServiceFolder) {
        List<Path> versioned = allScriptsForServiceFolder.stream().filter(path -> path.getFileName().toString().startsWith("v")).toList();
        List<Path> numbered = allScriptsForServiceFolder.stream().filter(path -> !path.getFileName().toString().startsWith("v")).toList();

        if (!versioned.isEmpty() && !numbered.isEmpty()) {
            System.out.println("Service folder can contain only all scipts orderred or all scripts schemantic version. Combination is not allowed");
//TODO print out and use logger
//               System.out.println("Numbered: " + numbered.stream().collect());
//            System.out.println("versioned: " + numbered.stream().collect());
            return false;
        }

        return true;
    }
}

