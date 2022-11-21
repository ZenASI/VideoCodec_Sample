#extension GL_OES_EGL_image_external : require
precision mediump float;

uniform samplerExternalOES iChannel0;
varying vec2 texCoord;

void createImageH(out vec4 fragColor, in vec2 fragCoord) {
    if (fragCoord.y <=  0.333){
        fragColor = texture2D(iChannel0, vec2(fragCoord.x,fragCoord.y + 0.333));
    } else if(fragCoord.y > 0.333 && fragCoord.y<= 0.666){
        fragColor = texture2D(iChannel0, fragCoord);
    } else {
        fragColor = texture2D(iChannel0, vec2(fragCoord.x,fragCoord.y - 0.333));
    }
}

void createImageV(out vec4 fragColor, in vec2 fragCoord) {
    if (fragCoord.x <=  0.333){
        fragColor = texture2D(iChannel0, vec2(fragCoord.x + 0.333 ,fragCoord.y));
    } else if(fragCoord.x > 0.333 && fragCoord.x <= 0.666){
        fragColor = texture2D(iChannel0, fragCoord);
    } else {
        fragColor = texture2D(iChannel0, vec2(fragCoord.x - 0.333 ,fragCoord.y));
    }
}

void main() {

//        createImagegeH(gl_FragColor, texCoord);

        createImageV(gl_FragColor, texCoord);
 }