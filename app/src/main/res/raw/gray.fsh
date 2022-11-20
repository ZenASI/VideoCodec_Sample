#extension GL_OES_EGL_image_external : require

precision mediump float;
varying vec2 texCoord;
uniform samplerExternalOES iChannel0;

void main() {
    vec4 origin = texture2D(iChannel0, texCoord);
    float gray = origin.r * .3 + origin.g * .59 + origin.b * .11;
    gl_FragColor = vec4(gray, gray, gray, origin.a);
}