package com.ccat.core;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class CameraController {

    private final Matrix4f projection;
    private Matrix4f view;
    private final Vector3f UP = new Vector3f(0f,1f,0f);


    public CameraController(Vector3f cameraPos, float width, float height) {
        float zNear = 0.01f;
        float zFar = 10000f;
        float fov = 45f;
        float aspect = width / height;

        this.projection = new Matrix4f()
                .perspective(fov, aspect, zNear, zFar);

        Vector3f center = new Vector3f(0f,0f,0f);

        this.view = new Matrix4f().lookAt(cameraPos, center, UP);
    }

    public Matrix4f updateCameraPos(Vector3f cameraPos) {
        Vector3f center= new Vector3f(0f,0f,0f);
        this.view = view.identity().lookAt(cameraPos, center, UP);

        return view;
    }

    public Matrix4f getProjection() {
        return projection;
    }

    public Matrix4f getView() {
        return view;
    }
}
