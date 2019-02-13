package loaders;

import entities.GameObject;
import loaders.obj.OBJData;
import loaders.obj.OBJLoader;
import opengl.Vao;
import textures.Texture;
import utilities.ResourceFile;

public class GameObjectLoader
{
	public static GameObject loadFromOBJ(ResourceFile modelFile, ResourceFile textureFile)
	{
		OBJData modelData = OBJLoader.loadModel(modelFile);
		Vao model = createVao(modelData);
		Texture texture = loadTexture(textureFile);
		return new GameObject(model, texture);
	}

	private static Texture loadTexture(ResourceFile textureFile) {
		Texture diffuseTexture = Texture.newTexture(textureFile).anisotropic().create();
		return diffuseTexture;
	}

	private static Vao createVao(OBJData modelData) {
		Vao vao = Vao.create();
		vao.bind();
		vao.createIndexBuffer(modelData.getIndices());
		vao.createAttribute(0, modelData.getVertices(), 3);
		vao.createAttribute(1, modelData.getTextureCoords(), 2);
		vao.createAttribute(2, modelData.getNormals(), 3);
		vao.unbind();
		return vao;
	}

}
