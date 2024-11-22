package com.jabub;

import java.util.regex.Pattern;

import static java.lang.System.getenv;

public enum EnvVar {
    GITHUB_REPO_LOCAL_FOLDER("C:\\Users\\mt2560\\IdeaProjects\\jabub-test-repo"),
    GITHUB_REPO_REMOTE_URL("https://github.com/tlachy/jabub-test-repo.git"),
    MIGRATION_DIRECTORY("MIGRATION"),
    MIGRATION_OUTPUT_DIRECTORY("MIGRATION_AUDIT"),
    SEMANTIC_VERSION_PREFIX("v"),
    VERSION_FILE_NAME("version.properties"),
    SEMANTIC_VERSION_FILE_REGEX("^v\\d+\\.\\d+\\.\\d+_.+$"),
    NUMBERED_VERSION_FILE_REGEX("^\\d+(\\.\\d+)?_.+$");


    private final String defaultValue;


    EnvVar(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    @Override
    public String toString() {
        return getenv(this.name()) == null ? this.defaultValue : getenv(this.name());
    }
}
