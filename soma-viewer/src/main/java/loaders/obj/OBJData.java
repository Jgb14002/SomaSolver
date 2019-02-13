package loaders.obj;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class OBJData
{
	@Getter
	private final int[] indices;

	@Getter
	private final float[] vertices;

	@Getter
	private final float[] textureCoords;

	@Getter
	private final float[] normals;
}
