package com.ccat.core.model;

public enum UniformType {
    VIEW("uView"),
    PROJECTION("uProjection"),
    TRANSFORM("uTransform"),
    TEXTURE("uTexture");

    private final String name;

    UniformType(String uniformName) {
        this.name = uniformName;
    }

    public String getName() {
        return name;
    }
}
