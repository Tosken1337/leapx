#version 330

layout(location = 0) in vec3 in_position;
layout(location = 1) in vec2 in_texCoord;

uniform mat4 viewProjMatrix;
uniform vec4 offsetScale;

out vec2 texCoord;

void main(void) {
    // Scale and offset texture coordinates to index the spritemap
    texCoord = in_texCoord * offsetScale.zw + offsetScale.xy;
    gl_Position = viewProjMatrix * vec4(in_position, 1);
}