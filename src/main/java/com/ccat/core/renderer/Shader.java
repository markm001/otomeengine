package com.ccat.core.renderer;

import com.ccat.core.model.ShaderType;
import com.ccat.core.util.FileReaderUtil;

import java.io.IOException;

import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL20.*;

public class Shader {
    private final int shaderId;

    public Shader(ShaderType type, String filepath) {
        this.shaderId = compile(type, filepath);
    }

    /**
     * Compiles the Shader from Type and filepath provided
     *
     * @param type Type of Shader to compile
     * @param filepath Filepath to the Shader file
     * @return Id of the Shader-Object
     */
    private int compile(ShaderType type, String filepath) {
        try {
            String shaderSource = FileReaderUtil.readFile(filepath);

            int shaderId = glCreateShader(type.getGlType());
            glShaderSource(shaderId, shaderSource);
            glCompileShader(shaderId);

            //Check Errors:
            if(glGetShaderi(shaderId, GL_COMPILE_STATUS) == GL_FALSE) {
                String infoLog = glGetShaderInfoLog(shaderId);
                glDeleteShader(shaderId);

                throw new RuntimeException(infoLog);
            }

            return shaderId;

        } catch (IOException e) {
            throw new RuntimeException("Failed to read Shader File-Path: " + filepath);
        }
    }

    /** @return Id of the Shader */
    public int getShaderId() {
        return shaderId;
    }

    /** Deletes the Shader Object */
    public void destroy() {
        glDeleteShader(shaderId);
    }
}
