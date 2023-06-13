package com.ccat.core.challenge;

import com.ccat.core.WindowManager;
import com.ccat.core.model.UniformType;
import com.ccat.core.renderer.ShaderProgram;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.glfw.GLFW.glfwGetTime;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;
import static org.lwjgl.opengl.GL45.*;

public class TexturesChallenge extends SimpleChallenge {
    private final ShaderProgram shaderProgram;
    private final float[] vertexArray = new float[]{
            0f,0f,0f,
            0f,1f,0f,
            1f,0f,0f,
            1f,1f,0f,

            0f,0f,1f,
            0f,1f,1f,
            1f,0f,1f,
            1f,1f,1f
    };

    private final int[] elementArray = new int[]{
            3,0,2, 3,1,0,   // BACK
            7,5,4, 7,4,6,   // FRONT
            6,4,0, 6,0,2,   // LEFT
            7,3,2, 7,2,6,   // BOTTOM
            7,5,1, 7,1,3,   // RIGHT
            5,1,0, 5,0,4    // TOP

    };

    public TexturesChallenge(WindowManager window) {
        final String vertexShaderFilepath = "shaders/vertex/vertex_shader_texture.glsl";
        final String fragmentShaderFilepath = "shaders/fragment/terrain_fragment_shader.glsl";
        this.shaderProgram = new ShaderProgram(vertexShaderFilepath, fragmentShaderFilepath);

        shaderProgram.bind();
        initializeProjection(window.getWidth(), window.getHeight());
    }

    private void initializeProjection(int windowWidth, int windowHeight) {
        float fov = 45f;
        float zNear = 0.01f;
        float zFar = 10000f;
        float aspect = (float) windowWidth / (float) windowHeight;

        Matrix4f projection = new Matrix4f();
        projection.perspective(fov, aspect, zNear, zFar);

        shaderProgram.uploadMat4(UniformType.PROJECTION.getName(), projection);
    }

    private int vao;
    private int vbo;
    private int ebo;
    private void initializeCube() {
        int positionSize = 3;
        int vertexBindingPoint = 0;

        this.vao = glGenVertexArrays();
        glBindVertexArray(vao);

        try(MemoryStack stack = MemoryStack.stackPush()) {
            this.vbo = glGenBuffers();

            FloatBuffer vertexBuffer = stack
                    .mallocFloat(vertexArray.length)
                    .put(vertexArray)
                    .flip();

            glNamedBufferData(vbo, vertexBuffer, GL_STATIC_DRAW);
            glVertexArrayVertexBuffer(vao, vertexBindingPoint,vbo,0,positionSize*FLOAT_SIZE);


            IntBuffer elementBuffer = stack
                    .mallocInt(elementArray.length)
                    .put(elementArray)
                    .flip();

            this.ebo = glGenBuffers();
            glNamedBufferData(ebo, elementBuffer, GL_STATIC_DRAW);
            glVertexArrayElementBuffer(vao, ebo);
        }

        int positionAttribSlot = 0;
        glVertexArrayAttribFormat(vao,positionAttribSlot,positionSize,GL_FLOAT,false,0);
        glVertexArrayAttribBinding(vao,positionAttribSlot,vertexBindingPoint);
        glEnableVertexArrayAttrib(vao, positionAttribSlot);
    }


    @Override
    public void initNewChallenge() {
        initializeCube();
    }

    @Override
    public void drawCurrentChallenge() {
        shaderProgram.bind();

        final Vector3f position = new Vector3f(-0.5f, -0.5f, -0.5f);
        Matrix4f transform = new Matrix4f()
                .scale(1f)
                .rotate(0f, 0f, 0f, 0f)
                .translate(position);
        shaderProgram.uploadMat4(UniformType.TRANSFORM.getName(), transform);

        float radius = 10.0f;
        float camX = (float) (Math.sin(glfwGetTime()) * radius);
        float camZ = (float) (Math.cos(glfwGetTime()) * radius);

//        Vector3f cameraPos = new Vector3f(0f, 0f, 10f);
        Vector3f cameraPos = new Vector3f(camX, 4f, camZ);
        Vector3f cameraLookAt = new Vector3f(0.0f, 0.0f, 0.0f);
        Vector3f up = new Vector3f(0.0f, 1.0f, 0.0f);

        Matrix4f view = new Matrix4f().lookAt(cameraPos, cameraLookAt, up);

        shaderProgram.uploadMat4(UniformType.VIEW.getName(), view);
        shaderProgram.uploadFloat("uTime", (float) glfwGetTime());

        glBindVertexArray(vao);
        glDrawElements(GL_TRIANGLES,
                elementArray.length,
                GL_UNSIGNED_INT,
                0
        );
    }

    @Override
    public void disposeCurrentChallenge() {
        glDeleteVertexArrays(vao);
        glDeleteBuffers(vbo);
        glDeleteBuffers(ebo);

        glDisableVertexAttribArray(0);
        glBindVertexArray(0);

        shaderProgram.unbind();
        shaderProgram.destroy();
    }
}
