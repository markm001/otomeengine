package com.ccat.core.listener;

import org.lwjgl.glfw.GLFWKeyCallback;

import static org.lwjgl.glfw.GLFW.*;

public class KeyListener {
    private static final KeyListener INSTANCE = new KeyListener();
    private static final boolean[] keysPressed = new boolean[GLFW_KEY_LAST];
    private KeyListener() { }
    public static KeyListener getInstance() {
        return INSTANCE;
    }

    public static final GLFWKeyCallback keyCallback = new GLFWKeyCallback() {
        @Override
        public void invoke(long window, int key, int scancode, int action, int mods) {
            if(action == GLFW_PRESS) {
                keysPressed[key] = true;
            } else if(action == GLFW_RELEASE) {
                keysPressed[key] = false;
            }
        }
    };

    public static boolean isKeyPressed(int keyCode) {
        if(keyCode > keysPressed.length)
            throw new IndexOutOfBoundsException("Requested key is invalid.");

        return keysPressed[keyCode];
    }
}
