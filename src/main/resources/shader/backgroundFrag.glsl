#version 330

in vec2 texCoord;

uniform sampler2D texImage;
uniform sampler2D texImage2;
uniform float time;
uniform float screenAspect;

out vec4 out_color;

void main(void) {
    vec4 textureColorScroll = texture(texImage, texCoord * vec2(4, 4 / screenAspect) + vec2(0, time * 0.001));
    vec4 baseColor = texture(texImage2, texCoord * vec2(1, 1 / screenAspect) + vec2(0, time * 0.0001));
    vec4 backgroundColor = mix(baseColor, textureColorScroll, 0.9);
    out_color = vec4(backgroundColor.rgb, 1);
}