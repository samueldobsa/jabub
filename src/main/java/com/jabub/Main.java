package com.jabub;

import org.eclipse.jgit.api.errors.GitAPIException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import static com.jabub.NumberVersionsComparator.isHigher;

public class Main {

    public static void main(String[] args) throws GitAPIException, IOException {

        File[] allServiceFolders = Utils.getAllServiceFolders();
        Migration migration = new Migration();
        for (File serviceFolder : allServiceFolders) {
            List<Path> allScriptsForServiceFolder = migration.getAllScriptsForServiceFolder(serviceFolder);

            if (allScriptsForServiceFolder.isEmpty()) {
                System.out.println("There are no scripts for service: '" + serviceFolder + "'. Skipping...");
                continue;
            }

            if (migration.checkIfContainsBothNumberedAndVersioned(serviceFolder, allScriptsForServiceFolder)) {
                System.out.println("Service folder " + serviceFolder.getName() + " can contain only all scripts numbered or all scripts schemantic version. Combination is not allowed");
                continue;
            }

            if (allScriptsForServiceFolder.getFirst().getFileName().toString().startsWith("v")) {
                allScriptsForServiceFolder.sort(new SemanticVersionsComparator());
            } else {
                allScriptsForServiceFolder.sort(new NumberVersionsComparator());
            }

            String lastRunVersion = "v1.1.1";

            Path lastExecutedScript = null;
            for (Path script : allScriptsForServiceFolder) {
                if (isHigher(script, lastRunVersion)) {
                    migration.executeScript(script);
                    lastExecutedScript = script;
                }
            }
            migration.updateVersion(lastExecutedScript);

        }
    }

}
