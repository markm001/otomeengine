package com.ccat;

import com.ccat.core.WindowManager;
import com.ccat.core.challenge.ShaderChallenge;
import com.ccat.core.challenge.TerrainChallenge;
import com.ccat.core.listener.KeyListener;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

public class Main {
    private static GLFWErrorCallback errorCallback;
    private static final int WIDTH = 1920;
    private static final int HEIGHT = 1080;
    private static final String TITLE = "Otome Engine";

    private static WindowManager window;

    public static void main(String[] args) {
        System.out.println("LWJGL Version " + Version.getVersion() + " is working.");

        errorCallback = GLFWErrorCallback.createPrint(System.err);
        glfwSetErrorCallback(errorCallback);

        if(!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW.");
        }

        //Initialize Window
        window = WindowManager.getInstance(WIDTH, HEIGHT, TITLE);
        glEnable(GL_DEPTH_TEST);

        loop();

        destroy();
    }

    /** Main Program */
    private static void loop() {
        float delta;
        float frameStart = 0f;
        float keyDebounce = 0f;

//        MovingSquareChallenge challenge = new MovingSquareChallenge(window);
//        challenge.initializeQuad();

//        ShaderChallenge shaderChallenge = new ShaderChallenge(window);
//        shaderChallenge.initNewChallenge();

        TerrainChallenge terrainChallenge = new TerrainChallenge(window);
        terrainChallenge.initNewChallenge();

        while(!glfwWindowShouldClose(window.getWindow())) {
            delta = (float) glfwGetTime() - frameStart;
            frameStart = (float) glfwGetTime();
            keyDebounce -= delta;

            glClearColor(0.3f, 0.4f, 0.5f, 1);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

//            challenge.update(delta);
//            shaderChallenge.drawCurrentChallenge();
            terrainChallenge.update(delta);

            input(keyDebounce);

            glfwSwapBuffers(window.getWindow());
            glfwPollEvents();
        }

//        challenge.disposeCurrentChallenge();
//        shaderChallenge.disposeCurrentChallenge();
        terrainChallenge.disposeCurrentChallenge();
    }

    /** Perform clean-up */
    private static void destroy() {
        window.dispose();

        glfwTerminate();
        errorCallback.free();
    }

    /**
     * Check Input
     * @param debounce
     */
    private static void input(float debounce) {
        if(KeyListener.isKeyPressed(GLFW_KEY_F1) && debounce < 0) {
            window.toggleFullScreen();
        }
        else if(KeyListener.isKeyPressed(GLFW_KEY_ESCAPE)) {
            window.closeWindow();
        }
    }
}
