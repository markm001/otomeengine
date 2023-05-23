package com.ccat.core.renderer;

import com.ccat.core.model.ShaderType;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL20.*;

public class ShaderProgram {
    private int program;

    private final Map<String, Integer> uniformLocations = new HashMap<>();

    /**
     * Compiles both Vertex and Fragment Shaders from provided Filepath
     *
     * @param vertexShaderFilepath filepath of the Vertex Shader
     * @param fragmentShaderFilepath filepath of the Fragment Shader
     */
    public ShaderProgram(String vertexShaderFilepath, String fragmentShaderFilepath) {
        Shader vertexShader = new Shader(ShaderType.VERTEX, vertexShaderFilepath);
        Shader fragmentShader = new Shader(ShaderType.FRAGMENT, fragmentShaderFilepath);

        linkShaders(vertexShader, fragmentShader);
    }

    /**
     * Links the Vertex and Fragment Shaders
     *
     * @param vertexShader compiled Vertex Shader
     * @param fragmentShader compiled Fragment Shader
     */
    private void linkShaders(Shader vertexShader, Shader fragmentShader) {
        this.program = glCreateProgram();

        int vertexShaderId = vertexShader.getShaderId();
        int fragmentShaderId = fragmentShader.getShaderId();

        glAttachShader(program, vertexShaderId);
        glAttachShader(program, fragmentShaderId);

        glLinkProgram(program);

        //Check Errors:
        if(glGetProgrami(program, GL_LINK_STATUS) == GL_FALSE) {
            String infoLog = glGetProgramInfoLog(program);

            glDeleteProgram(program);
            glDeleteShader(vertexShaderId);
            glDeleteShader(fragmentShaderId);

            throw new RuntimeException(infoLog);
        }

        glDetachShader(program, vertexShaderId);
        glDetachShader(program, fragmentShaderId);
        vertexShader.destroy();
        fragmentShader.destroy();

        getUniformLocations();
    }

    /**
     * Creates a Map of Shader Program Uniforms and their layout locations
     */
    private void getUniformLocations() {
        int uniformNum = glGetProgrami(program, GL_ACTIVE_UNIFORMS);
        int maxLength = glGetProgrami(program, GL_ACTIVE_UNIFORM_MAX_LENGTH);

        try(MemoryStack stack = MemoryStack.stackPush()) {

            if (uniformNum > 0 && maxLength > 0) {
                for (int i = 0; i < uniformNum; i++) {
                    IntBuffer size = stack.mallocInt(1);
                    IntBuffer type = stack.mallocInt(1);

                    String uniformName = glGetActiveUniform(program, i, maxLength, size, type);
                    int uniformLocation = glGetUniformLocation(program, uniformName);

                    //todo: Debug Output - remove later
                    System.out.printf("Uniform ## Name:%s - Location:%d - Type:%d%n",
                            uniformName, uniformLocation, type.get()
                    );

                    uniformLocations.put(uniformName, uniformLocation);
                }

                System.out.println(uniformLocations);
            }
        }
    }

    /** Binds the Shader Program for use */
    public void bind() {
        glUseProgram(program);
    }

    /** Unbinds the Shader Program */
    public void unbind() {
        glUseProgram(0);
    }

    /** Deletes the Shader Program and sets the Program-Id to -1 */
    public void destroy() {
        glDeleteProgram(0);
        program = -1;
    }

    /** @return Shader Program-Id */
    public int getProgram() {
        return program;
    }
}
