package com.ccat.core;

import com.ccat.core.challenge.ShadersChallenge;
import com.ccat.core.challenge.SimpleChallenge;
import com.ccat.core.listener.KeyListener;
import com.ccat.core.listener.MouseListener;
import com.ccat.core.renderer.ShaderProgram;
import org.joml.Math;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;

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
    private ShaderProgram program;

    private SimpleChallenge challenge;

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
//                glfwGetPrimaryMonitor(),
                NULL,
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


        //Initialize Shader
        final String vertexShaderFilepath = "shaders/vertex/vertex_shader_base.glsl";
        final String fragmentShaderFilepath = "shaders/fragment/fragment_shader_base.glsl";

        this.program = new ShaderProgram(vertexShaderFilepath, fragmentShaderFilepath);
        program.bind();
    }

    private void loop() {
        /** Specific Challenge can be initialized here! */
//        this.challenge = new BuffersChallenge(program);
        this.challenge = new ShadersChallenge(program, width, height);

        Vector3f squarePos = new Vector3f(0.5f, 0.5f, 0f);


        Vector3f cameraPos = new Vector3f(0f, -5f, 10f);

        while(!glfwWindowShouldClose(window)) {
            glClearColor(0.3f, 0.4f, 0.5f, 1);
            glClear(GL_COLOR_BUFFER_BIT);

            challenge.drawCurrentChallenge(squarePos);

            glfwSwapBuffers(window);
            glfwPollEvents();

            selectChallengeDisplay();

            if(KeyListener.isKeyPressed(GLFW_KEY_ESCAPE)) {
                closeWindow();
            }

            transformSquarePos(squarePos);
            transformCameraPos(cameraPos);
        }
    }

    private void transformCameraPos(Vector3f cameraPos) {
        Vector3f cameraLookAt = new Vector3f(0f, 0f, 0f);
        Vector3f up = new Vector3f(0f, 1f, 0f);

        if(KeyListener.isKeyPressed(GLFW_KEY_LEFT)) {
            cameraPos.x -= 0.1;
        }
        if(KeyListener.isKeyPressed(GLFW_KEY_RIGHT)) {
            cameraPos.x += 0.1;
        }
        if(KeyListener.isKeyPressed(GLFW_KEY_UP)) {
            cameraPos.y -= 0.1;
        }
        if(KeyListener.isKeyPressed(GLFW_KEY_DOWN)) {
            cameraPos.y += 0.1;
        }
        if(KeyListener.isKeyPressed(GLFW_KEY_KP_ADD)) {
            cameraPos.z -= 0.1;
        }
        if(KeyListener.isKeyPressed(GLFW_KEY_KP_SUBTRACT)) {
            cameraPos.z += 0.1;
        }

        Matrix4f view = new Matrix4f().lookAt(cameraPos, cameraLookAt, up);
        program.uploadMat4("uView", view);

    }

    private void transformSquarePos(Vector3f pos) {

        if(KeyListener.isKeyPressed(GLFW_KEY_W)) {
            pos.y += 0.05;
        }
        if(KeyListener.isKeyPressed(GLFW_KEY_S)) {
            pos.y -= 0.05;
        }
        if(KeyListener.isKeyPressed(GLFW_KEY_D)) {
            pos.x += 0.05;
        }
        if(KeyListener.isKeyPressed(GLFW_KEY_A)) {
            pos.x -= 0.05;
        }

        pos.x = Math.clamp(-10f, 10f, pos.x);
        pos.y = Math.clamp(-10f, 10f, pos.y);
    }

    private void selectChallengeDisplay() {
        if(KeyListener.isKeyPressed(GLFW_KEY_1)) {
            initializeChallenge(1);
        } else if(KeyListener.isKeyPressed(GLFW_KEY_2)) {
            initializeChallenge(2);
        } else if(KeyListener.isKeyPressed(GLFW_KEY_3)) {
            initializeChallenge(3);
        } else if(KeyListener.isKeyPressed(GLFW_KEY_4)) {
            initializeChallenge(4);
        } else if(KeyListener.isKeyPressed(GLFW_KEY_5)) {
            initializeChallenge(5);
        } else if(KeyListener.isKeyPressed(GLFW_KEY_0)) {
            initializeChallenge(6);
        }

        if(KeyListener.isKeyPressed(GLFW_KEY_F1)) {
            toggleFullScreen();
        }
    }

    private void dispose() {
        program.unbind();
        program.destroy();

        glfwDestroyWindow(window);
        glfwTerminate();
        errorCallback.free();
    }

    private void initializeChallenge(int index) {
        challenge.setChallengeIndex(index);
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
