package gui.text;


import launcher.GeneralSettings;
import utilities.ResourceFile;

public class FontFactory
{
	public static FontType CENTURY_FONT;

	public static void init()
	{
		CENTURY_FONT = new FontType(new ResourceFile(GeneralSettings.FONT_FOLDER, GeneralSettings.CENTURY_FONT_FILE), new ResourceFile(GeneralSettings.FONT_FOLDER, GeneralSettings.CENTURY_ATLAS));
	}
}
