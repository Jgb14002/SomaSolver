package gui.text;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;

class Word
{

	@Getter
	private List<Character> characters = new ArrayList<>();
	@Getter
	private double width = 0;
	private final double fontSize;

	Word(double fontSize)
	{
		this.fontSize = fontSize;
	}

	protected void addCharacter(Character character)
	{
		characters.add(character);
		width += character.getXAdvance() * fontSize;
	}

}
