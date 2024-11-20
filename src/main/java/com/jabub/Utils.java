package com.jabub;

import java.io.File;
import java.lang.reflect.Array;

import static com.jabub.EnvVar.GITHUB_REPO_LOCAL_FOLDER;
import static com.jabub.EnvVar.MIGRATION_DIRECTORY;
import static java.io.File.separator;

public class Utils {


    public static File[] getAllMigrationFolders() {
        return new File(GITHUB_REPO_LOCAL_FOLDER + separator + MIGRATION_DIRECTORY).listFiles(File::isDirectory);
    }

    public static boolean isNullOrEmpty(Object[] array) {
        return array == null || array.length == 0;
    }

}
