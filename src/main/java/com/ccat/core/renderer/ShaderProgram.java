package com.ccat.core.renderer;

import com.ccat.core.model.ShaderType;
import org.joml.*;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
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

    public ShaderProgram() {
        final String defaultVertexFilepath = "shaders/vertex/vertex_shader_default.glsl";
        final String defaultFragmentFilepath = "shaders/fragment/fragment_shader_default.glsl";

        Shader vertexShader = new Shader(ShaderType.VERTEX, defaultVertexFilepath);
        Shader fragmentShader = new Shader(ShaderType.FRAGMENT, defaultFragmentFilepath);

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

        compileUniformLocations();
    }

    /**
     * Creates a Map of Shader Program Uniforms and their layout locations
     */
    private void compileUniformLocations() {
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
        uniformLocations.clear();
        glDeleteProgram(0);
        program = -1;
    }

    /** @return Shader Program-Id */
    public int getProgram() {
        return program;
    }

    /**
     * Uploads a Vector4 Uniform to the Shader Object
     *
     * @param var Uniform name
     * @param vec4 Value to upload
     */
    public void uploadVec4(String var, Vector4f vec4) {
        Integer location = uniformLocations.get(var);
        glUniform4f(location, vec4.x, vec4.y, vec4.z, vec4.w);
    }

    public void uploadVec3(String var, Vector3f vec3) {
        Integer location = uniformLocations.get(var);
        glUniform3f(location, vec3.x, vec3.y, vec3.z);
    }

    public void uploadVec2(String var, Vector2f vec2) {
        Integer location = uniformLocations.get(var);
        glUniform2f(location, vec2.x, vec2.y);
    }

    public void uploadIVec4(String var, Vector4i vec4) {
        Integer location = uniformLocations.get(var);
        glUniform4i(location, vec4.x, vec4.y, vec4.z, vec4.w);
    }

    public void uploadIVec3(String var, Vector3i vec3) {
        Integer location = uniformLocations.get(var);
        glUniform3i(location, vec3.x, vec3.y, vec3.z);
    }

    public void uploadIVec2(String var, Vector2i vec2) {
        Integer location = uniformLocations.get(var);
        glUniform2i(location, vec2.x, vec2.y);
    }

    public void uploadFloat(String var, float val) {
        Integer location = uniformLocations.get(var);
        glUniform1f(location, val);
    }

    public void uploadInt(String var, Integer val) {
        Integer location = uniformLocations.get(var);
        glUniform1i(location, val);
    }

    public void uploadIntArray(String var, int[] array) {
        Integer location = uniformLocations.get(var);
        glUniform1iv(location, array);
    }

    public void uploadBool(String var, boolean val ) {
        Integer location = uniformLocations.get(var);
        glUniform1i(location, val ? 1 : 0);
    }

    public void uploadMat4(String var, Matrix4f val) {
        Integer location = uniformLocations.get(var);

        FloatBuffer buffer = MemoryUtil.memAllocFloat(16);
        val.get(buffer);

        glUniformMatrix4fv(location, false, buffer);
    }

    public void uploadMat3(String var, Matrix3f val) {
        Integer location = uniformLocations.get(var);

        FloatBuffer buffer = MemoryUtil.memAllocFloat(9);
        val.get(buffer);

        glUniformMatrix3fv(location, false, buffer);
    }
}
