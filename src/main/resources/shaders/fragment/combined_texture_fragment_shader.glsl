#version 460 core

out vec4 FragColor;

layout (location = 0) in vec3 fragCoord;
in vec2 fTexCoords;

uniform sampler2D uTexture1;
uniform sampler2D uTexture2;

void main() {
    FragColor = mix(texture(uTexture1, fTexCoords),texture(uTexture2, fTexCoords), 0.2);
}