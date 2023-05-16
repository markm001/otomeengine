package com.ccat.core;

import com.ccat.core.listener.KeyListener;
import com.ccat.core.listener.MouseListener;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public final class WindowManager {
    private static final WindowManager INSTANCE = new WindowManager();
    private final int width = 1920;
    private final int height = 1080;
    private final String title = "Otome Engine";
    private long window;
    private GLFWErrorCallback errorCallback;

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

        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 2);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 0);

        window = glfwCreateWindow(
                width, height, title,
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
    }

    private void loop() {
        while(!glfwWindowShouldClose(window)) {
            glClearColor(0.3f, 0.4f, 0.5f, 1);
            glClear(GL_COLOR_BUFFER_BIT);

            glfwSwapBuffers(window);
            glfwPollEvents();

            if(KeyListener.isKeyPressed(GLFW_KEY_ESCAPE)) {
                closeWindow();
            }
        }
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
}
