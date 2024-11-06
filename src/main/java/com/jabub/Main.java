package com.jabub;

import org.eclipse.jgit.api.CheckoutCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;

import java.io.File;
import java.io.IOException;

public class Main {

    public static void main(String[] args) throws GitAPIException, IOException {

        File cloneDirectoryPath = new File("C:\\Users\\mt2560\\Downloads\\local-git");
        if (cloneDirectoryPath.exists()) {

            try (Git git = Git.open(cloneDirectoryPath)) {
                git.pull();
            }

        } else {
            try (Git git = Git.cloneRepository()
                    .setURI("https://github.com/tlachy/jabub.git")
                    .setDirectory(cloneDirectoryPath)
                    .call()) {

                Repository repository = git.getRepository();
                System.out.println(repository.getFullBranch());
            }
        }
    }
}
