package entities;

import java.util.Arrays;
import java.util.Optional;
import lombok.Getter;
import org.joml.Vector4f;

public enum CubeColor
{
	ORANGE(0, new Vector4f(1f, 0.674f, 0.341f, 1f)),
	BLUE(1, new Vector4f(0.341f, 0.537f, 1f, 1f)),
	RED(2, new Vector4f(1f, 0.341f, 0.341f, 1f)),
	GREEN(3, new Vector4f(0.454f, 1f, 0.341f, 1f)),
	WHITE(4, new Vector4f(1f, 1f, 1f, 1f)),
	YELLOW(5, new Vector4f(1f, 0.941f, 0.341f, 1f)),
	PURPLE(6, new Vector4f(0.803f, 0.341f, 1f, 1f));

	@Getter
	private final Vector4f color;

	private final int index;

	CubeColor(int index, Vector4f color)
	{
		this.index = index;
		this.color = color;
	}


	public static CubeColor getColorForIndex(int index)
	{
		Optional<CubeColor> pieceColor = Arrays.asList(values()).stream().filter(color -> color.index == index).findFirst();
		return pieceColor.orElse(WHITE);
	}
}
