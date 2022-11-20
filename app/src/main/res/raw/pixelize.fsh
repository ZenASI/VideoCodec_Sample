precision mediump float;

uniform vec3 iResolution;
uniform sampler2D iChannel0;
varying vec2 texCoord;

#define S (iResolution.x / 6e1) // The cell size.

void main() {
    gl_FragColor = texture2D(iChannel0, floor((texCoord*iResolution.xy + .5) / S) * S / iResolution.xy);
}