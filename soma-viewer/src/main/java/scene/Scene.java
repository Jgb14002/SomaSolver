package scene;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import javax.imageio.ImageIO;
import launcher.GeneralSettings;
import lombok.Getter;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

public abstract class Scene
{
	@Getter
	private final ICamera camera;

	public Scene(ICamera camera)
	{
		this.camera = camera;
	}

	public abstract void init();

	public abstract void update();

	public abstract void render();

	public abstract void cleanUp();

	public abstract void processInput();

	public abstract void reset();

	public void saveScreenShot(File out)
	{
		final int width = GeneralSettings.WIDTH;
		final int height = GeneralSettings.HEIGHT;

		GL11.glReadBuffer(GL11.GL_FRONT);
		ByteBuffer buffer = BufferUtils.createByteBuffer(width * height * 4);
		GL11.glReadPixels(0, 0, width, height, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);

		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

		for (int x = 0; x < width; x++)
		{
			for (int y = 0; y < height; y++)
			{
				int i = (x + (width * y)) * 4;
				int r = buffer.get(i) & 0xFF;
				int g = buffer.get(i + 1) & 0xFF;
				int b = buffer.get(i + 2) & 0xFF;

				int mask = (r << 16) | (g << 8) | b;
				if(mask != 16777215)
					image.setRGB(x, height - (y + 1), (0xFF << 24) | (r << 16) | (g << 8) | b);
			}
		}

		try
		{
			ImageIO.write(image, "PNG", out);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
