package graph;

import entities.Cube;
import entities.CubeColor;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.joml.Vector3i;

@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Node
{
	@EqualsAndHashCode.Include
	protected final int x, y, z;

	@Getter
	private final Map<Direction, Node> neighbors;

	/**
	 * @param y - Which layer of the cube the node belongs too (0 - top | 1 - middle | 2 - bottom)
	 * @param x - The x coordinate of the node
	 * @param z - The y coordinate of the node
	 */
	public Node(int y, int x, int z)
	{
		this.y = y;
		this.x = x;
		this.z = z;

		this.neighbors = new HashMap<>();
	}

	public void addNeighbor(Direction d, Node n)
	{
		this.neighbors.put(d, n);
	}

	public boolean canFollowPath(Path path)
	{
		if (path.peek() == null)
		{
			return true;
		}
		Direction direction = path.next();
		Node next = neighbors.get(direction);
		return next != null && next.canFollowPath(path);
	}

	public Cube toCube(CubeColor color)
	{
		return Cube.create(color, new Vector3i(z - 1, 1 - y, x - 1));
	}

}
