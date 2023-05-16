package com.ccat.core.listener;

import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;
import org.lwjgl.glfw.GLFWScrollCallback;

import static org.lwjgl.glfw.GLFW.*;

public class MouseListener {
    private static final MouseListener INSTANCE = new MouseListener();
    private static final boolean[] mouseButtonPressed = new boolean[GLFW_MOUSE_BUTTON_LAST];
    private float xPos = 0f;
    private float yPos = 0f;
    private float xScroll = 0f;
    private float yScroll = 0f;

    private MouseListener() { }

    public static MouseListener getInstance() {
        return INSTANCE;
    }

    public final GLFWCursorPosCallback cursorCallback = new GLFWCursorPosCallback() {
        @Override
        public void invoke(long window, double xpos, double ypos) {
            xPos = (float) xpos;
            yPos = (float) ypos;
        }
    };

    public final GLFWMouseButtonCallback mouseCallback = new GLFWMouseButtonCallback() {
        @Override
        public void invoke(long window, int button, int action, int mods) {
            if(button > mouseButtonPressed.length)
                throw new IndexOutOfBoundsException("Invalid MouseButton pressed.");

            if(action == GLFW_PRESS) {
                mouseButtonPressed[button] = true;
            } else if(action == GLFW_RELEASE) {
                mouseButtonPressed[button] = false;
            }
        }
    };

    public final GLFWScrollCallback scrollCallback = new GLFWScrollCallback() {
        @Override
        public void invoke(long window, double xoffset, double yoffset) {
            xScroll = (float) xoffset;
            yScroll = (float) yoffset;
        }
    };

    public float getXPos() {
        return xPos;
    }

    public float getYPos() {
        return yPos;
    }

    public float getXScroll() {
        return xScroll;
    }

    public float getYScroll() {
        return yScroll;
    }
}
