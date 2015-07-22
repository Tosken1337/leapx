#version 330
in vec2 texCoord;

uniform sampler2D texImage;

out vec4 out_color;

float random(vec3 scale,float seed){return fract(sin(dot(gl_FragCoord.xyz+seed,scale))*43758.5453+seed);}

void main(void) {
    float amount = 500;
    vec2 resolution = vec2(1920, 1080);
    out_color = texture(texImage, texCoord);

    // Sepia
    /*vec4 color = texture2D(texImage, texCoord);
    float r = color.r;
    float g = color.g;
    float b = color.b;

    color.r = min(1.0, (r * (1.0 - (0.607 * amount))) + (g * (0.769 * amount)) + (b * (0.189 * amount)));
    color.g = min(1.0, (r * 0.349 * amount) + (g * (1.0 - (0.314 * amount))) + (b * 0.168 * amount));
    color.b = min(1.0, (r * 0.272 * amount) + (g * 0.534 * amount) + (b * (1.0 - (0.869 * amount))));
    out_color = color;*/


    // Pixelate
    /*float d = 1.0 / amount;
    float ar = resolution.x / resolution.y;
    float u = floor(texCoord.x / d) * d;
    d = ar / amount;
    float v = floor(texCoord.y / d) * d;
    out_color = texture2D(texImage, vec2(u,v));*/


    // zoom blur effect
    /*vec4 color=vec4(0.0);
    float total=0.0;
    vec2 center = vec2(1920/2, 1080/2); // center dynamic at player position
    float strength =0.05;
    vec2 toCenter=center-texCoord*resolution;
    float offset=random(vec3(12.9898,78.233,151.7182),0.0);
    for(float t=0.0;t<=40.0;t++){
        float percent=(t+offset)/40.0;
        float weight=4.0*(percent-percent*percent);
        vec4 sam =texture2D(texImage,texCoord+toCenter*percent*strength/resolution);
        sam.rgb*=sam.a;
        color+=sam*weight;
        total+=weight;
    }
    out_color=color/total;
    out_color.rgb/=out_color.a+0.00001;*/

}