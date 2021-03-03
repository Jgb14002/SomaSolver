package graph;

import entities.Cube;
import entities.CubeColor;
import entities.Piece;
import entities.PieceIndex;

import java.util.*;
import javax.annotation.Nullable;

public class GraphBuilder
{
	private final int[][][] layout;
	private Set<Node> traversed = new HashSet<>();

	public GraphBuilder(int[][][] layout)
	{
		this.layout = layout;
	}

	@Nullable
	private Node findFirstOpen()
	{
		for (int y = 0; y < 3; y++)
		{
			for (int x = 0; x < 3; x++)
			{
				for (int z = 0; z < 3; z++)
				{
					int val = layout[y][x][z];
					if (val == 0)
					{
						return new Node(y, x, z);
					}
				}
			}
		}
		return null;
	}

	public GraphBuilder build()
	{
		final Node root = findFirstOpen();

		if (root == null)
		{
			return null;
		}

		traverse(root);

		return this;
	}

	private void traverse(Node node)
	{
		if (layout[node.y][node.x][node.z] == 1)
		{
			return;
		}

		for (Direction dir : Direction.values())
		{
			int y = node.y + dir.y;
			int z = node.z + dir.z;
			int x = node.x + dir.x;

			if ((y >= 0 && y < 3) && (x >= 0 && x < 3) && (z >= 0 && z < 3))
			{
				Optional<Node> existing = traversed.stream().filter(present -> present.y == y && present.x == x && present.z == z)
						.findFirst();
				final Node next = existing.orElseGet(() -> new Node(y, x, z));
				if (layout[next.y][next.x][next.z] == 0)
				{
					node.addNeighbor(dir, next);
				}
			}
		}

		if (traversed.contains(node))
		{
			return;
		}

		traversed.add(node);

		for (Node child : node.getNeighbors().values())
		{
			traverse(child);
		}
	}

	public List<Piece> getPieces(PieceIndex index)
	{
		List<Piece> pieces = new LinkedList<>();

		PathTransformer transformer = new PathTransformer(index.getPath());
		Set<List<Direction>> paths = transformer.transform();

		for (Node node : traversed)
		{
			paths.forEach(transformedPath -> {
				if (node.canFollowPath(new Path(transformedPath)))
				{
					List<Cube> cubes = new ArrayList<>();
					cubes.add(node.toCube(CubeColor.ORANGE));
					Node parent = node;
					for (Direction dir : transformedPath)
					{
						final Node neighbor = parent.getNeighbors().get(dir);
						if (neighbor != null)
						{
							cubes.add(neighbor.toCube(CubeColor.ORANGE));
							parent = neighbor;
						}
					}
					pieces.add(new Piece(index, cubes));
				}
			});
		}
		return pieces;
	}
}
