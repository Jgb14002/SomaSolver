package gui.text;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class Character
{

	@Getter(AccessLevel.PROTECTED)
	private int id;
	@Getter(AccessLevel.PROTECTED)
	private double xTextureCoord;
	@Getter(AccessLevel.PROTECTED)
	private double yTextureCoord;
	@Getter(AccessLevel.PROTECTED)
	private double xMaxTextureCoord;
	@Getter(AccessLevel.PROTECTED)
	private double yMaxTextureCoord;
	@Getter(AccessLevel.PROTECTED)
	private double xOffset;
	@Getter(AccessLevel.PROTECTED)
	private double yOffset;
	@Getter(AccessLevel.PROTECTED)
	private double sizeX;
	@Getter(AccessLevel.PROTECTED)
	private double sizeY;
	@Getter(AccessLevel.PROTECTED)
	private double xAdvance;

}
