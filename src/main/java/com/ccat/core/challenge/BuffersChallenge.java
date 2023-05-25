package com.ccat.core.challenge;

import com.ccat.core.renderer.ShaderProgram;
import com.ccat.core.util.ShapeUtil;
import org.joml.Vector3f;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_LINES;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glDrawArrays;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL15.glDeleteBuffers;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL45.*;

public class BuffersChallenge extends SimpleChallenge{
    private final ShaderProgram shaderProgram;

    public BuffersChallenge(ShaderProgram shaderProgram) {
        this.shaderProgram = shaderProgram;
    }

    /**
     * Challenge 1
     */
    private int vao1;
    private int vbo1;
    private void initChallenge1() {
        // Generate VAO, VBO
        this.vao1 = glGenVertexArrays();
        glBindVertexArray(vao1);

        try(MemoryStack stack = MemoryStack.stackPush()) {
            float[] twoTrisArray = ShapeUtil.getTwoTrianglesVertexArray();
            FloatBuffer vertexBuffer = stack.mallocFloat(twoTrisArray.length);
            vertexBuffer.put(twoTrisArray);
            vertexBuffer.flip();


            this.vbo1 = glGenBuffers();
            glBindBuffer(GL_ARRAY_BUFFER, vbo1);
            glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);
        }

        int positionSize = 3;
        int colorSize = 4;
        int vertexSizeBytes = (colorSize + positionSize) * floatSize;


        int colAttrib = glGetAttribLocation(shaderProgram.getProgram(), "aColor");
        glEnableVertexAttribArray(colAttrib);
        glVertexAttribPointer(
                0,
                colorSize,
                GL_FLOAT,
                false,
                vertexSizeBytes,
                0
        );

        int posAttrib = glGetAttribLocation(shaderProgram.getProgram(), "aPosition");
        glEnableVertexAttribArray(posAttrib);
        glVertexAttribPointer(
                1,
                positionSize,
                GL_FLOAT,
                false,
                vertexSizeBytes,
                colorSize * floatSize
        );
    }
    private void drawChallenge1() {
        glBindVertexArray(vao1);
        glDrawArrays(GL_TRIANGLES, 0, 6);
    }
    private void disposeChallenge1() {
        glDeleteVertexArrays(vao1);
        glDeleteBuffers(vbo1);

        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glBindVertexArray(0);
    }

    /**
     * Challenge 2
     */
    private int vao2;
    private int vbo2;
    private int ebo2;
    private void initChallenge2() {
        float[] vertexArray = ShapeUtil.getSquareVertexArray();
        this.vao2 = glGenVertexArrays();
        glBindVertexArray(vao2);

        try(MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer vertices = stack.mallocFloat(vertexArray.length);
            vertices.put(vertexArray);
            vertices.flip();

            this.vbo2 = glGenBuffers();
            glBindBuffer(GL_ARRAY_BUFFER, vbo2);
            glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);

            this.ebo2 = glGenBuffers();
            int[] elementArray = ShapeUtil.getSquareElementArray();
            IntBuffer elementBuffer = stack.mallocInt(elementArray.length);
            elementBuffer.put(elementArray);
            elementBuffer.flip();

            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo2);
            glBufferData(GL_ELEMENT_ARRAY_BUFFER, elementBuffer, GL_STATIC_DRAW);

            int positionSize = 3;
            int colorSize = 4;
            int vertexSize = (colorSize + positionSize) * floatSize;

            glVertexAttribPointer(0, colorSize, GL_FLOAT, false, vertexSize,0);
            glEnableVertexAttribArray(0);

            glVertexAttribPointer(1, positionSize, GL_FLOAT, false, vertexSize, colorSize * floatSize);
            glEnableVertexAttribArray(1);
        }
    }
    private void drawChallenge2() {
        glBindVertexArray(vao2);
        glDrawElements(
                GL_TRIANGLES,
                ShapeUtil.getSquareElementArray().length,
                GL_UNSIGNED_INT,
                0
        );
    }
    private void disposeChallenge2() {
        glDeleteVertexArrays(vao2);
        glDeleteBuffers(vbo2);
        glDeleteBuffers(ebo2);

        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glBindVertexArray(0);
    }

    /**
     * Challenge3
     */
    private int vao3;
    private int vbo3;
    private int ebo3;
    private void initChallenge3() {
        float[] vertexArray = ShapeUtil.getStarVertexArray();
        int[] elementArray = ShapeUtil.getStarElementArray();

        this.vao3 = glGenVertexArrays();
        glBindVertexArray(vao3);

        try(MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer vertices = stack.mallocFloat(vertexArray.length);
            vertices.put(vertexArray).flip();

            this.vbo3 = glGenBuffers();
            glBindBuffer(GL_ARRAY_BUFFER, vbo3);
            glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);

            IntBuffer elementBuffer = stack.mallocInt(elementArray.length);
            elementBuffer.put(elementArray).flip();

            this.ebo3 = glGenBuffers();
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo3);
            glBufferData(GL_ELEMENT_ARRAY_BUFFER, elementBuffer, GL_STATIC_DRAW);

            int positionSize = 3;
            int colorSize = 4;
            int vertexSize = (positionSize + colorSize) * floatSize;

            glVertexAttribPointer(0, colorSize, GL_FLOAT,false, vertexSize, 0);
            glEnableVertexAttribArray(0);
            glVertexAttribPointer(1, positionSize, GL_FLOAT,false,vertexSize, colorSize * floatSize);
            glEnableVertexAttribArray(1);
        }
    }
    private void drawChallenge3() {
        glBindVertexArray(vao3);
        glDrawElements(GL_TRIANGLES,
                ShapeUtil.getStarElementArray().length,
                GL_UNSIGNED_INT,
                0
        );
    }
    private void disposeChallenge3() {
        glDeleteVertexArrays(vao3);
        glDeleteBuffers(vbo3);
        glDeleteBuffers(ebo3);

        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glBindVertexArray(0);
    }

    /**
     * Challenge 4
     */
    private int vao4;
    private int vbo4;
    private int ebo4;
    private void initChallenge4() {
        this.vao4 = glGenVertexArrays();
        glBindVertexArray(vao4);

        float[] vertexArray = ShapeUtil.getSquareOutlineArray();
        int[] elementArray = ShapeUtil.getSquareOutlineElementArray();

        try(MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer vertices = stack.mallocFloat(vertexArray.length);
            vertices.put(vertexArray).flip();


            this.vbo4 = glGenBuffers();
            glBindBuffer(GL_ARRAY_BUFFER, vbo4);
            glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);

            IntBuffer elementBuffer = stack.mallocInt(elementArray.length);
            elementBuffer.put(elementArray).flip();

            this.ebo4 = glGenBuffers();
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo4);
            glBufferData(GL_ELEMENT_ARRAY_BUFFER, elementBuffer, GL_STATIC_DRAW);

            int positionSize = 3;
            int colorSize = 4;
            int vertexSize = (positionSize + colorSize) * floatSize;

            glVertexAttribPointer(0, colorSize, GL_FLOAT,false,colorSize,0);
            glEnableVertexAttribArray(0);
            glVertexAttribPointer(1, positionSize, GL_FLOAT,false,vertexSize,colorSize * floatSize);
            glEnableVertexAttribArray(1);
        }
    }
    private void drawChallenge4() {
        glBindVertexArray(vao4);
        glDrawElements(GL_LINES,10,GL_UNSIGNED_INT,0);
    }
    private void disposeChallenge4() {
        glDeleteVertexArrays(vao4);
        glDeleteBuffers(vbo4);
        glDeleteBuffers(ebo4);

        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glBindVertexArray(0);
    }

    /**
     * Challenge 5
     */
    private int vao5;
    private int vbo5;
    private int ebo5;
    private void initChallenge5() {
        float[] vertexArray = ShapeUtil.getStarVertexArray();
        int[] elementArray = ShapeUtil.getStarElementArray();

        int positionSize = 3;
        int colorSize = 4;
        int vertexSize = (positionSize + colorSize) * floatSize;
        int vertexBindingPoint = 0;

        this.vao5 = glCreateVertexArrays();
        glBindVertexArray(vao5);

        try(MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer vertexBuffer = stack.mallocFloat(vertexArray.length)
                    .put(vertexArray)
                    .flip();

            this.vbo5 = glCreateBuffers();
            glNamedBufferData(vbo5, vertexBuffer, GL_STATIC_DRAW);
            glVertexArrayVertexBuffer(vao5, vertexBindingPoint, vbo5, 0, vertexSize);


            IntBuffer elementBuffer = stack.mallocInt(elementArray.length)
                    .put(elementArray)
                    .flip();

            this.ebo5 = glCreateBuffers();
            glNamedBufferData(ebo5, elementBuffer, GL_STATIC_DRAW);
            glVertexArrayElementBuffer(vao5, ebo5);
        }

        int colorAttribSlot = 0;
        glVertexArrayAttribFormat(vao5, colorAttribSlot, colorSize, GL_FLOAT,false,0);
        glVertexArrayAttribBinding(vao5, colorAttribSlot, vertexBindingPoint);
        glEnableVertexArrayAttrib(vao5, colorAttribSlot);

        int positionAttribSlot = 1;
        glVertexArrayAttribFormat(vao5, positionAttribSlot, positionSize, GL_FLOAT,false,colorSize * floatSize);
        glVertexArrayAttribBinding(vao5, positionAttribSlot, vertexBindingPoint);
        glEnableVertexArrayAttrib(vao5, positionAttribSlot);
    }

    private void drawChallenge5() {
        glBindVertexArray(vao5);
        glDrawElements(GL_TRIANGLES,
                ShapeUtil.getStarElementArray().length,
                GL_UNSIGNED_INT,
                0
        );
    }

    private void disposeChallenge5() {
        glDeleteVertexArrays(vao5);
        glDeleteBuffers(vbo5);
        glDeleteBuffers(ebo5);

        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glBindVertexArray(0);
    }

    @Override
    protected void disposeCurrentChallenge() {
        switch (challengeIndex) {
            case 1 -> disposeChallenge1();
            case 2 -> disposeChallenge2();
            case 3 -> disposeChallenge3();
            case 4 -> disposeChallenge4();
            case 5 -> disposeChallenge5();
        }
    }

    @Override
    public void drawCurrentChallenge() {
        switch (challengeIndex) {
            case 1 -> drawChallenge1();
            case 2 -> drawChallenge2();
            case 3 -> drawChallenge3();
            case 4 -> drawChallenge4();
            case 5 -> drawChallenge5();
            default -> {}
        }
    }

    @Override
    public void drawCurrentChallenge(Vector3f position) { }

    @Override
    protected void initNewChallenge() {
        switch (challengeIndex) {
            case 1 -> initChallenge1();
            case 2 -> initChallenge2();
            case 3 -> initChallenge3();
            case 4 -> initChallenge4();
            case 5 -> initChallenge5();
            default -> {}
        }
    }
}
