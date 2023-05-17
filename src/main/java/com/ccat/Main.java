package com.ccat;

import com.ccat.core.WindowManager;
import com.ccat.core.renderer.Shader;
import org.lwjgl.Version;

import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;

public class Main {
    public static void main(String[] args) {
        System.out.println("LWJGL Version " + Version.getVersion() + " is working.");

        WindowManager window = WindowManager.getInstance();
        window.create();
    }
}
