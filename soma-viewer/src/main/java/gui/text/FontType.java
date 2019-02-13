package gui.text;

import lombok.Getter;
import opengl.Vao;
import textures.Texture;
import utilities.ResourceFile;

public class FontType
{

	@Getter
	private Texture textureAtlas;
	private TextMeshCreator loader;

	public FontType(ResourceFile fontFile, ResourceFile textureAtlas)
	{
		this.textureAtlas = Texture.newTexture(textureAtlas).create();
		this.loader = new TextMeshCreator(fontFile);
	}

	public Vao loadText(String text, float fontSize, float maxLineSize, boolean isCentered)
	{
		return loader.createModel(text, fontSize, maxLineSize, isCentered);
	}

}
