package com.ccat.core.renderer;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Shader {
    private final String vertexShaderSource = """
            #version 460 core

            layout(location = 0) in vec4 aColor;
            layout(location = 1) in vec3 aPosition;

            out vec4 fColor;

            void main() {
                fColor = aColor;
                gl_Position = vec4(aPosition, 1.0);
            }""";

    private final String fragmentShaderSource = """
            #version 460 core

            in vec4 fColor;

            out vec4 FragColor;

            void main() {
                FragColor = fColor;
            }""";

    private int programId;

    public void compile() {
        // -- Vertex Shader --
        int vertexShader = glCreateShader(GL_VERTEX_SHADER);

        glShaderSource(vertexShader, vertexShaderSource);
        glCompileShader(vertexShader);

            //Check Errors:
        if(glGetShaderi(vertexShader, GL_COMPILE_STATUS) == GL_FALSE) {
            throw new RuntimeException(glGetShaderInfoLog(vertexShader));
        }


        // -- Fragment Shader --
        int fragmentShader = glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(fragmentShader, fragmentShaderSource);
        glCompileShader(fragmentShader);

            //Check Error:
        if(glGetShaderi(fragmentShader, GL_COMPILE_STATUS) == GL_FALSE) {
            throw new RuntimeException(glGetShaderInfoLog(fragmentShader));
        }

        // -- Link Shaders --
        this.programId = glCreateProgram();
        glAttachShader(programId, vertexShader);
        glAttachShader(programId, fragmentShader);
        glLinkProgram(programId);

            //Check Error:
        if(glGetProgrami(programId, GL_LINK_STATUS) == GL_FALSE) {
            throw new RuntimeException(glGetProgramInfoLog(programId));
        }

        glDeleteShader(vertexShader);
        glDeleteShader(fragmentShader);
    }

    public void bind() {
        glUseProgram(programId);
    }

    public void unbind() {
        glUseProgram(0);
    }

    public int getProgramId() {
        return programId;
    }
}
