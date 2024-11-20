package com.jabub;

import static java.lang.System.getenv;

public enum EnvVar {
    GITHUB_REPO_LOCAL_FOLDER("C:\\Users\\mt2560\\IdeaProjects\\jabub-test-repo"),
    GITHUB_REPO_REMOTE_URL("https://github.com/tlachy/jabub.git");


    private final String defaultValue;

    EnvVar(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    @Override
    public String toString() {
        return getenv(this.name()) == null ? this.defaultValue : getenv(this.name());
    }
}
