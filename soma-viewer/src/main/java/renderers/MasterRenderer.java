package renderers;

import org.joml.Vector4f;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLCapabilities;
import scene.Scene;
import utilities.OpenGLUtils;

public class MasterRenderer
{
	private final Vector4f clearColor = new Vector4f(1f, 1f, 1f, 1f);

	public MasterRenderer(GLCapabilities glCapabilities)
	{
		GL.setCapabilities(glCapabilities);
	}

	public void render(Scene scene)
	{
		prepare();
		scene.render();
	}

	private void prepare()
	{
		GL11.glClearColor(clearColor.x, clearColor.y, clearColor.z, clearColor.w);
		GL11.glClearStencil(0);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT | GL11.GL_STENCIL_BUFFER_BIT);
		OpenGLUtils.cullBackFaces(true);
		OpenGLUtils.disableBlending();
		OpenGLUtils.antialias(true);
		OpenGLUtils.enableDepthTesting(true);
	}

}
