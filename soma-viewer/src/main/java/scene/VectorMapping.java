package scene;

import org.joml.Vector3i;

public class VectorMapping
{
	public static Vector3i getForwardDirection(float yaw)
	{
		if (yaw < 45 || yaw > 315)
		{
			return new Vector3i(0, 0, -1);
		}

		if (yaw < 135 && yaw > 45)
		{
			return new Vector3i(1, 0, 0);
		}

		if (yaw < 225 && yaw > 135)
		{
			return new Vector3i(0, 0, 1);
		}

		if (yaw < 315 && yaw > 225)
		{
			return new Vector3i(-1, 0, 0);
		}

		return new Vector3i(0);
	}

	public static Vector3i getBackwardDirection(float yaw)
	{
		if (yaw < 45 || yaw > 315)
		{
			return new Vector3i(0, 0, 1);
		}

		if (yaw < 135 && yaw > 45)
		{
			return new Vector3i(-1, 0, 0);
		}

		if (yaw < 225 && yaw > 135)
		{
			return new Vector3i(0, 0, -1);
		}

		if (yaw < 315 && yaw > 225)
		{
			return new Vector3i(1, 0, 0);
		}

		return new Vector3i(0);
	}

	public static Vector3i getLeftDirection(float yaw)
	{
		if (yaw < 45 || yaw > 315)
		{
			return new Vector3i(-1, 0, 0);
		}

		if (yaw < 135 && yaw > 45)
		{
			return new Vector3i(0, 0, -1);
		}

		if (yaw < 225 && yaw > 135)
		{
			return new Vector3i(1, 0, 0);
		}

		if (yaw < 315 && yaw > 225)
		{
			return new Vector3i(0, 0, 1);
		}

		return new Vector3i(0);
	}

	public static Vector3i getRightDirection(float yaw)
	{
		if (yaw < 45 || yaw > 315)
		{
			return new Vector3i(1, 0, 0);
		}

		if (yaw < 135 && yaw > 45)
		{
			return new Vector3i(0, 0, 1);
		}

		if (yaw < 225 && yaw > 135)
		{
			return new Vector3i(-1, 0, 0);
		}

		if (yaw < 315 && yaw > 225)
		{
			return new Vector3i(0, 0, -1);
		}

		return new Vector3i(0);
	}
}