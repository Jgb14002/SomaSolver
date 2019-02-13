#version 330

in vec2 in_position;
in vec2 in_textureCoords;

out vec2 pass_textureCoords;

uniform vec2 translation;

void main() {

	gl_Position = vec4(in_position, 0.0, 1.0);
	pass_textureCoords = in_textureCoords;

}
