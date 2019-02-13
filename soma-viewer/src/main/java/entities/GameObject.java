package entities;

import lombok.Getter;
import lombok.Setter;
import opengl.Vao;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector4f;
import shaders.ShaderProgram;
import textures.Texture;

public class GameObject
{
	@Getter
	private final Vao model;
	@Getter
	private Texture texture;
	@Getter
	private Matrix4f transformationMatrix = new Matrix4f();
	@Getter
	private Vector3f position = new Vector3f(0f, 0f, 0f);
	@Getter
	private Vector3f scale = new Vector3f(1f, 1f, 1f);
	@Getter
	private Quaternionf rotation = new Quaternionf();

	public GameObject(Vao model, Texture texture)
	{
		this.model = model;
		this.texture = texture;
		transformationMatrix.identity();
	}

	public GameObject(Vao model)
	{
		this.model = model;
		transformationMatrix.identity();
	}

	public void setPosition(Vector3f position)
	{
		this.position = position;
		updateMatrix();
	}

	public void setRotation(Quaternionf rotation)
	{
		this.rotation = rotation;
		updateMatrix();
	}


	public void setScale(Vector3f scale)
	{
		this.scale = scale;
		updateMatrix();
	}

	protected void updateMatrix()
	{
		transformationMatrix.identity();
		transformationMatrix.translate(position);
		transformationMatrix.rotate(rotation);
		transformationMatrix.scale(scale);
	}

	public enum Axis
	{
		X_AXIS,
		Y_AXIS,
		Z_AXIS;
	}

}
