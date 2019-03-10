package graph;

import entities.CubeColor;
import lombok.Getter;

public enum Direction
{
	LEFT(0,-1,0, CubeColor.BLUE),
	RIGHT(0,1,0, CubeColor.ORANGE),
	FORWARD(0,0, -1, CubeColor.PURPLE),
	BACKWARD(0,0,1, CubeColor.YELLOW),
	UP(-1,0,0, CubeColor.RED),
	DOWN(1,0,0, CubeColor.GREEN);

	protected final int y;
	protected final int x;
	protected final int z;
	@Getter
	protected final CubeColor color;

	Direction(int y, int x, int z, CubeColor color)
	{
		this.y = y;
		this.x = x;
		this.z = z;
		this.color = color;
	}

}
