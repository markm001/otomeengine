#version 460 core
layout (location = 0) in vec3 aPosition;

layout (location = 0) out vec3 fragCoord;
layout (location = 1) out vec3 fNormal;

layout (location = 0) uniform mat4 uProjection;
uniform mat4 uTransform;
uniform mat4 uView;
uniform float uTime;

const vec2 uResolution = vec2(5.0);

float random (in vec2 st) {
    return fract(sin(dot(st.xy,
    vec2(12.9898,78.233)))*
    43758.5453123);
}

float noise (in vec2 st) {
    vec2 i = floor(st);
    vec2 f = fract(st);

    // Four corners in 2D of a tile
    float a = random(i);
    float b = random(i + vec2(1.0, 0.0));
    float c = random(i + vec2(0.0, 1.0));
    float d = random(i + vec2(1.0, 1.0));

    vec2 u = f * f * (3.0 - 2.0 * f);

    return mix(a, b, u.x) +
    (c - a)* u.y * (1.0 - u.x) +
    (d - b) * u.x * u.y;
}

    #define OCTAVES 6
float fbm (in vec2 st) {
    // Initial values
    float value = 0.0;
    float amplitude = 1.5;
    float frequency = 0.05;
    //
    // Loop of octaves
    for (int i = 0; i < OCTAVES; i++) {
        value += amplitude * noise(st);
        st *= 2.;
        amplitude *= .5;
    }
    return value;
}

float height(in vec2 _st) {
    return fbm(_st) * 2.0;
}

void main() {
    fragCoord = vec3(aPosition.xz + vec2(0.5), 0.0);
    vec2 st = fragCoord.xy/uResolution.xy;
    st.x *= uResolution.x/uResolution.y;
    st += vec2(uTime/3.0);

    vec4 myHeight = vec4(0, height(st), 0, 0);
    float frontHeight = height(st + vec2(0, 1));
    float backHeight = height(st - vec2(0, 1));
    float leftHeight = height(st - vec2(1, 0));
    float rightHeight = height(st + vec2(1, 0));
    fNormal = normalize(vec3(leftHeight - rightHeight, 2.0, frontHeight - backHeight));

    gl_Position = uProjection * uView * (uTransform * vec4(aPosition, 1.0));
    fragCoord = aPosition + myHeight.xyz;
}