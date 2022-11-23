#extension GL_OES_EGL_image_external : require
precision mediump float;

uniform float iGlobalTime;
uniform samplerExternalOES iChannel0;
varying vec2 texCoord;

void mainImage(out vec4 fragColor, in vec2 fragCoord) {
	float stongth = 0.3;
	vec2 uv = fragCoord.xy;
	float waveu = sin((uv.y + iGlobalTime) * 20.0) * 0.5 * 0.05 * stongth;
	fragColor = texture2D(iChannel0, uv + vec2(waveu, 0));
}

void main() {
	mainImage(gl_FragColor, texCoord);
}