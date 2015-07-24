#version 330
uniform mat4 viewProjMatrix;

layout(location = 0) in vec3 in_position;
layout(location = 1) in vec2 in_texCoord;

out vec2 texCoord;

void main(void) {
    texCoord = in_texCoord;
    gl_Position = viewProjMatrix * vec4(in_position, 1);
}