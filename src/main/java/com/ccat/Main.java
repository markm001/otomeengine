package com.ccat;

import com.ccat.core.WindowManager;
import org.lwjgl.Version;

public class Main {
    public static void main(String[] args) {
        System.out.println("LWJGL Version " + Version.getVersion() + " is working.");

        WindowManager window = WindowManager.getInstance();
        window.create();
    }
}
