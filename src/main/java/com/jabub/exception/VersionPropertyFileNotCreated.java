package com.jabub.exception;

import lombok.Getter;

@Getter
public class VersionPropertyFileNotCreated extends Exception {

    String versionPropertyFileName;

    public VersionPropertyFileNotCreated(String versionPropertyFileName) {
        this.versionPropertyFileName = versionPropertyFileName;
    }
}
