#extension GL_OES_EGL_image_external : require

precision mediump float;

uniform samplerExternalOES iChannel0;
varying vec2 texCoord;

void main() {
    vec4 color = texture2D(iChannel0, texCoord);
    float newR = abs(color.r + color.g * 2.0 - color.b) * color.r;
    float newG = abs(color.r + color.b * 2.0 - color.g) * color.r;
    float newB = abs(color.r + color.b * 2.0 - color.g) * color.g;
    gl_FragColor = vec4(newR, newG, newB, 1.0f);
 }