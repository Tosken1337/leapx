#version 330

in vec2 texCoord;

uniform sampler2D texImage;
uniform bool isEvading;
uniform float time;

out vec4 out_color;

void main(void) {
    vec4 textureColor = texture(texImage, texCoord);
    if (isEvading) {
        textureColor *= vec4(sin(time * 0.01) * 10, 0.6, 0.6, 1);
    }
    out_color = textureColor;
}