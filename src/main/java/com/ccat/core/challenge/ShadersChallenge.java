package com.ccat.core.challenge;

import com.ccat.core.renderer.ShaderProgram;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;
import static org.lwjgl.opengl.GL45.*;

public class ShadersChallenge extends SimpleChallenge{
    private final ShaderProgram shaderProgram;
    private final int FLOAT_SIZE = Float.BYTES;

    private int vao;
    private int vbo;
    private int ebo;

    private final float[] squareVertexArray = new float[] {
            -0.5f,  0.5f, 0f, //Top Left        | 0
             0.5f,  0.5f, 0f, //Top Right       | 1
             0.5f, -0.5f, 0f, //Bottom Right    | 2
            -0.5f, -0.5f, 0f, //Bottom Left     | 3
    };
    private final int[] squareElementArray = new int[] {
            2, 1, 0,
            2, 0, 3
    };

    public ShadersChallenge(ShaderProgram shaderProgram, int windowWidth, int windowHeight) {
        this.shaderProgram = shaderProgram;
        initializeProjection(windowWidth, windowHeight);
        initializeCamera();
    }

    /**
     * Uploads the Projection Matrix to the Vertex Shader
     *
     * @param windowWidth Window width
     * @param windowHeight Window height
     */
    private void initializeProjection(int windowWidth, int windowHeight) {
        float fov = 45f;
        float zNear = 0.01f;
        float zFar = 10000f;
        float aspect = (float) windowWidth / (float) windowHeight;

        Matrix4f projection = new Matrix4f();
        projection.perspective(fov, aspect, zNear, zFar);

        shaderProgram.uploadMat4("uProjection", projection);
    }

    /**
     * Uploads the View Matrix to the Vertex Shader
     */
    private void initializeCamera() {
        Vector3f cameraPos = new Vector3f(0f, -5f, 10f);
        Vector3f cameraLookAt = new Vector3f(0f, 0f, 0f);
        Vector3f up = new Vector3f(0f, 1f, 0f);

        Matrix4f view = new Matrix4f().lookAt(cameraPos, cameraLookAt, up);

        shaderProgram.uploadMat4("uView", view);
    }


    private void initChallenge1() {
        int vertexBindingPoint = 0;
        int positionSize = 3;

        this.vao = glGenVertexArrays();
        glBindVertexArray(vao);

        try(MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer vertexBuffer = stack
                    .mallocFloat(squareVertexArray.length)
                    .put(squareVertexArray)
                    .flip();

            this.vbo = glGenBuffers();
            glNamedBufferData(vbo, vertexBuffer, GL_STATIC_DRAW);
            glVertexArrayVertexBuffer(vao, vertexBindingPoint, vbo, 0, positionSize * FLOAT_SIZE);

            IntBuffer elementBuffer = stack
                    .mallocInt(squareElementArray.length)
                    .put(squareElementArray)
                    .flip();

            this.ebo = glGenBuffers();
            glNamedBufferData(ebo, elementBuffer, GL_STATIC_DRAW);
            glVertexArrayElementBuffer(vao, ebo);
        }

        int positionAttribSlot = 0;
        glVertexArrayAttribFormat(vao, positionAttribSlot, positionSize, GL_FLOAT, false, 0);
        glVertexArrayAttribBinding(vao, positionAttribSlot, vertexBindingPoint);
        glEnableVertexArrayAttrib(vao, positionAttribSlot);
    }

    private void drawChallenge1(Vector3f position) {
        float rotation = 0.0f;

        Matrix4f transform = new Matrix4f()
                .scale(1.0f)
                .rotate(rotation, 0f, 0f, 1f)
                .translate(position);

        shaderProgram.uploadMat4("uTransform", transform);

        glDrawElements(GL_TRIANGLES,
                squareElementArray.length,
                GL_UNSIGNED_INT,
                0
        );
    }

    @Override
    public void drawCurrentChallenge(Vector3f position) {
        glBindVertexArray(vao);

        switch (challengeIndex) {
            case 1 -> drawChallenge1(position);
        }
    }

    @Override
    public void drawCurrentChallenge() { }

    @Override
    void disposeCurrentChallenge() {
        glDeleteVertexArrays(vao);
        glDeleteBuffers(vbo);
        glDeleteBuffers(ebo);

        glDisableVertexAttribArray(0);
        glBindVertexArray(0);
    }

    @Override
    void initNewChallenge() {
        switch (challengeIndex) {
            case 1 -> initChallenge1();
        }
    }
}
