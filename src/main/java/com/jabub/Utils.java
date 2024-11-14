package com.jabub;

import java.io.File;
import java.nio.file.Path;

public class Utils {

    public static final String GIT_REPO_PATH = "C:\\Users\\mt2560\\IdeaProjects\\jabub-test-repo";
    public static final String MIGRATION_DIRECTORY = "\\MIGRATION";

    public static File[] getAllServiceFolders() {
        return new File(GIT_REPO_PATH + MIGRATION_DIRECTORY).listFiles(File::isDirectory);
    }

    String getVersionString(Path path){

    }
}
