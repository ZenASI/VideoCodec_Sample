#extension GL_OES_EGL_image_external : require

precision mediump float;
varying vec2 texCoord;
uniform samplerExternalOES iChannel0;

void main() {
    vec4 color = texture2D(iChannel0, texCoord);
    gl_FragColor = vec4(1.0-color.r, 1.0-color.g, 1.0-color.b, color.a);
}