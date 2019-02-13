#version 400 core

 in vec3 in_position;
 in vec2 in_textureCoords;
 in vec3 in_normal;

 out vec2 pass_textureCoords;
 out vec3 pass_normal;

 uniform mat4 projectionViewMatrix;
 uniform mat4 transformationMatrix;

 void main(void){

 	gl_Position = projectionViewMatrix * transformationMatrix * vec4(in_position, 1.0);
 	pass_normal = in_normal;
 	pass_textureCoords = in_textureCoords;

 }