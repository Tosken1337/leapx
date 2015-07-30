#version 330
in vec2 texCoord;

uniform sampler2D texImage;

uniform vec2 resolution;
uniform vec2 centerPoint;

out vec4 out_color;

uniform float time; // effect elapsed time

// Swirl effect parameters
uniform float radius = 200.0;
uniform float angle = 1.8;
//uniform vec2 center = vec2(980.0, 540.0);

vec4 PostFX(sampler2D tex, vec2 uv, float time)
{
  vec2 texSize = resolution;
  vec2 tc = texCoord * texSize;
  tc -= centerPoint;
  float dist = length(tc);
  if (dist < radius)
  {
    float percent = (radius - dist) / radius;
    float theta = percent * percent * angle * 8.0;
    float s = sin(theta);
    float c = cos(theta);
    tc = vec2(dot(tc, vec2(c, -s)), dot(tc, vec2(s, c)));
  }
  tc += centerPoint;
  vec3 color = texture2D(tex, tc / texSize).rgb;
  return vec4(color, 1.0);
}

void main (void)
{
  out_color = PostFX(texImage, texCoord, time);
}