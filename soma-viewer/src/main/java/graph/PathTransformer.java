package graph;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class PathTransformer
{
	private static PathAxis[] ROTATION_PATH = {
		PathAxis.X_AXIS,
		PathAxis.X_AXIS,
		PathAxis.Z_AXIS,
		PathAxis.X_AXIS,
		PathAxis.X_AXIS,
		PathAxis.Z_AXIS
	};
	private final Set<List<Direction>> transformedPaths = new HashSet<>();
	private List<Direction> path;

	public PathTransformer(Direction... path)
	{
		this.path = new LinkedList<>();
		this.path.addAll(Arrays.asList(path));
	}

	public Set<List<Direction>> transform()
	{
		for (PathAxis pathAxis : ROTATION_PATH)
		{
			for (int y = 0; y < 4; y++)
			{
				transformedPaths.add(path);
				rotate(PathAxis.Y_AXIS);
			}
			rotate(pathAxis);
		}
		return transformedPaths;
	}

	private void rotate(PathAxis axis)
	{
		switch (axis)
		{
			case X_AXIS:
			{
				Map<Direction, Direction> map = new LinkedHashMap<>();
				map.put(Direction.DOWN, Direction.FORWARD);
				map.put(Direction.UP, Direction.BACKWARD);
				map.put(Direction.FORWARD, Direction.UP);
				map.put(Direction.BACKWARD, Direction.DOWN);
				swap(map);
				break;
			}
			case Y_AXIS:
			{
				Map<Direction, Direction> map = new LinkedHashMap<>();
				map.put(Direction.RIGHT, Direction.BACKWARD);
				map.put(Direction.LEFT, Direction.FORWARD);
				map.put(Direction.FORWARD, Direction.RIGHT);
				map.put(Direction.BACKWARD, Direction.LEFT);
				swap(map);
				break;
			}
			case Z_AXIS:
			{
				Map<Direction, Direction> map = new LinkedHashMap<>();
				map.put(Direction.DOWN, Direction.RIGHT);
				map.put(Direction.UP, Direction.LEFT);
				map.put(Direction.RIGHT, Direction.UP);
				map.put(Direction.LEFT, Direction.DOWN);
				swap(map);
				break;
			}
		}
	}

	private void swap(Map<Direction, Direction> map)
	{
		List<Direction> tmp = new LinkedList<>();
		for (Direction dir : path)
		{
			Direction target = map.get(dir);
			tmp.add((target != null) ? target : dir);
		}
		path = tmp;
	}

}
