#version 330
uniform mat4 viewProjMatrix;

in vec3 in_position;
in vec2 in_texCoord;

out vec2 texCoord;
void main(void) {
    texCoord = in_texCoord;
    gl_Position = viewProjMatrix * vec4(in_position, 1);
}