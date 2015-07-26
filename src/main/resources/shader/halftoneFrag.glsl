#version 330
in vec2 texCoord;

uniform sampler2D texImage;

uniform vec2 resolution;
uniform vec2 focusPoint;

out vec4 out_color;

void main(void) {

	float pixelSize = 5;
	vec2 p = texCoord;

	float pixelsPerRow = resolution.x / pixelSize;
	float pixelsPerCol = resolution.y / pixelSize;

	float pixelSizeX = 1.0 / pixelsPerRow;
	float dx = mod(p.x, pixelSizeX ) - pixelSizeX *0.5;
	float pixelSizeY = 1.0 / pixelsPerCol;
	float dy = mod(p.y, pixelSizeY ) - pixelSizeY * 0.5;
	pixelSize = pixelSizeX;//sqrt( pixelSizeX * pixelSizeX + pixelSizeY + pixelSizeY );

	p.x -= dx;
	p.y -= dy;
	vec3 col = texture2D(texImage, p).rgb;
	vec3 luma = vec3( .299, 0.587, 0.114 );
	float bright = dot( col.rgb, luma );

	float dist = sqrt(dx*dx + dy*dy);
	float rad = bright * pixelSize * 1.;
	float m = step( dist, rad );

	vec3 col2 = mix(vec3(0.0), vec3(1.0), m);
	out_color = vec4(col2, 1.0);

}