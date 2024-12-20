package com.jabub;

import static java.lang.System.getenv;

public enum EnvVar {
    // It will be work like this? I think that user must add their repo?
    GITHUB_REPO_LOCAL_FOLDER("/Users/samueldobsa/IdeaProjects/jabub-test-repo-second"),
    GITHUB_REPO_REMOTE_URL("https://github.com/samueldobsa/jabub-test-repo-second.git"),
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
