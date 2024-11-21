package com.jabub;

import com.jabub.exception.MixedVersionsException;
import com.jabub.exception.NoScriptsException;
import com.jabub.exception.ScriptHasIncorrectName;
import com.jabub.exception.VersionPropertyFileNotCreated;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.PullCommand;
import org.eclipse.jgit.api.PullResult;
import org.eclipse.jgit.api.errors.GitAPIException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;


import static com.jabub.EnvVar.*;
import static com.jabub.Utils.getAllMigrationFolders;
import static com.jabub.Utils.isNullOrEmpty;
import static java.io.File.separator;

@Slf4j
public class Application {

    public static void main(String[] args) {

        Application application = new Application();
        application.cloneOrUpdateGithubRepo();

        File[] migrationFolders = getAllMigrationFolders();
        if (isNullOrEmpty(migrationFolders)) {
            log.error("Github repo does not contain any folders in '{}' directory. Exiting...", MIGRATION_DIRECTORY);
            return;
        }
        boolean success = application.createMigrationOutputFoldersIfDontExist(migrationFolders);
        if (!success) {
            log.error("Github repo does not contain any folders in '{}' directory. Exiting...", MIGRATION_DIRECTORY);
            return;
        }

        for (File migrationFolder : migrationFolders) {
            Migration migration;
            try {
                migration = new Migration(migrationFolder);
            } catch (IOException e) {
                log.error("Unable to initialize migration for folder: + '{}'. Skipping...", migrationFolder, e);
                continue;
            } catch (NoScriptsException e) {
                log.warn("No scripts for folder: '{}'. Skipping...", migrationFolder);
                continue;
            } catch (MixedVersionsException e) {
                log.error("Folder '{}' can contain only all scripts either with number versioning or schemantic versioning. Skipping...", migrationFolder);
                continue;
            } catch (VersionPropertyFileNotCreated e) {
                log.error("Cannot create version properties file: '{}'", e.getVersionPropertyFileName());
                continue;
            } catch (ScriptHasIncorrectName e) {
                log.error("Script '{}' has incorrect name", e.getScript().getFileName().toAbsolutePath());
                continue;
            }
            log.info("Successfully initiated migration for folder: '{}'", migrationFolder.getName());

            String lastExecutedVersion = migration.getLastExecutedVersion();
            log.info("Last executed version: '{}'", lastExecutedVersion);

            Path lastExecutedScript = null;
            for (Path script : migration.getAllScriptsSorted()) {
                if (migration.isHigher(script, lastExecutedVersion)) {
                    migration.executeScript(script);
                    lastExecutedScript = script;
                } else {
                    log.debug("Skipping script {}", script.getFileName());
                }
            }
            migration.updateVersion(lastExecutedScript);
        }
    }

    private boolean createMigrationOutputFoldersIfDontExist(File[] migrationFolders) {
        log.debug("creating migration output folder if don't exists");
        boolean result = true;
        for (File folder : migrationFolders) {
            boolean success = new File(GITHUB_REPO_LOCAL_FOLDER
                    + separator
                    + MIGRATION_OUTPUT_DIRECTORY
                    +separator
                    + folder.getName()).mkdirs();
            result = result && success;
        }
        return result;
    }


    private void cloneOrUpdateGithubRepo() {

        File localRepo = new File(GITHUB_REPO_LOCAL_FOLDER.toString());
        if (localRepo.exists()) {
            log.debug("Github repo already cloned. Pulling latest changes...");
            try (Git gitRepo = Git.open(localRepo)) {

                PullCommand pull = gitRepo.pull();
                PullResult call = pull.call();

                if (!call.isSuccessful()) {
                    throw new RuntimeException("Unable to update github repo");
                }
                log.debug("Github repo sucessfully updated from:{}", call.getFetchedFrom());

            } catch (IOException | GitAPIException e) {
                throw new RuntimeException(e);
            }
        } else {

            log.debug("Github repo not cloned. Cloning...");

            try (Git call = Git.cloneRepository()
                    .setURI(GITHUB_REPO_REMOTE_URL.toString())
                    .setDirectory(localRepo)
                    .call()) {

                log.debug("Github repo successfully cloned. {}", call.status().toString());//TODO print current version

            } catch (GitAPIException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
