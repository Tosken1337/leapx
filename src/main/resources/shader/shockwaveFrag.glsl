#version 330
in vec2 texCoord;

uniform sampler2D texImage;

uniform vec2 resolution;
uniform vec2 centerPoint;

out vec4 out_color;

uniform float time; // effect elapsed time
const vec3 shockParams = vec3(10.0, 0.8, 0.1); // 10.0, 0.8, 0.1

void main(void) {
  vec2 uv = texCoord;
  vec2 texCoord2 = uv;
  vec2 diffUV = vec2(0);
  float diffTime = 0;
  float distance = distance(uv, centerPoint);
  if ( (distance <= (time + shockParams.z)) &&
       (distance >= (time - shockParams.z)) )
  {
    float diff = (distance - time);
    float powDiff = 1.0 - pow(abs(diff*shockParams.x),
                                shockParams.y);
    diffTime = diff  * powDiff;
    diffUV = normalize(uv - centerPoint);
    texCoord2 = uv + (diffUV * diffTime);
  }


  vec4 waveColor = vec4(0.3, 0.3, 1, 0);
  out_color = mix(texture2D(texImage, texCoord2), diffTime * 40 * waveColor , diffTime * 10);
}