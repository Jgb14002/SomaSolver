package gui.text;

import gui.ModelGenerator;
import java.util.ArrayList;
import java.util.List;
import opengl.Vao;
import utilities.ResourceFile;

public class TextMeshCreator
{

	protected static final double LINE_HEIGHT = 0.03f;
	protected static final int SPACE_ASCII = 32;

	private MetaFile metaData;

	protected TextMeshCreator(ResourceFile fontFile)
	{
		metaData = new MetaFile(fontFile);
	}

	private static void addVertices(List<Float> vertices, double x, double y, double maxX, double maxY)
	{
		vertices.add((float) x);
		vertices.add((float) y);
		vertices.add((float) x);
		vertices.add((float) maxY);
		vertices.add((float) maxX);
		vertices.add((float) maxY);
		vertices.add((float) maxX);
		vertices.add((float) maxY);
		vertices.add((float) maxX);
		vertices.add((float) y);
		vertices.add((float) x);
		vertices.add((float) y);
	}

	private static void addTexCoords(List<Float> texCoords, double x, double y, double maxX, double maxY)
	{
		texCoords.add((float) x);
		texCoords.add((float) y);
		texCoords.add((float) x);
		texCoords.add((float) maxY);
		texCoords.add((float) maxX);
		texCoords.add((float) maxY);
		texCoords.add((float) maxX);
		texCoords.add((float) maxY);
		texCoords.add((float) maxX);
		texCoords.add((float) y);
		texCoords.add((float) x);
		texCoords.add((float) y);
	}

	private static float[] listToArray(List<Float> listOfFloats)
	{
		float[] array = new float[listOfFloats.size()];
		for (int i = 0; i < array.length; i++)
		{
			array[i] = listOfFloats.get(i);
		}
		return array;
	}

	protected Vao createModel(String text, float fontSize, float maxLineSize, boolean isCentered)
	{
		List<Line> lines = createStructure(text, fontSize, maxLineSize);
		return createModel(lines, fontSize, isCentered);
	}

	private List<Line> createStructure(String text, float fontSize, float maxLineSize)
	{
		char[] chars = text.toCharArray();
		List<Line> lines = new ArrayList<>();
		Line currentLine = new Line(metaData.getSpaceWidth(), fontSize, maxLineSize);
		Word currentWord = new Word(fontSize);
		for (char c : chars)
		{
			int ascii = (int) c;
			if (ascii == SPACE_ASCII)
			{
				boolean added = currentLine.attemptToAddWord(currentWord);
				if (!added)
				{
					lines.add(currentLine);
					currentLine = new Line(metaData.getSpaceWidth(), fontSize, maxLineSize);
					currentLine.attemptToAddWord(currentWord);
				}
				currentWord = new Word(fontSize);
				continue;
			}
			Character character = metaData.getCharacter(ascii);
			currentWord.addCharacter(character);
		}
		completeStructure(lines, currentLine, currentWord, fontSize, maxLineSize);
		return lines;
	}

	private void completeStructure(List<Line> lines, Line currentLine, Word currentWord, float fontSize, float maxLineSize)
	{
		boolean added = currentLine.attemptToAddWord(currentWord);
		if (!added)
		{
			lines.add(currentLine);
			currentLine = new Line(metaData.getSpaceWidth(), fontSize, maxLineSize);
			currentLine.attemptToAddWord(currentWord);
		}
		lines.add(currentLine);
	}

	private Vao createModel(List<Line> lines, float fontSize, boolean isCentered)
	{
		double cursorX = 0f;
		double cursorY = 0f;
		List<Float> vertices = new ArrayList<>();
		List<Float> textureCoords = new ArrayList<>();
		for (Line line : lines)
		{
			if (isCentered)
			{
				cursorX = (line.getMaxLength() - line.getLineLength()) / 2;
			}
			for (Word word : line.getWords())
			{
				for (Character letter : word.getCharacters())
				{
					addVerticesForCharacter(cursorX, cursorY, letter, fontSize, vertices);
					addTexCoords(textureCoords, letter.getXTextureCoord(), letter.getYTextureCoord(),
						letter.getXMaxTextureCoord(), letter.getYMaxTextureCoord());
						cursorX += letter.getXAdvance() * fontSize;
				}
				cursorX += metaData.getSpaceWidth() * fontSize;
			}
			cursorX = 0;
			cursorY += LINE_HEIGHT * fontSize;
		}

		return ModelGenerator.createTextCharModel(listToArray(vertices), listToArray(textureCoords));
	}

	private void addVerticesForCharacter(double cursorX, double cursorY, Character character, double fontSize,
										 List<Float> vertices)
	{
		double x = cursorX + (character.getXOffset() * fontSize);
		double y = cursorY + (character.getYOffset() * fontSize);
		double maxX = x + (character.getSizeX() * fontSize);
		double maxY = y + (character.getSizeY() * fontSize);
		double properX = (2 * x) - 1;
		double properY = (-2 * y) + 1;
		double properMaxX = (2 * maxX) - 1;
		double properMaxY = (-2 * maxY) + 1;
		addVertices(vertices, properX, properY, properMaxX, properMaxY);
	}

}
