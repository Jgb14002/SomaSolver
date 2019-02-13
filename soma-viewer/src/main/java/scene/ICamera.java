package scene;

import org.joml.Matrix4f;

public interface ICamera
{
	Matrix4f getViewMatrix();
	Matrix4f getProjectionMatrix();
	Matrix4f getProjectionViewMatrix();

	void update();
	void updatePerspective(float aspect);
	float getYaw();
}
