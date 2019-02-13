package gui.text;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
class TextMeshData
{
	@Getter
	private float[] vertexPositions;
	@Getter
	private float[] textureCoords;
}
