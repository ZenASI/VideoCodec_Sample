#extension GL_OES_EGL_image_external : require

precision mediump float;
varying vec2 texCoord;
uniform samplerExternalOES iChannel0;

void main() {
    vec2 flipCoord = texCoord;

    if (flipCoord.y <= 0.5){
        flipCoord.y = 1.0 - flipCoord.y;
    }
    gl_FragColor = texture2D(iChannel0, fract(flipCoord));
}