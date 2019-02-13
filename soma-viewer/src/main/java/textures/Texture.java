package textures;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import utilities.ResourceFile;

public class Texture
{
	public final int textureId;

	public Texture(int textureId)
	{
		this.textureId = textureId;
	}

	public static TextureBuilder newTexture(ResourceFile textureFile)
	{
		return new TextureBuilder(textureFile);
	}

	public void bindToUnit(int unit)
	{
		GL13.glActiveTexture(GL13.GL_TEXTURE0 + unit);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureId);
	}

	public void delete()
	{
		GL11.glDeleteTextures(textureId);
	}
}
