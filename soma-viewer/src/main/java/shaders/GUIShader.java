package shaders;

import utilities.ResourceFile;

public class GUIShader extends ShaderProgram
{
	private static final ResourceFile VERTEX_SHADER = new ResourceFile("shaders", "GUIVertex.glsl");
	private static final ResourceFile FRAGMENT_SHADER = new ResourceFile("shaders", "GUIFragment.glsl");
	private static GUIShader instance = null;
	public UniformMatrix transformationMatrix = new UniformMatrix("transformationMatrix");
	public UniformVec2 dimensions = new UniformVec2("dimensions");
	public UniformInt borderWidth = new UniformInt("borderWidth");
	public UniformVec4 foregroundColor = new UniformVec4("foregroundColor");
	public UniformVec4 borderColor = new UniformVec4("borderColor");
	public UniformVec4 hoverColor = new UniformVec4("hoverColor");
	public UniformInt hovered = new UniformInt("hovered");


	public GUIShader()
	{
		super(VERTEX_SHADER, FRAGMENT_SHADER, "in_position");
		super.storeAllUniformLocations(transformationMatrix, foregroundColor, borderColor, borderWidth, dimensions, hovered, hoverColor);
	}


	public static GUIShader getInstance()
	{
		if (instance == null)
		{
			instance = new GUIShader();
		}
		return instance;
	}

}
