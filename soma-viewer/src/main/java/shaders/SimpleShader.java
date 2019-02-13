package shaders;

import utilities.ResourceFile;

public class SimpleShader extends ShaderProgram
{
	private static final ResourceFile VERTEX_SHADER = new ResourceFile("shaders", "SimpleVertex.glsl");
	private static final ResourceFile FRAGMENT_SHADER = new ResourceFile("shaders", "SimpleFragment.glsl");
	private static SimpleShader instance = null;
	public UniformMatrix projectionViewMatrix = new UniformMatrix("projectionViewMatrix");
	public UniformMatrix transformationMatrix = new UniformMatrix("transformationMatrix");
	public UniformVec4 colourMask = new UniformVec4("colourMask");


	public SimpleShader()
	{
		super(VERTEX_SHADER, FRAGMENT_SHADER, "in_position", "in_textureCoords", "in_normal");
		super.storeAllUniformLocations(projectionViewMatrix, transformationMatrix, colourMask);
	}

	public static SimpleShader getInstance()
	{
		if (instance == null)
		{
			instance = new SimpleShader();
		}
		return instance;
	}

}
