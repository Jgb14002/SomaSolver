package processing;

import opengl.Vao;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import processing.contrast.ContrastChanger;


public class PostProcessing
{

	private static final float[] POSITIONS = {-1, 1, -1, -1, 1, 1, 1, -1};

	private static Vao quad;
	private static ContrastChanger contrastChanger;


	public static void init()
	{
		quad = createVao();
		contrastChanger = new ContrastChanger();
	}

	public static void doPostProcessing(int colourTexture)
	{
		start();
		contrastChanger.render(colourTexture);
		end();
	}

	public static void cleanUp()
	{
		contrastChanger.cleanUp();
	}

	private static void start()
	{
		GL30.glBindVertexArray(quad.id);
		GL20.glEnableVertexAttribArray(0);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
	}

	private static void end()
	{
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL20.glDisableVertexAttribArray(0);
		GL30.glBindVertexArray(0);
	}

	private static Vao createVao()
	{
		Vao vao = Vao.create();
		vao.bind();
		vao.createAttribute(0, POSITIONS, 2);
		vao.unbind();
		return vao;
	}

}
