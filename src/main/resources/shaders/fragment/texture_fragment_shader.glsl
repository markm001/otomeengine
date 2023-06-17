#version 460 core

out vec4 FragColor;

layout (location = 0) in vec3 fragCoord;
in vec2 fTexCoords;

uniform sampler2D uTexture;

void main() {
    FragColor = texture(uTexture, fTexCoords);
//    FragColor = vec4(fTexCoords.xy, 0.0,1.0);
}