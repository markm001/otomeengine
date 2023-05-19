package com.ccat.core;

import com.ccat.core.listener.KeyListener;
import com.ccat.core.listener.MouseListener;
import com.ccat.core.renderer.Shader;
import com.ccat.core.util.ShapeUtil;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL45.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public final class WindowManager {
    private static final WindowManager INSTANCE = new WindowManager();
    private final String TITLE = "Otome Engine";
    private final int width = 1920;
    private final int height = 1080;
    private long window;
    private GLFWErrorCallback errorCallback;

    private Shader shader;
    private final int floatSize = Float.BYTES;
    private int challengeIndex = -1;

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
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 4);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 5);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GLFW_TRUE);
        glfwWindowHint(GLFW_OPENGL_DEBUG_CONTEXT, GLFW_TRUE);
        glfwWindowHint(GLFW_CONTEXT_DEBUG, GLFW_TRUE);

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
        this.shader = new Shader();
        shader.compile();
        shader.bind();

        while(!glfwWindowShouldClose(window)) {
            glClearColor(0.3f, 0.4f, 0.5f, 1);
            glClear(GL_COLOR_BUFFER_BIT);

            drawChallenges();

            glfwSwapBuffers(window);
            glfwPollEvents();

            selectChallengeDisplay();

            if(KeyListener.isKeyPressed(GLFW_KEY_ESCAPE)) {
                closeWindow();
            }
        }
    }

    /**
     * Challenge 1
     */
    private int vao1;
    private int vbo1;
    private void initChallenge1() {
        // Generate VAO, VBO
        this.vao1 = glGenVertexArrays();
        glBindVertexArray(vao1);

        try(MemoryStack stack = MemoryStack.stackPush()) {
            float[] twoTrisArray = ShapeUtil.getTwoTrianglesVertexArray();
            FloatBuffer vertexBuffer = stack.mallocFloat(twoTrisArray.length);
            vertexBuffer.put(twoTrisArray);
            vertexBuffer.flip();


            this.vbo1 = glGenBuffers();
            glBindBuffer(GL_ARRAY_BUFFER, vbo1);
            glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);
        }

        int positionSize = 3;
        int colorSize = 4;
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
        glBindVertexArray(vao1);
        glDrawArrays(GL_TRIANGLES, 0, 6);
    }
    private void disposeChallenge1() {
        glDeleteVertexArrays(vao1);
        glDeleteBuffers(vbo1);

        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glBindVertexArray(0);
    }

    /**
     * Challenge 2
     */
    private int vao2;
    private int vbo2;
    private int ebo2;
    private void initChallenge2() {
        float[] vertexArray = ShapeUtil.getSquareVertexArray();
        this.vao2 = glGenVertexArrays();
        glBindVertexArray(vao2);

        try(MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer vertices = stack.mallocFloat(vertexArray.length);
            vertices.put(vertexArray);
            vertices.flip();

            this.vbo2 = glGenBuffers();
            glBindBuffer(GL_ARRAY_BUFFER, vbo2);
            glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);

            this.ebo2 = glGenBuffers();
            int[] elementArray = ShapeUtil.getSquareElementArray();
            IntBuffer elementBuffer = stack.mallocInt(elementArray.length);
            elementBuffer.put(elementArray);
            elementBuffer.flip();

            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo2);
            glBufferData(GL_ELEMENT_ARRAY_BUFFER, elementBuffer, GL_STATIC_DRAW);

            int positionSize = 3;
            int colorSize = 4;
            int vertexSize = (colorSize + positionSize) * floatSize;

            glVertexAttribPointer(0, colorSize, GL_FLOAT, false, vertexSize,0);
            glEnableVertexAttribArray(0);

            glVertexAttribPointer(1, positionSize, GL_FLOAT, false, vertexSize, colorSize * floatSize);
            glEnableVertexAttribArray(1);
        }
    }
    private void drawChallenge2() {
        glBindVertexArray(vao2);
        glDrawElements(
                GL_TRIANGLES,
                ShapeUtil.getSquareElementArray().length,
                GL_UNSIGNED_INT,
                0
        );
    }
    private void disposeChallenge2() {
        glDeleteVertexArrays(vao2);
        glDeleteBuffers(vbo2);
        glDeleteBuffers(ebo2);

        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glBindVertexArray(0);
    }

    /**
     * Challenge3
     */
    private int vao3;
    private int vbo3;
    private int ebo3;
    private void initChallenge3() {
        float[] vertexArray = ShapeUtil.getStarVertexArray();
        int[] elementArray = ShapeUtil.getStarElementArray();

        this.vao3 = glGenVertexArrays();
        glBindVertexArray(vao3);

        try(MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer vertices = stack.mallocFloat(vertexArray.length);
            vertices.put(vertexArray).flip();

            this.vbo3 = glGenBuffers();
            glBindBuffer(GL_ARRAY_BUFFER, vbo3);
            glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);

            IntBuffer elementBuffer = stack.mallocInt(elementArray.length);
            elementBuffer.put(elementArray).flip();

            this.ebo3 = glGenBuffers();
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo3);
            glBufferData(GL_ELEMENT_ARRAY_BUFFER, elementBuffer, GL_STATIC_DRAW);

            int positionSize = 3;
            int colorSize = 4;
            int vertexSize = (positionSize + colorSize) * floatSize;

            glVertexAttribPointer(0, colorSize, GL_FLOAT,false, vertexSize, 0);
            glEnableVertexAttribArray(0);
            glVertexAttribPointer(1, positionSize, GL_FLOAT,false,vertexSize, colorSize * floatSize);
            glEnableVertexAttribArray(1);
        }
    }
    private void drawChallenge3() {
        glBindVertexArray(vao3);
        glDrawElements(GL_TRIANGLES,
                ShapeUtil.getStarElementArray().length,
                GL_UNSIGNED_INT,
                0
                );
    }
    private void disposeChallenge3() {
        glDeleteVertexArrays(vao3);
        glDeleteBuffers(vbo3);
        glDeleteBuffers(ebo3);

        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glBindVertexArray(0);
    }

    /**
     * Challenge 4
     */
    private int vao4;
    private int vbo4;
    private int ebo4;
    private void initChallenge4() {
        this.vao4 = glGenVertexArrays();
        glBindVertexArray(vao4);

        float[] vertexArray = ShapeUtil.getSquareOutlineArray();
        int[] elementArray = ShapeUtil.getSquareOutlineElementArray();

        try(MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer vertices = stack.mallocFloat(vertexArray.length);
            vertices.put(vertexArray).flip();


            this.vbo4 = glGenBuffers();
            glBindBuffer(GL_ARRAY_BUFFER, vbo4);
            glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);

            IntBuffer elementBuffer = stack.mallocInt(elementArray.length);
            elementBuffer.put(elementArray).flip();

            this.ebo4 = glGenBuffers();
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo4);
            glBufferData(GL_ELEMENT_ARRAY_BUFFER, elementBuffer, GL_STATIC_DRAW);

            int positionSize = 3;
            int colorSize = 4;
            int vertexSize = (positionSize + colorSize) * floatSize;

            glVertexAttribPointer(0, colorSize, GL_FLOAT,false,colorSize,0);
            glEnableVertexAttribArray(0);
            glVertexAttribPointer(1, positionSize, GL_FLOAT,false,vertexSize,colorSize * floatSize);
            glEnableVertexAttribArray(1);
        }
    }
    private void drawChallenge4() {
        glBindVertexArray(vao4);
        glDrawElements(GL_LINES,10,GL_UNSIGNED_INT,0);
    }
    private void disposeChallenge4() {
        glDeleteVertexArrays(vao4);
        glDeleteBuffers(vbo4);
        glDeleteBuffers(ebo4);

        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glBindVertexArray(0);
    }

    /**
     * Challenge 5
     */
    private int vao5;
    private int vbo5;
    private int ebo5;
    private void initChallenge5() {
        float[] vertexArray = ShapeUtil.getStarVertexArray();
        int[] elementArray = ShapeUtil.getStarElementArray();

        int positionSize = 3;
        int colorSize = 4;
        int vertexSize = (positionSize + colorSize) * floatSize;
        int vertexBindingPoint = 0;

        this.vao5 = glCreateVertexArrays();
        glBindVertexArray(vao5);

        try(MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer vertexBuffer = stack.mallocFloat(vertexArray.length)
                    .put(vertexArray)
                    .flip();

            this.vbo5 = glCreateBuffers();
            glNamedBufferData(vbo5, vertexBuffer, GL_STATIC_DRAW);
            glVertexArrayVertexBuffer(vao5, vertexBindingPoint, vbo5, 0, vertexSize);


            IntBuffer elementBuffer = stack.mallocInt(elementArray.length)
                    .put(elementArray)
                    .flip();

            this.ebo5 = glCreateBuffers();
            glNamedBufferData(ebo5, elementBuffer, GL_STATIC_DRAW);
            glVertexArrayElementBuffer(vao5, ebo5);
        }

        int colorAttribSlot = 0;
        glVertexArrayAttribFormat(vao5, colorAttribSlot, colorSize, GL_FLOAT,false, colorSize * floatSize);
        glVertexArrayAttribBinding(vao5, colorAttribSlot, vertexBindingPoint);
        glEnableVertexArrayAttrib(vao5, colorAttribSlot);

        int positionAttribSlot = 1;
        glVertexArrayAttribFormat(vao5, positionAttribSlot, positionSize, GL_FLOAT,false, positionSize * floatSize);
        glVertexArrayAttribBinding(vao5, positionAttribSlot, vertexBindingPoint);
        glEnableVertexArrayAttrib(vao5, positionAttribSlot);
    }

    private void drawChallenge5() {
        glBindVertexArray(vao5);
        glDrawElements(GL_TRIANGLES,
                ShapeUtil.getStarElementArray().length,
                GL_UNSIGNED_INT,
                0
        );
    }

    private void disposeChallenge5() {
        glDeleteVertexArrays(vao5);
        glDeleteBuffers(vbo5);
        glDeleteBuffers(ebo5);

        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glBindVertexArray(0);
    }

    private void disposeCurrentChallenge() {
        switch (challengeIndex) {
            case 1 -> disposeChallenge1();
            case 2 -> disposeChallenge2();
            case 3 -> disposeChallenge3();
            case 4 -> disposeChallenge4();
            case 5 -> disposeChallenge5();
        }
    }

    private void drawChallenges() {
        switch (challengeIndex) {
            case 1 -> drawChallenge1();
            case 2 -> drawChallenge2();
            case 3 -> drawChallenge3();
            case 4 -> drawChallenge4();
            case 5 -> drawChallenge5();
            default -> {}
        }
    }

    private void selectChallengeDisplay() {
        if(KeyListener.isKeyPressed(GLFW_KEY_1)) {
            disposeCurrentChallenge();
            challengeIndex = 1;
            initChallenge1();
        } else if(KeyListener.isKeyPressed(GLFW_KEY_2)) {
            disposeCurrentChallenge();
            challengeIndex = 2;
            initChallenge2();
        } else if(KeyListener.isKeyPressed(GLFW_KEY_3)) {
            disposeCurrentChallenge();
            challengeIndex = 3;
            initChallenge3();
        } else if(KeyListener.isKeyPressed(GLFW_KEY_4)) {
            disposeCurrentChallenge();
            challengeIndex = 4;
            initChallenge4();
        } else if(KeyListener.isKeyPressed(GLFW_KEY_5)) {
            disposeCurrentChallenge();
            challengeIndex = 5;
            initChallenge5();
        } else if(KeyListener.isKeyPressed(GLFW_KEY_0)) {
            disposeCurrentChallenge();
            challengeIndex = -1;
        }

        if(KeyListener.isKeyPressed(GLFW_KEY_F1) && debounce) {
            debounce = false;

            new Thread(() -> {
                try {
                    Thread.sleep(400);
                    debounce = true;
                } catch (InterruptedException ignore) { }
            });
            toggleFullScreen();
        }
    }
    private volatile boolean debounce = true;

    private void dispose() {
        shader.unbind();

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
