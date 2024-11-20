package com.jabub;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.PullCommand;
import org.eclipse.jgit.api.PullResult;
import org.eclipse.jgit.api.errors.GitAPIException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import static com.jabub.EnvVar.GITHUB_REPO_LOCAL_FOLDER;
import static com.jabub.EnvVar.GITHUB_REPO_REMOTE_URL;
import static com.jabub.NumberVersionsComparator.isHigher;

public class Application {


    public static void main(String[] args) throws GitAPIException, IOException {

        Application application = new Application();
        application.cloneOrUpdateGithubRepo();


        File[] allServiceFolders = Utils.getAllServiceFolders();

        for (File serviceFolder : allServiceFolders) {
            Migration migration = new Migration(serviceFolder);
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

    private void cloneOrUpdateGithubRepo() {

        File localRepo = new File(GITHUB_REPO_LOCAL_FOLDER.toString());
        if (localRepo.exists()) {

            try (Git gitRepo = Git.open(localRepo)) {

                PullCommand pull = gitRepo.pull();
                PullResult call = pull.call();

                if (!call.isSuccessful()) {
                    throw new RuntimeException("Unable to update github repo");
                }

            } catch (IOException | GitAPIException e) {
                throw new RuntimeException(e);
            }
        } else {

            try (Git call = Git.cloneRepository()
                    .setURI(GITHUB_REPO_REMOTE_URL.toString())
                    .setDirectory(localRepo)
                    .call()) {

                System.out.println(call.describe());

            } catch (GitAPIException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
