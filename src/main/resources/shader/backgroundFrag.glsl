#version 330

in vec2 texCoord;

uniform sampler2D texImage;
uniform float time;
uniform float screenAspect;

out vec4 out_color;

void main(void) {
    vec4 textureColor = texture(texImage, texCoord * vec2(4, 4 / screenAspect) + vec2(0, time * 0.001));
    out_color = vec4(textureColor.rgb, 1);
}