package com.ccat.core.challenge;

import com.ccat.core.WindowManager;
import com.ccat.core.model.UniformType;
import com.ccat.core.renderer.ShaderProgram;
import org.joml.Matrix4f;
import org.joml.Vector2f;
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

public class ShaderChallenge extends SimpleChallenge {
    private int vao;
    private int vbo;
    private int ebo;
    private final ShaderProgram program;

    public ShaderChallenge(WindowManager window) {
        final String vertexShaderFilepath = "shaders/vertex/vertex_shader_base.glsl";
        final String fragmentShaderFilepath = "shaders/fragment/circle_fragment_shader.glsl";
        this.program = new ShaderProgram(vertexShaderFilepath, fragmentShaderFilepath);

        //todo: for each ShaderProgram, initialize:
        program.bind();
        initializeProjection(program, window.getWidth(), window.getHeight());
        initializeCamera(program);
    }

    private void initializeProjection(ShaderProgram program, int width, int height) {
        float fov = 45f;
        float aspect = (float) width / (float) height;
        float zNear = 0.01f;
        float zFar = 10000f;

        Matrix4f projection = new Matrix4f()
                .perspective(fov, aspect, zNear, zFar);
        program.uploadMat4(UniformType.PROJECTION.getName(), projection);
    }

    private void initializeCamera(ShaderProgram program) {
        Vector3f eye = new Vector3f(0f,0f,2f);
        Vector3f center = new Vector3f();
        Vector3f up = new Vector3f(0f,1f,0f);

        Matrix4f view = new Matrix4f()
                .lookAt(eye, center, up);
        program.uploadMat4(UniformType.VIEW.getName(), view);
    }

    private void initQuad() {
        int bindingIndex = 0;
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
            glVertexArrayVertexBuffer(vao, bindingIndex, vbo, 0, positionSize * FLOAT_SIZE);


            IntBuffer elementBuffer = stack
                    .mallocInt(squareElementArray.length)
                    .put(squareElementArray)
                    .flip();

            this.ebo = glGenBuffers();
            glNamedBufferData(ebo, elementBuffer, GL_STATIC_DRAW);
            glVertexArrayElementBuffer(vao, ebo);
        }

        int positionAttribSlot = 0;
        glVertexArrayAttribFormat(vao, positionAttribSlot, positionSize * FLOAT_SIZE, GL_FLOAT, false, 0);
        glVertexArrayAttribBinding(vao, positionAttribSlot, bindingIndex);
        glEnableVertexArrayAttrib(vao, positionAttribSlot);

    }

    @Override
    public void drawCurrentChallenge() {
        program.bind();

        final Vector3f position = new Vector3f(0f, 0f, 0f);
        Matrix4f transform = new Matrix4f()
                .scale(1f)
                .rotate(0f, 0f, 0f, 0f)
                .translate(position);

        program.uploadMat4(UniformType.TRANSFORM.getName(), transform);
        program.uploadVec2("uResolution", new Vector2f(1f));
        program.uploadFloat("uTime", (float)glfwGetTime());

        glBindVertexArray(vao);
        glDrawElements(GL_TRIANGLES,
                squareElementArray.length,
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

        program.unbind();
        program.destroy();
    }

    @Override
    public void initNewChallenge() {
        initQuad();
    }
}
