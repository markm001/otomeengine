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
import java.util.LinkedList;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glDeleteBuffers;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL45.*;
import static org.lwjgl.opengl.GL45.glEnableVertexArrayAttrib;

public class TerrainChallenge extends SimpleChallenge {
    private final int size = 12;
    private int subdivisions = 10;
    private float[] vertexArray;
    private int[] elementArray;

    private void createGrid() {
        float offset = (size / 2f);

        LinkedList<Float> floats = new LinkedList<>();
        float interval = (float) size / subdivisions;
        for (int y = 0; y <= subdivisions; y++) {
            for (int x = 0; x <= subdivisions; x++) {
                float xPos = x * interval - offset;
                float zPos = y * interval - offset;
                float yPos = -2f;

                floats.add(xPos);
                floats.add(yPos);
                floats.add(zPos);
            }
        }

        Object[] v = floats.toArray();
        int length = v.length;
        float[] vert = new float[length];
        for (int i = 0; i < length; i++) {
            vert[i] = (float) v[i];
        }
        this.vertexArray = vert;

        int[] elements = new int[(subdivisions * subdivisions) * 6];
        int quad = 1;
        int row = 0;
        for (int i = 0; i < (subdivisions * subdivisions); i++) {
            //First Tris
            elements[i * 6] = (row + i + subdivisions + 2);         // 4
            elements[i * 6 + 1] = (row + i);                        // 0
            elements[i * 6 + 2] = (row + i + subdivisions + 1);     // 3

            //Second Tris
            elements[i * 6 + 3] = (row + i + subdivisions + 2);     // 4
            elements[i * 6 + 4] = (row + i + 1);                    // 1
            elements[i * 6 + 5] = (row + i);                        // 0

            if(quad % subdivisions == 0) row++; //next row
            quad++;
        }
        this.elementArray = elements;
    }

    private final ShaderProgram shaderProgram;
    private int vao;
    private int vbo;
    private int ebo;

    public TerrainChallenge(WindowManager window) {
        //Initialize Shader
        final String vertexShaderFilepath = "shaders/vertex/vertex_shader_terrain.glsl";
        final String fragmentShaderFilepath = "shaders/fragment/terrain_fragment_shader.glsl";
        this.shaderProgram = new ShaderProgram(vertexShaderFilepath, fragmentShaderFilepath);

        shaderProgram.bind();
        initializeProjection(window.getWidth(), window.getHeight());
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
     */

    float rotation = 0f;
    float debounce = 0f;
    private void initializeCamera(float delta) {
        debounce -= delta;

        float maxRadius = (size / 2f)* 2f;
//        Vector3f cameraPos = new Vector3f(0f, -8f, 10f);
        rotation += delta * 30.0f;
        double radians = Math.toRadians(rotation);

        Vector3f cameraPos = new Vector3f((float) (maxRadius*Math.sin(radians)), 10f, (float) (maxRadius*Math.cos(radians)));
        Vector3f cameraLookAt = new Vector3f(0.0f, 0.0f, 2.0f);
        Vector3f up = new Vector3f(0.0f, 1.0f, 0.0f);

        Matrix4f view = new Matrix4f().lookAt(cameraPos, cameraLookAt, up);

        shaderProgram.uploadMat4(UniformType.VIEW.getName(), view);
    }

    private void initializeQuad() {
        createGrid();

        int vertexBindingPoint = 0;
        int positionSize = 3;

        this.vao = glGenVertexArrays();
        glBindVertexArray(vao);

        try(MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer vertexBuffer = stack
                    .mallocFloat(vertexArray.length)
                    .put(vertexArray)
                    .flip();

            this.vbo = glGenBuffers();
            glNamedBufferData(vbo, vertexBuffer, GL_STATIC_DRAW);
            glVertexArrayVertexBuffer(vao, vertexBindingPoint, vbo, 0, positionSize * FLOAT_SIZE);

            IntBuffer elementBuffer = stack
                    .mallocInt(elementArray.length)
                    .put(elementArray)
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

        initializeCamera(delta);
        shaderProgram.uploadFloat("uTime", (float) glfwGetTime());

        if(KeyListener.isKeyPressed(GLFW_KEY_RIGHT) && debounce < 0) {
            subdivisions--;
            clearQuad();
            initializeQuad();
            System.out.println("Subdivisions: " + subdivisions);

            debounce = 0.4f;
        }
        if(KeyListener.isKeyPressed(GLFW_KEY_LEFT) && debounce < 0) {
            subdivisions++;
            clearQuad();
            initializeQuad();
            System.out.println("Subdivisions: " + subdivisions);

            debounce = 0.4f;
        }

        glBindVertexArray(vao);
        glDrawElements(GL_TRIANGLES,
                elementArray.length,
                GL_UNSIGNED_INT,
                0
        );
    }

    @Override
    public void drawCurrentChallenge() {

    }

    @Override
    public void disposeCurrentChallenge() {
        clearQuad();
        shaderProgram.unbind();
        shaderProgram.destroy();
    }

    private void clearQuad() {
        glDeleteVertexArrays(vao);
        glDeleteBuffers(vbo);
        glDeleteBuffers(ebo);

        glDisableVertexAttribArray(0);
        glBindVertexArray(0);
    }

    @Override
    public void initNewChallenge() {
        initializeQuad();
    }
}
