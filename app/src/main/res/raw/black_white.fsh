#extension GL_OES_EGL_image_external : require

precision mediump float;
varying vec2 texCoord;
uniform samplerExternalOES iChannel0;

void main() {
    vec4 origin = texture2D(iChannel0, texCoord);
    float black_white = (origin.r + origin.g + origin.b)/3.0;
    float finalColor = step(.5, black_white);
    gl_FragColor = vec4(finalColor, finalColor, finalColor, origin.a);
}