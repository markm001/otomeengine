package com.ccat.core.challenge;

import org.joml.Vector3f;

public abstract class SimpleChallenge {
    protected final int FLOAT_SIZE = Float.BYTES;
    protected int challengeIndex = -1;

    protected final float[] squareVertexArray = new float[] {
            -0.5f,  0.5f, 0f, //Top Left        | 0
            0.5f,  0.5f, 0f, //Top Right       | 1
            0.5f, -0.5f, 0f, //Bottom Right    | 2
            -0.5f, -0.5f, 0f, //Bottom Left     | 3
    };

    protected final int[] squareElementArray = new int[] {
            2, 1, 0,
            2, 0, 3
    };

    public abstract void drawCurrentChallenge();

    abstract void disposeCurrentChallenge();
    abstract void initNewChallenge();

    public void setChallengeIndex(int challengeIndex) {
        disposeCurrentChallenge();
        this.challengeIndex = challengeIndex;
        initNewChallenge();
    }
}
