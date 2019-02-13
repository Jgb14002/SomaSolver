package scene;

import input.Input;
import launcher.Game;
import static launcher.GeneralSettings.HEIGHT;
import static launcher.GeneralSettings.WIDTH;
import lombok.Getter;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import utilities.SmoothFloat;

public class Camera implements ICamera
{
	private static final float PITCH_SENSITIVITY = 0.3f;
	private static final float YAW_SENSITIVITY = 0.3f;
	private static final float MAX_PITCH = 90;
	private static final float ZOOM_SENSITIVITY = 3f;

	private static final float FOV = 70;
	private static final float NEAR_PLANE = 0.2f;
	private static final float FAR_PLANE = 400;

	private static final float Y_OFFSET = 0;
	private static final float MAX_DISTANCE = 45.0f;
	private static final float MIN_DISTANCE = 6.0f;

	private Matrix4f projectionMatrix;
	private Matrix4f viewMatrix = new Matrix4f();

	private Vector3f position = new Vector3f(0, 0, 0);

	private float yaw = 0;
	private SmoothFloat pitch = new SmoothFloat(10, 10);
	private SmoothFloat angleAroundPlayer = new SmoothFloat(0, 10);
	private SmoothFloat distanceFromPlayer = new SmoothFloat(10, 5);

	public Camera()
	{
		this.projectionMatrix = createProjectionMatrix();
	}

	private static Matrix4f createProjectionMatrix()
	{
		Matrix4f projectionMatrix = new Matrix4f();
		float aspectRatio = Game.getGameWindow().getAspect();
		float y_scale = (float) ((1f / Math.tan(Math.toRadians(FOV / 2f))));
		float x_scale = y_scale / aspectRatio;
		float frustum_length = FAR_PLANE - NEAR_PLANE;

		projectionMatrix.m00(x_scale);
		projectionMatrix.m11(y_scale);
		projectionMatrix.m22(-((FAR_PLANE + NEAR_PLANE) / frustum_length));
		projectionMatrix.m23(-1);
		projectionMatrix.m32(-((2 * NEAR_PLANE * FAR_PLANE) / frustum_length));
		projectionMatrix.m33(0);
		return projectionMatrix;
	}

	@Override
	public Matrix4f getViewMatrix()
	{
		return viewMatrix;
	}

	@Override
	public Matrix4f getProjectionMatrix()
	{
		return projectionMatrix;
	}

	@Override
	public Matrix4f getProjectionViewMatrix()
	{
		return new Matrix4f().set(projectionMatrix).mul(viewMatrix);
	}

	@Override
	public void update()
	{
		move();
		updateMatrix();
	}

	@Override
	public void updatePerspective(float aspect)
	{
		//screen isn't resizable so nothing to do here
	}

	public float getYaw()
	{
		return Math.abs(yaw);
	}

	private void move()
	{
		calculatePitch();
		calculateAngleAroundPlayer();
		float horizontalDistance = calculateHorizontalDistance();
		float verticalDistance = calculateVerticalDistance();
		calculateCameraPosition(horizontalDistance, verticalDistance);
		this.yaw = 360 - (angleAroundPlayer.get() % 360);
		yaw %= 360;
		processZoom();
	}

	private void updateMatrix()
	{
		viewMatrix.identity();
		viewMatrix.rotate((float) Math.toRadians(pitch.get()), new Vector3f(1, 0, 0));
		viewMatrix.rotate((float) Math.toRadians(yaw), new Vector3f(0, 1, 0));
		Vector3f negativeCameraPos = new Vector3f(-position.x, -position.y, -position.z);
		viewMatrix.translate(negativeCameraPos);
	}

	private void calculateCameraPosition(float horizDistance, float verticDistance)
	{
		float theta = angleAroundPlayer.get();
		position.x = (float) (horizDistance * Math.sin(Math.toRadians(theta)));
		position.y = verticDistance + Y_OFFSET;
		position.z = (float) (horizDistance * Math.cos(Math.toRadians(theta)));
	}

	private float calculateHorizontalDistance()
	{
		return (float) (distanceFromPlayer.get() * Math.cos(Math.toRadians(pitch.get())));
	}

	private float calculateVerticalDistance()
	{
		return (float) (distanceFromPlayer.get() * Math.sin(Math.toRadians(pitch.get())));
	}

	private void processZoom()
	{
		distanceFromPlayer.increaseTarget(-Input.getDWheel() * ZOOM_SENSITIVITY);
		clampDistance();
		distanceFromPlayer.update(Math.min(Game.getGameRenderTime() / 1000f, 0.008f));
	}

	private void calculatePitch()
	{
		if (Input.isButtonDown(1))
		{
			float pitchChange = Input.getDY() * PITCH_SENSITIVITY;
			pitch.increaseTarget(pitchChange);
			clampPitch();
		}
		pitch.update(Math.min(Game.getGameRenderTime() / 1000f, 0.008f));
	}

	private void calculateAngleAroundPlayer()
	{
		if (Input.isButtonDown(1))
		{
			float angleChange = Input.getDX() * YAW_SENSITIVITY;
			angleAroundPlayer.increaseTarget(-angleChange);
		}
		angleAroundPlayer.update(Math.min(Game.getGameRenderTime() / 1000f, 0.008f));
	}

	private void clampPitch()
	{
		if (pitch.getTarget() < 0)
		{
			pitch.setTarget(0);
		}
		else if (pitch.getTarget() > MAX_PITCH)
		{
			pitch.setTarget(MAX_PITCH);
		}
	}

	private void clampDistance()
	{
		if (distanceFromPlayer.getTarget() < MIN_DISTANCE)
		{
			distanceFromPlayer.setTarget(MIN_DISTANCE);
		}
		else if (distanceFromPlayer.getTarget() > MAX_DISTANCE)
		{
			distanceFromPlayer.setTarget(MAX_DISTANCE);
		}
	}
}
