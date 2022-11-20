attribute vec4 vPosition;
attribute vec2 vTexCoord;
varying vec2 texCoord;
uniform mat4 vMatrix;

void main() {
    // 頂點
    gl_Position = vPosition; // 把java 傳來的交給gl_position
    // 紋理
    //texCoord = vTexCoord; // 把java 傳來的交給texCoord
    texCoord = (vMatrix * vec4(vTexCoord, 1.0, 1.0)).xy;
}