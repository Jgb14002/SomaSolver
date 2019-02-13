package shaders;

import utilities.ResourceFile;

public class SkyboxShader extends ShaderProgram
{
	private static final ResourceFile VERTEX_SHADER = new ResourceFile("shaders", "SkyboxVertex.glsl");
	private static final ResourceFile FRAGMENT_SHADER = new ResourceFile("shaders", "SkyboxFragment.glsl");
	private static SkyboxShader instance = null;
	public UniformMatrix projectionViewMatrix = new UniformMatrix("projectionViewMatrix");


	public SkyboxShader()
	{
		super(VERTEX_SHADER, FRAGMENT_SHADER, "in_position");
		super.storeAllUniformLocations(projectionViewMatrix);
	}

	public static SkyboxShader getInstance()
	{
		if (instance == null)
		{
			instance = new SkyboxShader();
		}
		return instance;
	}
}
