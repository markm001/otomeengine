package com.ccat.core.challenge;

import com.ccat.core.WindowManager;
import com.ccat.core.model.UniformType;
import com.ccat.core.renderer.ShaderProgram;
import com.ccat.core.renderer.TextureLoader;
import com.ccat.core.util.FileReaderUtil;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.system.MemoryStack;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;
import static org.lwjgl.opengl.GL45.*;

public class TexturedPlane extends SimpleChallenge {
    private final float[] vertexArray = new float[]{
            // pos                      texCoords(u,v)
            -0.5f, -0.5f, 0.0f,         0.0f, 0.0f,     //bottom-left
             0.5f, -0.5f, 0.0f,         1.0f, 0.0f,     //bottom-right
            -0.5f,  0.5f, 0.0f,         0.0f, 1.0f,     //top-left
             0.5f,  0.5f, 0.0f,         1.0f, 1.0f      //top-right
    };

    private final int[] elementArray = new int[]{
            1, 3, 2,
            1, 2, 0
    };

    private final ShaderProgram shaderProgram;

    private TextureLoader texture;
    private int vao;
    private int vbo;
    private int ebo;

    public TexturedPlane(WindowManager window) {
        final String vertexShaderFilepath = "shaders/vertex/vertex_shader_texture.glsl";
        final String fragmentShaderFilepath = "shaders/fragment/texture_fragment_shader.glsl";
        this.shaderProgram = new ShaderProgram(vertexShaderFilepath, fragmentShaderFilepath);

        shaderProgram.bind();

        float fov = 45f;
        float zNear = 0.01f;
        float zFar = 10000f;
        float aspect = (float) window.getWidth() / (float) window.getHeight();

        Matrix4f projection = new Matrix4f().perspective(fov, aspect, zNear, zFar);
        shaderProgram.uploadMat4(UniformType.PROJECTION.getName(), projection);
    }

    @Override
    public void initNewChallenge() {
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


        //Texture
//        String filepath = "textures/test_texture_04.jpg";
        String filepath = "textures/test_texture_03.png";
//        String filepath = "textures/test_texture_02.jpg";
//        String filepath = "textures/Test_Texture_01.png";
        try {
            this.texture = new TextureLoader(FileReaderUtil.readImage(filepath), GL_RGB, GL_LINEAR);
        } catch (IOException e) {
            throw new RuntimeException("Unable to load Texture image from path:" + filepath);
        }
    }

    @Override
    public void drawCurrentChallenge() {
        shaderProgram.bind();
        //Transformation
        Matrix4f transform = new Matrix4f()
                .scale(1f)
                .rotate(0f, 0f, 0f, 0f)
                .translate(0f,0f,0f);
        shaderProgram.uploadMat4(UniformType.TRANSFORM.getName(), transform);

        //Camera:
        Vector3f eye = new Vector3f(0f, 0f, 2f);
        Vector3f center = new Vector3f(0f, 0f, 0f);
        Vector3f up = new Vector3f(0f, 1f, 0f);
        Matrix4f view = new Matrix4f().lookAt(eye, center, up);
        shaderProgram.uploadMat4(UniformType.VIEW.getName(), view);

        //Texture
        int textureSlot = 0;
        texture.bind(textureSlot);

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
        texture.unbind();
        shaderProgram.destroy();
        texture.destroy();
    }
}
