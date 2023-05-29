package com.ccat.core.model;

public enum UniformType {
    VIEW("uView"),
    PROJECTION("uProjection"),
    TRANSFORM("uTransform");

    private final String name;

    UniformType(String uniformName) {
        this.name = uniformName;
    }

    public String getName() {
        return name;
    }
}
