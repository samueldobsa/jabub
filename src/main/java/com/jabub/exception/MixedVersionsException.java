package com.jabub.exception;

import lombok.Getter;

import java.util.List;

@Getter
public class MixedVersionsException extends Exception {

    List<String> semanticVersioned;
    List<String> numberedVersioned;

    public MixedVersionsException(List<String> semanticVersioned, List<String> numberedVersioned) {
        this.semanticVersioned = semanticVersioned;
        this.numberedVersioned = numberedVersioned;
    }


}
