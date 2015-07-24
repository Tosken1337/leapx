#version 330
in vec2 texCoord;

uniform sampler2D texImage;
uniform vec2 resolution;

out vec4 out_color;

float random(vec3 scale,float seed){return fract(sin(dot(gl_FragCoord.xyz+seed,scale))*43758.5453+seed);}

void main(void) {
    float amount = 500;

    // Pixelate
    float d = 1.0 / amount;
    float ar = resolution.x / resolution.y;
    float u = floor(texCoord.x / d) * d;
    d = ar / amount;
    float v = floor(texCoord.y / d) * d;
    out_color = texture2D(texImage, vec2(u,v));

}