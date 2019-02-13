#version 400 core

 in vec2 in_position;

 out vec2 textureCoords;

 uniform mat4 transformationMatrix;

 void main(void){

 	gl_Position = transformationMatrix * vec4(in_position.x, in_position.y, 0.0, 1.0);
 	textureCoords = vec2((in_position.x + 1.0) / 2.0, 1 - (in_position.y + 1.0) / 2.0);

 }