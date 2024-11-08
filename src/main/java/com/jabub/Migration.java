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

    File cloneDirectoryPath = new File("C:\\Users\\mt2560\\Downloads\\local-git");
    Git git;

    public Migration() throws IOException, GitAPIException {

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

    public List<Path> getAllServiceFolders() throws IOException {
        List<Path> result;
        try (Stream<Path> walk = walk(cloneDirectoryPath.toPath())) {
            result = walk.filter(Files::isDirectory).toList();
        }
        return result;

    }

}
