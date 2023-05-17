package com.ccat.core.util;

public class ShapeUtil {
    public static float[] getSquareVertexArray() {
        return new float[] {
                //color                 //position
                1f, 1f, 0f, 1f,         -0.5f,  0.5f, 0f,   //Top Left      | 0 | Y
                1f, 0f, 0f, 1f,          0.5f,  0.5f, 0f,   //Top Right     | 1 | R
                0f, 1f, 0f, 1f,         -0.5f, -0.5f, 0f,   //Bottom Left   | 2 | G
                0f, 0f, 1f, 1f,          0.5f, -0.5f, 0f    //Bottom Right  | 3 | B
        };
    }
    public static int[] getSquareElementArray() {
        return new int[] {
                3, 0, 2,
                3, 1, 0
        };
    }

    public static float[] getTwoTrianglesVertexArray() {
        return new float[] {
                //color             // position
                1f, 0f, 0f, 1f,     -0.5f,  0.5f, 0f,
                0f, 1f, 0f, 1f,     -0.5f, -0.5f, 0f,
                1f, 1f, 0f, 1f,      0.5f, -0.5f, 0f,

                1f, 0f, 0f, 1f,     -0.5f,  0.5f, 0f,
                1f, 1f, 0f, 1f,      0.5f, -0.5f, 0f,
                0f, 0f, 1f, 1f,      0.5f, 0.5f, 0f
        };
    }
}
