package entities;

import gui.ModelGenerator;
import gui.UIShape;
import opengl.Vao;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;
import scene.ICamera;
import shaders.FloorShader;
import utilities.OpenGLUtils;


public class Floor extends GameObject
{
	private final FloorShader shader = FloorShader.getInstance();

	private Floor(Vao model)
	{
		super(model);
		setScale(new Vector3f(3.06f,3.06f,1.0f));
		setPosition(new Vector3f(-0.02f, -3.0f, -0.02f));
		getRotation().rotateAxis((float) Math.toRadians(-90.0f), new Vector3f(1,0,0));
		updateMatrix();
	}

	public static Floor create()
	{
		return new Floor(ModelGenerator.createElementModel(UIShape.QUAD));
	}

	public void render(ICamera camera)
	{
		shader.start();
		OpenGLUtils.enableAlphaBlending();
		shader.transformationMatrix.loadMatrix(getTransformationMatrix());
		shader.projectionViewMatrix.loadMatrix(camera.getProjectionViewMatrix());
		getModel().bind(0);
		GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, getModel().getIndexCount());
		getModel().unbind(0);
		shader.stop();
	}
}

