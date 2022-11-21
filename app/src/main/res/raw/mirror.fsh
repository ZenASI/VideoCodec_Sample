#extension GL_OES_EGL_image_external : require

precision mediump float;
varying vec2 texCoord;
uniform samplerExternalOES iChannel0;

void main() {
    vec2 flipCoord = vec2(1.0-texCoord.x, texCoord.y);

    if (flipCoord.x >= 0.5){
        gl_FragColor = texture2D(iChannel0, vec2(flipCoord.x - 0.5, flipCoord.y));
    } else {
        gl_FragColor = texture2D(iChannel0, vec2(0.5 - flipCoord.x, flipCoord.y));
    }
}