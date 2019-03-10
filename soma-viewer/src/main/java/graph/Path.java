package graph;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Queue;

public class Path
{
	private final Queue<Direction> path;

	public Path(Direction... path)
	{
		this.path = new LinkedList<>();
		this.path.addAll(Arrays.asList(path));
	}

	public Path(Collection<Direction> path)
	{
		this.path = new LinkedList<>();
		this.path.addAll(path);
	}

	public Direction next()
	{
		return path.poll();
	}

	public Direction peek()
	{
		return path.peek();
	}

}
