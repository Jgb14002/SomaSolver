#version 400 core

in vec3 in_position;

out vec2 textureCoords;

uniform mat4 transformationMatrix;
uniform mat4 projectionViewMatrix;


void main(void){

 gl_Position = projectionViewMatrix * transformationMatrix * vec4(in_position, 1.0);
 textureCoords = vec2((in_position.x + 1.0) / 2.0, 1 - (in_position.y + 1.0) / 2.0);

}