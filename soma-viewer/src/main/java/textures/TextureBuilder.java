package textures;

import lombok.Getter;
import utilities.ResourceFile;

public class TextureBuilder
{

	@Getter
	private boolean clampEdges = false;
	@Getter
	private boolean mipmap = false;
	@Getter
	private boolean anisotropic = true;
	@Getter
	private boolean nearest = false;

	private ResourceFile file;

	protected TextureBuilder(ResourceFile textureFile)
	{
		this.file = textureFile;
	}

	public Texture create()
	{
		TextureData textureData = TextureUtils.decodeTextureFile(file);
		int textureId = TextureUtils.loadTextureToOpenGL(textureData, this);
		return new Texture(textureId);
	}

	public TextureBuilder clampEdges()
	{
		this.clampEdges = true;
		return this;
	}

	public TextureBuilder normalMipMap()
	{
		this.mipmap = true;
		this.anisotropic = false;
		return this;
	}

	public TextureBuilder nearestFiltering()
	{
		this.mipmap = false;
		this.anisotropic = false;
		this.nearest = true;
		return this;
	}

	public TextureBuilder anisotropic()
	{
		this.mipmap = true;
		this.anisotropic = true;
		return this;
	}

}
