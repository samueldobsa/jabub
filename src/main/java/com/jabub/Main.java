package com.jabub;

import org.eclipse.jgit.api.errors.GitAPIException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public class Main {

    public static void main(String[] args) throws GitAPIException, IOException {
        Migration migration = new Migration();
        File[] allServiceFolders = migration.getAllServiceFolders();
        for (File serviceFolder : allServiceFolders) {
            List<Path> allScriptsForServiceFolder = migration.getAllScriptsForServiceFolder(serviceFolder);

            if (allScriptsForServiceFolder.isEmpty()) {
                System.out.println("There are no scripts for service: '" + serviceFolder + "'. Skipping...");
                continue;
            }

            if (!migration.checkAllScriptsAreNumberedOrSchemanticVersioned(allScriptsForServiceFolder)) {
                continue;
            }
            allScriptsForServiceFolder.sort(new NumberVersionsComparator());
            allScriptsForServiceFolder.sort(new SemanticVersionsComparator());

            //get current version for service
            //execute all new scripts
            // write newest version

        }
    }

}
