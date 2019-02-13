package gui.text;

import gui.UIElement;
import lombok.Getter;
import lombok.Setter;
import opengl.Vao;
import org.joml.Vector2f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;
import shaders.FontShader;
import utilities.OpenGLUtils;

public class TextLabel extends UIElement
{
	private static final FontShader shader = FontShader.getInstance();
	@Getter
	private String text;
	@Getter
	private float fontSize;
	@Getter
	private float maxLineSize;
	@Getter
	private boolean centered;
	@Getter
	@Setter
	private Vector2f position;

	private TextLabel(Vao model, String text, float fontSize, float maxLineSize, boolean centered, Vector2f position)
	{
		super(model, 0f, 0f);
		this.text = text;
		this.fontSize = fontSize;
		this.position = position;
		this.maxLineSize = maxLineSize;
		this.centered = centered;
		this.color = new Vector4f(0f, 0f, 0f, 1f);
	}

	public static TextLabel create(String text, float fontSize, float maxLineSize, boolean centered, Vector2f position)
	{
		final Vao model = FontFactory.CENTURY_FONT.loadText(text, fontSize, maxLineSize, centered);
		return new TextLabel(model, text, fontSize, maxLineSize, centered, position);
	}


	@Override
	public void render()
	{
		shader.start();
		OpenGLUtils.enableAlphaBlending();
		OpenGLUtils.enableDepthTesting(false);
		FontFactory.CENTURY_FONT.getTextureAtlas().bindToUnit(0);
		shader.color.loadVec3(color.x, color.y, color.z);
		shader.translation.loadVec2(position);
		model.bind(0);
		model.bind(1);
		GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, model.getIndexCount());
		model.unbind(0, 1);
		shader.stop();
		OpenGLUtils.enableDepthTesting(true);
		OpenGLUtils.disableBlending();
	}

	@Override
	public void onHoverStatusChanged(boolean entered)
	{

	}

	@Override
	public void update()
	{

	}
}
