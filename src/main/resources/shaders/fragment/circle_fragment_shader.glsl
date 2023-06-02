#version 460 core

precision mediump float;

layout(location = 0) in vec2 fragCoord;

out vec4 FragColor;

uniform vec2 uResolution;
uniform vec2 uMouse;
uniform float uTime;

float circle(in vec2 _st, in float _radius){
    vec2 dist = _st-vec2(0.5);
    return 1.-smoothstep(_radius-(_radius*0.01),
    _radius+(_radius*0.01),
    dot(dist,dist)*4.0);
}

void main(){
    vec2 st = fragCoord.xy/uResolution.xy;
    vec3 color = vec3(0.0);

    vec2 translate = vec2(cos(uTime),sin(uTime));
    st += translate*0.25;

    color += vec3(circle(st,0.15));

    FragColor = vec4(color,1.0);
}