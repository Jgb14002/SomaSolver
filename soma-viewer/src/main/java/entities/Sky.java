package entities;

import opengl.Vao;
import org.lwjgl.opengl.GL11;
import scene.ICamera;
import shaders.SkyboxShader;
import skybox.CubeGenerator;

public class Sky extends GameObject
{
	private static final SkyboxShader shader = SkyboxShader.getInstance();

	private Sky(Vao model)
	{
		super(model);
	}

	public static Sky create()
	{
		return new Sky(CubeGenerator.generateCube(200f));
	}

	public void render(ICamera camera)
	{
		shader.start();
		shader.projectionViewMatrix.loadMatrix(camera.getProjectionViewMatrix());
		getModel().bind(0);
		GL11.glDrawElements(GL11.GL_TRIANGLES, getModel().getIndexCount(), GL11.GL_UNSIGNED_INT, 0);
		getModel().unbind(0);
		shader.stop();
	}
}
