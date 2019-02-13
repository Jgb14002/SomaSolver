#version 400 core

in vec2 textureCoords;

out vec4 out_colour;

uniform vec4 foregroundColor;
uniform vec4 borderColor;
uniform vec2 dimensions;
uniform int borderWidth;
uniform int hovered;
uniform vec4 hoverColor;

uniform sampler2D tex;

void main(void){

	 vec2 st = textureCoords.xy * dimensions.xy;

     float left = step(borderWidth, st.x);
     float top = step(borderWidth, st.y);
     float right = step(borderWidth, dimensions.x - st.x);
     float bottom = step(borderWidth, dimensions.y - st.y);

     if((left * top * right * bottom) == 0)
     {
		out_colour = borderColor;
     }
     else
     {
     	vec4 col = texture(tex, textureCoords);
     	if(col.a == 1)
     	{
     		out_colour = col;
     	}
     	else
     	{
     		out_colour =(hovered == 1) ? hoverColor : foregroundColor;
     	}

     }

}