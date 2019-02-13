package gui;

import opengl.Vao;

public class ModelGenerator
{
	public static Vao createElementModel(UIShape shape)
	{
		final Vao vao = Vao.create();
		vao.bind();
		vao.createIndexBuffer(shape.getIndices());
		vao.createAttribute(0, shape.getPositions(), 2);
		vao.unbind();
		return vao;
	}

	public static Vao createTextCharModel(float[] positions, float[] textureCoords)
	{
		final Vao vao = Vao.create();
		vao.bind();
		vao.createAttribute(0, positions, 2);
		vao.createAttribute(1, textureCoords, 2);
		vao.setIndexCount(positions.length / 2);
		vao.unbind();
		return vao;
	}
}
