#version 460 core

layout(location = 0) in vec3 aPosition;

uniform mat4 uTransform;
uniform mat4 uProjection;
uniform mat4 uView;

void main() {
    gl_Position = uProjection * uView * (uTransform * vec4(aPosition, 1.0));
}