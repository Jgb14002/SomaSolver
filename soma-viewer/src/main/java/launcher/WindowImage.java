package launcher;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import static org.lwjgl.stb.STBImage.stbi_load;
import org.lwjgl.system.MemoryStack;

public class WindowImage
{
	private ByteBuffer image;
	private int width, height;

	WindowImage(int width, int heigh, ByteBuffer image)
	{
		this.image = image;
		this.height = heigh;
		this.width = width;
	}

	public static WindowImage loadImage(String path)
	{
		ByteBuffer image;
		int width, heigh;
		try (MemoryStack stack = MemoryStack.stackPush())
		{
			IntBuffer comp = stack.mallocInt(1);
			IntBuffer w = stack.mallocInt(1);
			IntBuffer h = stack.mallocInt(1);

			image = stbi_load(path, w, h, comp, 4);
			if (image == null)
			{

			}
			width = w.get();
			heigh = h.get();
		}
		return new WindowImage(width, heigh, image);
	}

	public ByteBuffer getImage()
	{
		return image;
	}

	public int getWidth()
	{
		return width;
	}

	public int getHeight()
	{
		return height;
	}
}