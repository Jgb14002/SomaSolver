package entities;

import launcher.GeneralSettings;
import loaders.GameObjectLoader;
import lombok.Getter;
import org.joml.Vector3f;
import org.joml.Vector3i;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;
import scene.ICamera;
import shaders.SimpleShader;
import utilities.ResourceFile;

public class Cube extends GameObject
{
	private static final SimpleShader shader = SimpleShader.getInstance();
	private final Vector4f colourMask;
	@Getter
	private Vector3i gridPosition = new Vector3i(0, 0, 0);

	public Cube(GameObject object, CubeColor color)
	{
		super(object.getModel(), object.getTexture());
		this.colourMask = color.getColor();
	}

	private Cube(GameObject object, CubeColor color, Vector3i gridPosition)
	{
		super(object.getModel(), object.getTexture());
		this.colourMask = color.getColor();
		this.gridPosition = gridPosition;
		setPosition(new Vector3f(gridPosition.x * 2f, gridPosition.y * 2f, gridPosition.z * 2f));
	}

	public static Cube create(CubeColor color)
	{
		return new Cube(GameObjectLoader.loadFromOBJ(new ResourceFile(GeneralSettings.MODEL_FOLDER, GeneralSettings.CUBE_MODEL),
			new ResourceFile(GeneralSettings.TEXTURE_FOLDER, GeneralSettings.CUBE_TEXTURE)), color);
	}

	public static Cube create(CubeColor color, Vector3i gridPosition)
	{
		return new Cube(GameObjectLoader.loadFromOBJ(new ResourceFile(GeneralSettings.MODEL_FOLDER, GeneralSettings.CUBE_MODEL),
			new ResourceFile(GeneralSettings.TEXTURE_FOLDER, GeneralSettings.CUBE_TEXTURE)), color, gridPosition);
	}

	public boolean canTranslate(int dx, int dy, int dz)
	{
		if (!(gridPosition.x + dx >= -2 && gridPosition.x + dx <= 2))
		{
			return false;
		}
		if (!(gridPosition.y + dy >= -1 && gridPosition.y + dy <= 3))
		{
			return false;
		}
		if (!(gridPosition.z + dz >= -2 && gridPosition.z + dz <= 2))
		{
			return false;
		}
		return true;
	}

	public void translate(int dx, int dy, int dz)
	{
		gridPosition.x += dx;
		gridPosition.y += dy;
		gridPosition.z += dz;

		clamp(gridPosition, -2, 2, -1, 3);

		setPosition(new Vector3f(gridPosition.x * 2f, gridPosition.y * 2f, gridPosition.z * 2f));
	}

	public void mirror()
	{
		rotate(Axis.Y_AXIS);
		rotate(Axis.Y_AXIS);

		gridPosition.z *= -1;

		setPosition(new Vector3f(gridPosition.x * 2f, gridPosition.y * 2f, gridPosition.z * 2f));
	}

	public void rotate(Axis axis)
	{
		switch (axis)
		{
			case X_AXIS:
			{
				int tmp = gridPosition.y;
				gridPosition.y= gridPosition.z * -1;
				gridPosition.z = tmp;
				break;
			}
			case Y_AXIS:
			{
				int tmp = gridPosition.x;
				gridPosition.x = gridPosition.z * -1;
				gridPosition.z = tmp;
				break;
			}
			case Z_AXIS:
			{
				int tmp = gridPosition.x;
				gridPosition.x = gridPosition.y * -1;
				gridPosition.y = tmp;
				break;
			}
		}

		setPosition(new Vector3f(gridPosition.x * 2f, gridPosition.y * 2f, gridPosition.z * 2f));
	}

	private void clamp(Vector3i gridPosition, int min, int max, int minY, int maxY)
	{
		gridPosition.x = Math.max(min, Math.min(max, gridPosition.x));
		gridPosition.y = Math.max(minY, Math.min(maxY, gridPosition.y));
		gridPosition.z = Math.max(min, Math.min(max, gridPosition.z));
	}

	public void render(ICamera camera)
	{
		shader.start();
		shader.projectionViewMatrix.loadMatrix(camera.getProjectionViewMatrix());
		shader.transformationMatrix.loadMatrix(getTransformationMatrix());
		shader.colourMask.loadVec4(colourMask);
		getTexture().bindToUnit(0);
		getModel().bind(0, 1, 2);
		GL11.glDrawElements(GL11.GL_TRIANGLES, getModel().getIndexCount(), GL11.GL_UNSIGNED_INT, 0);
		getModel().unbind(0, 1, 2);
		shader.stop();
	}
}
