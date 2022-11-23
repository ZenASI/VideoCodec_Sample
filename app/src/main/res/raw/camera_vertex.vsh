attribute vec4 vPosition;
attribute vec2 vTexCoord;
varying vec2 texCoord;
uniform mat4 vMatrix;

void main() {
    mat4 mvp = mat4(
    1.0, 0.0, 0.0, 0.0,
    0.0, 1.0, 0.0, 0.0,
    0.0, 0.0, 1.0, 0.0,
    0.0, 0.0, 0.0, 1.0);
    // 頂點
    gl_Position = mvp * vPosition;// 把java 傳來的交給gl_position
//        gl_Position = vec4(vPosition.x, vPosition.y, .0, 1.0);// 把java 傳來的交給gl_position
    // 紋理
    //    texCoord = vTexCoord; // 把java 傳來的交給texCoord
    texCoord = (vMatrix * vec4(vTexCoord.x, vTexCoord.y, 1.0, 1.0)).xy;
}