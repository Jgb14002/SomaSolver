package gui.text;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;


public class Line
{

	@Getter
	private List<Word> words = new ArrayList<>();
	@Getter
	private double maxLength;
	@Getter
	private double lineLength = 0;
	private double spaceSize;

	protected Line(double spaceWidth, double fontSize, double maxLength)
	{
		this.spaceSize = spaceWidth * fontSize;
		this.maxLength = maxLength;
	}

	protected boolean attemptToAddWord(Word word)
	{
		double additionalLength = word.getWidth();
		additionalLength += !words.isEmpty() ? spaceSize : 0;
		if (lineLength + additionalLength <= maxLength)
		{
			words.add(word);
			lineLength += additionalLength;
			return true;
		}
		else
		{
			return false;
		}
	}

}
