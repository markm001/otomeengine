package com.ccat.core;

import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.opengl.GL;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11C.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window {
    private final GLFWErrorCallback errorCallback = GLFWErrorCallback.createPrint(System.err);
    private Long window;
    public void init() {
        glfwSetErrorCallback(errorCallback);

        if(!glfwInit()) throw new IllegalStateException("Unable to initialize GLFW");

        window = glfwCreateWindow(640,480,"Example Window", NULL, NULL);

        if(window == NULL) {
            glfwTerminate();
            throw new RuntimeException("Failed to create the GLFW window");
        }

        glfwSetKeyCallback(window, keyCallback);
        glfwMakeContextCurrent(window);
        GL.createCapabilities();

        glClearColor(0f,0f,0f,0f);
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_STENCIL_TEST);
        glEnable(GL_CULL_FACE);
        glEnable(GL_BACK);
    }

    private final GLFWKeyCallback keyCallback = new GLFWKeyCallback() {
        @Override
        public void invoke(long window, int key, int scancode, int action, int mods) {
            if(key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) glfwSetWindowShouldClose(window, true);
        }
    };

    public void update() {
        glfwSwapBuffers(window);
        glfwPollEvents();
    }

    public void clean() {
        glfwDestroyWindow(window);
        keyCallback.free();
    }

    public boolean windowShouldClose() {
        return glfwWindowShouldClose(window);
    }

    public GLFWErrorCallback getErrorCallback() {
        return errorCallback;
    }
}
