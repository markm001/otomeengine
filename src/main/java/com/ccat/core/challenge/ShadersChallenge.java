package com.ccat.core.challenge;

import com.ccat.core.WindowManager;
import com.ccat.core.listener.KeyListener;
import com.ccat.core.model.UniformType;
import com.ccat.core.renderer.ShaderProgram;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;
import static org.lwjgl.opengl.GL45.*;

public class ShadersChallenge extends SimpleChallenge{
    private final ShaderProgram shaderProgram;
    private final int FLOAT_SIZE = Float.BYTES;
    private Vector3f squarePos = new Vector3f(0.5f, 0.5f, 0f);

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

    public ShadersChallenge(WindowManager window) {
        //Initialize Shader
        final String vertexShaderFilepath = "shaders/vertex/vertex_shader_base.glsl";
        final String fragmentShaderFilepath = "shaders/fragment/fragment_shader_base.glsl";
        this.shaderProgram = new ShaderProgram(vertexShaderFilepath, fragmentShaderFilepath);
        shaderProgram.bind();

        initializeProjection(window.getWidth(), window.getHeight());
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

        shaderProgram.uploadMat4(UniformType.PROJECTION.getName(), projection);
    }

    /**
     * Uploads the View Matrix to the Vertex Shader
     * (can be used in update())
     */
    private void initializeCamera() {
        Vector3f cameraPos = new Vector3f(0f, 0f, 10f);
        Vector3f cameraLookAt = new Vector3f(0f, 0f, 0f);
        Vector3f up = new Vector3f(0f, 1f, 0f);

        Matrix4f view = new Matrix4f().lookAt(cameraPos, cameraLookAt, up);

        shaderProgram.uploadMat4(UniformType.VIEW.getName(), view);
    }


    public void initializeSquare() {
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

    public void update(float delta) {
        shaderProgram.bind();

        moveSquare(delta);

        float rotation = 0.0f;
        Matrix4f transform = new Matrix4f()
                .scale(1f)
                .rotate(rotation, 0f, 0f, 1f)
                .translate(squarePos);
        shaderProgram.uploadMat4(UniformType.TRANSFORM.getName(), transform);

        glBindVertexArray(vao);
        glDrawElements(GL_TRIANGLES,
                squareElementArray.length,
                GL_UNSIGNED_INT,
                0
        );
    }

    private void moveSquare(float delta) {
        float moveSpeed = 10f;

        if(KeyListener.isKeyPressed(GLFW_KEY_W)) {
            squarePos.y += delta * moveSpeed;
        }
        else if(KeyListener.isKeyPressed(GLFW_KEY_S)) {
            squarePos.y -= delta * moveSpeed;
        }
        if(KeyListener.isKeyPressed(GLFW_KEY_D)) {
            squarePos.x += delta * moveSpeed;
        }
        if(KeyListener.isKeyPressed(GLFW_KEY_A)) {
            squarePos.x -= delta * moveSpeed;
        }
    }

    @Override
    public void drawCurrentChallenge(Vector3f position) { }

    @Override
    public void drawCurrentChallenge() { }

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

    @Override
    void initNewChallenge() { }
}
