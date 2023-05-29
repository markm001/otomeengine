package com.ccat.core;

import com.ccat.core.listener.KeyListener;
import com.ccat.core.listener.MouseListener;
import org.lwjgl.opengl.GL;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public final class WindowManager {
    private static WindowManager instance;
    private final String title;
    private final int width;
    private final int height;
    private long window;

    private WindowManager(int width, int height, String title) {
        this.width = width;
        this.height = height;
        this.title = title;

        init();
    }

    /**
     * Creates the WindowManager Instance with the specified params
     * @param width Window width
     * @param height Window height
     * @param title Window display title
     * @return A new or existing Instance of the Window Manager Class, if one already exists.
     */
    public static WindowManager getInstance(int width, int height, String title) {
        if(instance == null) {
            instance = new WindowManager(width, height, title);
        }
        return instance;
    }

    /**
     * Creates a WindowManager Instance with default params
     * @return A new default or existing WindowManager Instance
     */
    public static WindowManager getInstance() {
        if(instance == null) {
            instance = new WindowManager(1920, 1080, "OpenGL Window");
        }
        return instance;
    }

    /**
     * Initializes and creates the glfwWindow, registers necessary InputListener Callbacks
     */
    public void init() {
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 4);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 5);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GLFW_TRUE);
        glfwWindowHint(GLFW_OPENGL_DEBUG_CONTEXT, GLFW_TRUE);
        glfwWindowHint(GLFW_CONTEXT_DEBUG, GLFW_TRUE);

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

    public void dispose() {
        glfwDestroyWindow(window);
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

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
