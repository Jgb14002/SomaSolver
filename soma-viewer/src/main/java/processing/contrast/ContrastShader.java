package processing.contrast;

import shaders.ShaderProgram;
import utilities.ResourceFile;

public class ContrastShader extends ShaderProgram {

	private static final ResourceFile VERTEX_SHADER = new ResourceFile("shaders", "ContrastVertex.glsl");
	private static final ResourceFile FRAGMENT_SHADER = new ResourceFile("shaders", "ContrastFragment.glsl");


	public ContrastShader()
	{
		super(VERTEX_SHADER, FRAGMENT_SHADER, "position");
	}
}
