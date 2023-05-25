package com.ccat.core.challenge;

import org.joml.Vector3f;

public abstract class SimpleChallenge {
    protected final int floatSize = Float.BYTES;
    protected int challengeIndex = -1;

    public abstract void drawCurrentChallenge();

    public abstract void drawCurrentChallenge(Vector3f position);

    abstract void disposeCurrentChallenge();
    abstract void initNewChallenge();

    public void setChallengeIndex(int challengeIndex) {
        disposeCurrentChallenge();
        this.challengeIndex = challengeIndex;
        initNewChallenge();
    }
}
