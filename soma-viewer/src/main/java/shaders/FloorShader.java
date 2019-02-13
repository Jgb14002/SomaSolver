package shaders;

import utilities.ResourceFile;

public class FloorShader extends ShaderProgram
{
	private static final ResourceFile VERTEX_SHADER = new ResourceFile("shaders", "FloorVertex.glsl");
	private static final ResourceFile FRAGMENT_SHADER = new ResourceFile("shaders", "FloorFragment.glsl");
	private static FloorShader instance = null;
	public UniformMatrix projectionViewMatrix = new UniformMatrix("projectionViewMatrix");
	public UniformMatrix transformationMatrix = new UniformMatrix("transformationMatrix");


	public FloorShader()
	{
		super(VERTEX_SHADER, FRAGMENT_SHADER, "in_position");
		super.storeAllUniformLocations(projectionViewMatrix, transformationMatrix);
	}

	public static FloorShader getInstance()
	{
		if (instance == null)
		{
			instance = new FloorShader();
		}
		return instance;
	}

}
