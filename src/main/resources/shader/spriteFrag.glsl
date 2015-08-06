#version 330
in vec2 texCoord;

uniform sampler2D texImage;

out vec4 out_color;

void main(void) {
    out_color = texture(texImage, texCoord);
    //out_color = vec4(1,0, 0,1);
}