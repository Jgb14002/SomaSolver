package textures;

import java.nio.ByteBuffer;
import lombok.Getter;

public class TextureData
{
	@Getter
	private int width;
	@Getter
	private int height;
	@Getter
	private ByteBuffer buffer;

	public TextureData(ByteBuffer buffer, int width, int height)
	{
		this.buffer = buffer;
		this.width = width;
		this.height = height;
	}

}
