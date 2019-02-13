package shaders;

import utilities.ResourceFile;

public class FontShader extends ShaderProgram
{
	private static final ResourceFile VERTEX_SHADER = new ResourceFile("shaders", "FontVertex.glsl");
	private static final ResourceFile FRAGMENT_SHADER = new ResourceFile("shaders", "FontFragment.glsl");
	private static FontShader instance = null;

	public UniformVec2 translation = new UniformVec2("translation");
	public UniformVec3 color = new UniformVec3("color");

	public FontShader()
	{
		super(VERTEX_SHADER, FRAGMENT_SHADER, "in_position", "in_textureCoords");
		super.storeAllUniformLocations(translation, color);
	}


	public static FontShader getInstance()
	{
		if (instance == null)
		{
			instance = new FontShader();
		}
		return instance;
	}

}
