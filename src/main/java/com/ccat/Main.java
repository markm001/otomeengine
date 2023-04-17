package com.ccat;

import com.ccat.core.Window;
import org.lwjgl.Version;

import static org.lwjgl.glfw.GLFW.glfwGetTime;
import static org.lwjgl.glfw.GLFW.glfwTerminate;


public class Main {
    public static void main(String[] args) {
        System.out.println(Version.getVersion());

        Window window = new Window();
        window.init();

        while(!window.windowShouldClose()) {
            window.update();
            double time = glfwGetTime();
        }

        window.clean();
        glfwTerminate();
        window.getErrorCallback().free();
    }
}