#version 330

in vec2 texCoord;

uniform sampler2D texImage;

out vec4 out_color;

void main(void) {
    vec4 textureColor = texture(texImage, texCoord);
    out_color = textureColor;
}