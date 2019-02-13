package gui;

import java.awt.geom.Rectangle2D;
import launcher.GeneralSettings;
import lombok.Getter;
import lombok.Setter;
import opengl.Vao;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import textures.Texture;

public abstract class UIElement
{
	@Getter
	protected final Vao model;
	@Getter
	@Setter
	protected float width;
	@Getter
	@Setter
	protected float height;
	@Getter
	@Setter
	protected Vector2f position;
	@Getter
	protected Texture texture;
	@Getter
	@Setter
	protected Vector4f color;
	@Getter
	@Setter
	protected int borderWidth;
	@Getter
	@Setter
	protected Vector4f borderColor;
	@Getter
	@Setter
	protected Vector4f hoverColor;
	@Getter
	@Setter
	protected Vector2f scale;
	@Getter
	@Setter
	protected Matrix4f transformationMatrix;
	@Getter
	@Setter
	protected Quaternionf rotation;
	@Getter
	protected Rectangle2D boundingBox;
	@Getter
	protected boolean mouseHovered;

	public UIElement(UIShape shape, float width, float height)
	{
		this.model = ModelGenerator.createElementModel(shape);
		this.width = width;
		this.height = height;
		refresh();
	}

	public UIElement(UIShape shape, Texture texture, float width, float height)
	{
		this.model = ModelGenerator.createElementModel(shape);
		this.width = width;
		this.height = height;
		this.texture = texture;
		refresh();
	}

	public UIElement(Vao model, float width, float height)
	{
		this.model = model;
		this.width = width;
		this.height = height;
		refresh();
	}

	public abstract void render();

	public abstract void onHoverStatusChanged(boolean entered);

	public abstract void update();

	protected void refresh()
	{
		transformationMatrix = new Matrix4f();
		rotation = new Quaternionf();
		scale = new Vector2f(width / GeneralSettings.WIDTH, height / GeneralSettings.HEIGHT);
		position = (position == null) ? new Vector2f(0.0f, 0.0f) : position;
		color = GeneralSettings.UI_MAIN_COLOR;
		hoverColor = GeneralSettings.UI_HOVER_COLOR;
		borderWidth = 1;
		borderColor = GeneralSettings.UI_BORDER_COLOR;
		boundingBox = new Rectangle2D.Float(position.x, position.y, width, height);
		updateMatrix();
	}

	protected void updateMatrix()
	{
		transformationMatrix.identity();
		transformationMatrix.translate(new Vector3f(-1.0f + ((width + (2 * position.x)) / GeneralSettings.WIDTH),
			1.0f - ((height + (2 * position.y)) / GeneralSettings.HEIGHT), 0.0f));
		transformationMatrix.scale(new Vector3f(scale.x, scale.y, 1.0f));
		transformationMatrix.rotate(rotation);
	}

}
