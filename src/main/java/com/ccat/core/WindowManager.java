package com.ccat.core;

import com.ccat.core.listener.KeyListener;
import com.ccat.core.listener.MouseListener;
import com.ccat.core.renderer.Shader;
import com.ccat.core.util.ShapeUtil;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;
import static org.lwjgl.system.MemoryUtil.NULL;

public final class WindowManager {
    private static final WindowManager INSTANCE = new WindowManager();
    private final String TITLE = "Otome Engine";
    private final int width = 1920;
    private final int height = 1080;
    private long window;
    private GLFWErrorCallback errorCallback;

    private Shader shader;
    private int vao;

    private WindowManager() { }

    public static WindowManager getInstance() {
        return INSTANCE;
    }

    public void create() {
        init();
        loop();
        dispose();
    }

    private void init() {
        errorCallback = GLFWErrorCallback.createPrint(System.err);
        glfwSetErrorCallback(errorCallback);

        if(!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW.");
        }

        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 2);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GLFW_TRUE);

        window = glfwCreateWindow(
                width, height, TITLE,
                glfwGetPrimaryMonitor(),
                NULL);
        if(window == NULL) {
            glfwTerminate();
            throw new RuntimeException("Failed to create the GLFW window");
        }

        //Register Callbacks:
        MouseListener mouseListener = MouseListener.getInstance();
        glfwSetKeyCallback(window, KeyListener.keyCallback);
        glfwSetCursorPosCallback(window, mouseListener.cursorCallback);
        glfwSetMouseButtonCallback(window, mouseListener.mouseCallback);
        glfwSetScrollCallback(window, mouseListener.scrollCallback);

        glfwMakeContextCurrent(window);
        GL.createCapabilities();
    }

    private void loop() {
        initChallenge1();

        while(!glfwWindowShouldClose(window)) {
//            glClear(GL_COLOR_BUFFER_BIT);
//            glClearColor(0.3f, 0.4f, 0.5f, 1);

            drawChallenge1();


            glfwSwapBuffers(window);
            glfwPollEvents();

            if(KeyListener.isKeyPressed(GLFW_KEY_ESCAPE)) {
                closeWindow();
            }
        }
    }

    private void initChallenge1() {
        this.shader = new Shader();
        shader.compile();

        // Generate VAO, VBO
        this.vao = glGenVertexArrays();
        glBindVertexArray(vao);

        try(MemoryStack stack = MemoryStack.stackPush()) {
            float[] twoTrisArray = ShapeUtil.getTwoTrianglesVertexArray();
            FloatBuffer vertexBuffer = stack.mallocFloat(twoTrisArray.length);
            vertexBuffer.put(twoTrisArray);
            vertexBuffer.flip();


            int vbo = glGenBuffers();
            glBindBuffer(GL_ARRAY_BUFFER, vbo);
            glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);
        }

        int positionSize = 3;
        int colorSize = 4;
        int floatSize = Float.BYTES;
        int vertexSizeBytes = (colorSize + positionSize) * floatSize;


        int colAttrib = glGetAttribLocation(shader.getProgramId(), "aColor");
        glEnableVertexAttribArray(colAttrib);
        glVertexAttribPointer(
                0,
                colorSize,
                GL_FLOAT,
                false,
                vertexSizeBytes,
                0
        );

        int posAttrib = glGetAttribLocation(shader.getProgramId(), "aPosition");
        glEnableVertexAttribArray(posAttrib);
        glVertexAttribPointer(
                1,
                positionSize,
                GL_FLOAT,
                false,
                vertexSizeBytes,
                colorSize * floatSize
        );
    }
    private void drawChallenge1() {
        shader.bind();

        glBindVertexArray(vao);
        glDrawArrays(GL_TRIANGLES, 0, 6);

        //Unbind
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glBindVertexArray(0);

        shader.unbind();
    }

    private void testInputs() {

        if(KeyListener.isKeyPressed(GLFW_KEY_F1)) {
            toggleFullScreen();
        }
        if(MouseListener.getInstance().getXPos() > 50) {
            System.out.println("Mouse position is greater than 50");
        }
    }

    private void dispose() {
        glfwDestroyWindow(window);
        glfwTerminate();
        errorCallback.free();
    }

    public void toggleFullScreen() {
        glfwSetWindowMonitor(
                window,
                glfwGetWindowMonitor(window) == NULL ? glfwGetPrimaryMonitor() : NULL,
                0, 0, width, height, 1);
    }

    public void closeWindow() {
        glfwSetWindowShouldClose(window, true);
    }

    public long getWindow() {
        return window;
    }
}
