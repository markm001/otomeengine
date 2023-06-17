package com.ccat.core.challenge;

import com.ccat.core.WindowManager;
import com.ccat.core.model.UniformType;
import com.ccat.core.renderer.ShaderProgram;
import com.ccat.core.renderer.TextureLoader;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.LinkedList;

import static org.lwjgl.glfw.GLFW.glfwGetTime;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;
import static org.lwjgl.opengl.GL45.*;

public class TexturesChallenge extends SimpleChallenge {
    private final ShaderProgram shaderProgram;
    private float[] vertexArray = new float[]{
            // pos                      texCoords(u,v)
            -0.5f, -0.5f, -0.5f,        0.0f, 0.0f,     //bottom-left
             0.5f, -0.5f, -0.5f,        1.0f, 0.0f,     //bottom-right
            -0.5f,  0.5f, -0.5f,        0.0f, 1.0f,     //top-left
             0.5f,  0.5f, -0.5f,        1.0f, 1.0f,      //top-right

            -0.5f, -0.5f, 0.5f,         0.0f, 0.0f,     //bottom-left
             0.5f, -0.5f, 0.5f,         1.0f, 0.0f,     //bottom-right
            -0.5f,  0.5f, 0.5f,         0.0f, 1.0f,     //top-left
             0.5f,  0.5f, 0.5f,         1.0f, 1.0f      //top-right
    };
    private final int[] elementArray = new int[]{
            1, 3, 2,    1, 2, 0,    //BACK
            5, 7, 6,    5, 6, 4,    //FRONT
            7, 3, 2,    7, 2, 6,    //TOP
            5, 1, 0,    5, 0, 4,    //BOTTOM
            4, 6, 2,    4, 2, 0,    //LEFT
            5, 7, 3,    5, 3, 1     //RIGHT
    };

    private void generateCube() {
        Vector3f[] verts = {
                new Vector3f(-0.5f, -0.5f, -0.5f),
                new Vector3f(0.5f, -0.5f, -0.5f),
                new Vector3f(-0.5f,  0.5f, -0.5f),
                new Vector3f(0.5f,  0.5f, -0.5f),

                new Vector3f(-0.5f, -0.5f, 0.5f),
                new Vector3f(0.5f, -0.5f, 0.5f),
                new Vector3f(-0.5f,  0.5f, 0.5f),
                new Vector3f(0.5f,  0.5f, 0.5f)
        };

        Vector2f[] texCoords = {
                new Vector2f(1.0f, 0.0f),
                new Vector2f(1.0f, 1.0f),
                new Vector2f(0.0f, 1.0f),

                new Vector2f(1.0f, 0.0f),
                new Vector2f(0.0f, 1.0f),
                new Vector2f(0.0f, 0.0f),
        };

        LinkedList<Float> floats = new LinkedList<>();

        for (int i = 0; i < elementArray.length; i++) {
            Vector3f vertices = verts[elementArray[i]];
            Vector2f texCoord = texCoords[i % 6];

            floats.add(vertices.x);
            floats.add(vertices.y);
            floats.add(vertices.z);

            floats.add(texCoord.x);
            floats.add(texCoord.y);
        }

        float[] floatArray = new float[floats.size()];
        for (int i = 0; i < floats.size(); i++) {
            floatArray[i] = floats.get(i);
        }

        this.vertexArray = floatArray;
    }

    private final TextureLoader texture;
    private int vao;
    private int vbo;
    private int ebo;

    public TexturesChallenge(WindowManager window, TextureLoader texture) {
        generateCube();

        this.texture = texture;

        final String vertexShaderFilepath = "shaders/vertex/vertex_shader_texture.glsl";
        final String fragmentShaderFilepath = "shaders/fragment/texture_fragment_shader.glsl";
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

    private void initializeCube() {
        int positionSize = 3;
        int textureSize = 2;
        int vertexSize = (positionSize + textureSize) * FLOAT_SIZE;
        int vertexBindingPoint = 0;

        this.vao = glGenVertexArrays();
        glBindVertexArray(vao);

        try (MemoryStack stack = MemoryStack.stackPush()) {

            this.vbo = glGenBuffers();
            FloatBuffer vertexBuffer = stack
                    .mallocFloat(vertexArray.length)
                    .put(vertexArray)
                    .flip();

            glNamedBufferData(vbo, vertexBuffer, GL_STATIC_DRAW);
            glVertexArrayVertexBuffer(vao, vertexBindingPoint, vbo, 0, vertexSize);

            this.ebo = glGenBuffers();
            IntBuffer elementBuffer = stack
                    .mallocInt(elementArray.length)
                    .put(elementArray)
                    .flip();

            glNamedBufferData(ebo, elementBuffer, GL_STATIC_DRAW);
            glVertexArrayElementBuffer(vao, ebo);
        }

        int positionAttribSlot = 0;
        glVertexArrayAttribFormat(vao, positionAttribSlot, positionSize, GL_FLOAT, false, 0);
        glVertexArrayAttribBinding(vao, positionAttribSlot, vertexBindingPoint);
        glEnableVertexArrayAttrib(vao, positionAttribSlot);

        int textureAttribSlot = 1;
        glVertexArrayAttribFormat(vao, textureAttribSlot, textureSize, GL_FLOAT, false, positionSize * FLOAT_SIZE);
        glVertexArrayAttribBinding(vao, textureAttribSlot, vertexBindingPoint);
        glEnableVertexArrayAttrib(vao, textureAttribSlot);
    }


    @Override
    public void initNewChallenge() {
        initializeCube();
    }

    @Override
    public void drawCurrentChallenge() { }

    public void drawCube(Vector3f position) {
        shaderProgram.bind();


        //Transformation
        Matrix4f transform = new Matrix4f()
                .scale(1f)
                .rotate(0f, 0f, 0f, 0f)
                .translate(position);
        shaderProgram.uploadMat4(UniformType.TRANSFORM.getName(), transform);


        //Camera
        float radius = 10.0f;
        float camX = (float) (Math.sin(glfwGetTime()) * radius);
        float camZ = (float) (Math.cos(glfwGetTime()) * radius);

        Vector3f cameraPos = new Vector3f(camX, 4f, camZ);
        Vector3f cameraLookAt = new Vector3f(0.0f, 0.0f, 0.0f);
        Vector3f up = new Vector3f(0.0f, 1.0f, 0.0f);

        Matrix4f view = new Matrix4f().lookAt(cameraPos, cameraLookAt, up);

        shaderProgram.uploadMat4(UniformType.VIEW.getName(), view);


//        Texture
        int textureSlot = 0;
        texture.bind(textureSlot);

        glBindVertexArray(vao);
        glDrawArrays(GL_TRIANGLES, 0, vertexArray.length);
    }

    @Override
    public void disposeCurrentChallenge() {
        glDeleteVertexArrays(vao);
        glDeleteBuffers(vbo);
        glDeleteBuffers(ebo);

        glDisableVertexAttribArray(0);
        glBindVertexArray(0);

        shaderProgram.unbind();
        texture.unbind();
        shaderProgram.destroy();
        texture.destroy();
    }
}
