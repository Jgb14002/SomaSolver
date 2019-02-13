#version 400 core

in vec2 pass_textureCoords;
in vec3 pass_normal;

out vec4 out_colour;

uniform sampler2D diffuseMap;
uniform vec4 colourMask;

void main(void){

	vec4 diffuseColour = texture(diffuseMap, pass_textureCoords);
	vec3 unitNormal = normalize(pass_normal);
	out_colour = diffuseColour * colourMask;

}