package com.ccat.core.model;

import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;

public enum ShaderType {
    VERTEX(GL_VERTEX_SHADER),
    FRAGMENT(GL_FRAGMENT_SHADER);

    private final int glType;

    ShaderType(int glType) {
        this.glType = glType;
    }

    public int getGlType() {
        return glType;
    }
}
