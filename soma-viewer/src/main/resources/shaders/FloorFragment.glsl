#version 400 core

in vec2 textureCoords;

out vec4 out_colour;

void main(void){

	 vec2 dimensions = vec2(300, 300);
	 vec2 sub_dimensions = vec2(100, 100);
	 vec2 st = mod(textureCoords.xy * dimensions.xy, sub_dimensions);

	 int borderWidth = 2;
     vec4 borderColor = vec4(0.0, 0.0, 0.0, 0.2);

     float left = step(borderWidth, st.x);
     float top = step(borderWidth, st.y);
     float right = step(borderWidth, dimensions.x - st.x);
     float bottom = step(borderWidth, dimensions.y - st.y);

     if((left * top * right * bottom) == 1)
     {
		out_colour = borderColor;
     }
     else
     {
     	discard;
     }

}