package com.ccat.core.util;

public class ShapeUtil {
    public static float[] getSquareOutlineArray() {
        return new float[] {
                //color                 //position
                1f, 1f, 1f, 1f,         -0.5f,  0.5f, 0f,   //Top Left      | 0
                1f, 1f, 1f, 1f,          0.5f,  0.5f, 0f,   //Top Right     | 1
                1f, 1f, 1f, 1f,         -0.5f, -0.5f, 0f,   //Bottom Left   | 2
                1f, 1f, 1f, 1f,          0.5f, -0.5f, 0f    //Bottom Right  | 3
        };
    }
    public static int[] getSquareOutlineElementArray() {
        return new int[] {
                3, 1,   1, 0,
                0, 2,   2, 3,
                3, 0
        };
    }
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

    public static float[] getStarVertexArray() {
        return new float[] {
                //color         //position
                0f,0f,1f,1f,     0.2f,  0.1f, 0f, // 1 - Bottom Right   | 0
                0f,1f,0f,1f,     0.0f,  0.5f, 0f, // 1 - Top            | 1
                1f,0f,1f,1f,    -0.2f,  0.1f, 0f, // 1 - Bottom Left    | 2

                1f,0f,0f,1f,    -0.5f, -0.5f, 0f, // 2 - Bottom Left    | 3
                1f,1f,0f,1f,     0.5f,  0.1f, 0f, // 2 - Top Right      | 4
                1f,0f,1f,1f,    -0.2f,  0.1f, 0f, // 2 - Top Left       | 5

                0f,0f,1f,1f,     0.5f, -0.5f, 0f, // 3 - Bottom Right   | 6
                1f,0f,0f,1f,     0.2f,  0.1f, 0f, // 3 - Top Right      | 7
                1f,1f,0f,1f,    -0.5f,  0.1f, 0f  // 3 - Top Left       | 8
        };
    }
    public static int[] getStarElementArray() {
        return new int[] {
                7, 1, 5,
                3, 4, 5,
                6, 7, 8
        };
    }
}
