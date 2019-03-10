package graph;

import entities.CubeColor;
import lombok.Getter;

public enum Direction
{
	LEFT(0,-1,0),
	RIGHT(0,1,0),
	FORWARD(0,0, -1),
	BACKWARD(0,0,1),
	UP(-1,0,0),
	DOWN(1,0,0);

	protected final int y;
	protected final int x;
	protected final int z;

	Direction(int y, int x, int z)
	{
		this.y = y;
		this.x = x;
		this.z = z;
	}

}
