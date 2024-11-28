package com.jabub;

import java.io.File;
import java.nio.file.Path;
import java.util.regex.Pattern;

import static com.jabub.EnvVar.*;
import static java.io.File.separator;

public class Utils {

    private static final Pattern numbered = Pattern.compile(SEMANTIC_VERSION_FILE_REGEX.toString());
    private static final Pattern schemantic = Pattern.compile(NUMBERED_VERSION_FILE_REGEX.toString());

    public static File[] getAllMigrationFolders() {
        return new File(GITHUB_REPO_LOCAL_FOLDER + separator + MIGRATION_DIRECTORY).listFiles(File::isDirectory);
    }

    public static boolean isNullOrEmpty(Object[] array) {
        return array == null || array.length == 0;
    }

    public static boolean hasCorrectFormat(String name) {
        return numbered.matcher(name).matches() || schemantic.matcher(name).matches();
    }

    public static File getVersionPropertiesFile(String serviceName) {
        return new File(GITHUB_REPO_LOCAL_FOLDER
                + separator
                + MIGRATION_OUTPUT_DIRECTORY
                + separator
                + serviceName
                + separator
                + VERSION_FILE_NAME);
    }

    public static String extractVersion(Path script) {
        String fileName = script.getFileName().toString();
        if (fileName.startsWith("v")) {
            return fileName.substring(1, fileName.indexOf("_"));
        } else {
            return fileName.substring(0, fileName.indexOf("_"));
        }
    }
}
