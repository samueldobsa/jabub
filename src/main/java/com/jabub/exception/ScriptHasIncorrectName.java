package com.jabub.exception;

import lombok.Getter;

import java.nio.file.Path;

@Getter
public class ScriptHasIncorrectName extends Exception {

    Path script;

    public ScriptHasIncorrectName(Path script) {
        this.script = script;
    }
}
