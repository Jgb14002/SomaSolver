package entities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.joml.Vector3f;
import org.joml.Vector3i;
import org.lwjgl.opengl.GL11;
import scene.ICamera;
import shaders.SimpleShader;

public class Piece
{
	private static final SimpleShader shader = SimpleShader.getInstance();
	private static final Map<PieceIndex, Piece> pieceMap = new HashMap<>();

	static
	{
		for (PieceIndex index : PieceIndex.values())
		{
			pieceMap.put(index, create(index));
		}
	}

	@Getter
	private final PieceIndex pieceIndex;
	private final CubeColor colourMask;
	@Getter
	private List<Cube> cubes;

	private Piece(PieceIndex pieceIndex)
	{
		this.colourMask = CubeColor.getColorForIndex(pieceIndex.getIndex());
		this.pieceIndex = pieceIndex;
		this.cubes = new ArrayList<>();

		int[] cubePositions = pieceIndex.getGridPositions();
		for (int i = 0; i < cubePositions.length; i += 3)
		{
			Cube cube = Cube.create(colourMask, new Vector3i(cubePositions[i], cubePositions[i + 1], cubePositions[i + 2]));
			cubes.add(cube);
		}
	}

	public Piece(PieceIndex pieceIndex, List<Cube> cubeList)
	{
		this.colourMask = CubeColor.getColorForIndex(pieceIndex.getIndex());
		this.pieceIndex = pieceIndex;
		this.cubes = cubeList;
	}

	public static Piece getFromIndex(PieceIndex index)
	{
		return pieceMap.get(index);
	}

	public static Piece createFromData(PieceIndex index, int[][][] data)
	{
		final List<Cube> cubeList = new ArrayList<>();
		final CubeColor colourMask = CubeColor.getColorForIndex(index.getIndex());

		for (int slice = 0; slice < data.length; slice++)
		{
			for (int row = 0; row < 3; row++)
			{
				for (int col = 0; col < 3; col++)
				{
					int val = data[slice][row][col];

					if (val > 0)
					{
						cubeList.add(Cube.create(colourMask, new Vector3i(col - 1, 1 - slice, row - 1)));
					}
				}
			}
		}

		return new Piece(index, cubeList);
	}

	private static Piece create(PieceIndex pieceIndex)
	{
		return new Piece(pieceIndex);
	}

	public void translate(int dx, int dy, int dz)
	{
		for (Cube cube : cubes)
		{
			if (!cube.canTranslate(dx, dy, dz))
			{
				return;
			}
		}

		for (Cube cube : cubes)
		{
			cube.translate(dx, dy, dz);
		}
	}

	public void translate(Vector3i vector)
	{
		translate(vector.x, vector.y, vector.z);
	}

	public void rotate(GameObject.Axis axis)
	{
		for (Cube cube : cubes)
		{
			cube.rotate(axis);
		}
	}

	public void render(ICamera camera)
	{
		shader.start();
		shader.projectionViewMatrix.loadMatrix(camera.getProjectionViewMatrix());
		shader.colourMask.loadVec4(colourMask.getColor());
		cubes.forEach(cube ->
		{
			shader.transformationMatrix.loadMatrix(cube.getTransformationMatrix());
			cube.getTexture().bindToUnit(0);
			cube.getModel().bind(0, 1, 2);
			GL11.glDrawElements(GL11.GL_TRIANGLES, cube.getModel().getIndexCount(), GL11.GL_UNSIGNED_INT, 0);
			cube.getModel().unbind(0, 1, 2);
		});
		shader.stop();
	}

	public int[][][] getRawData()
	{
		final int[][][] data = new int[3][3][3];

		for(Cube cube : cubes)
		{
			Vector3i position = cube.getGridPosition();

			int slice = 1 - position.y;
			int row = position.z + 1;
			int col = position.x + 1;

			data[slice][row][col] = 1;
		}

		return data;
	}

	public void mirror()
	{
		for (Cube cube : cubes)
		{
			cube.mirror();
		}
	}
}
