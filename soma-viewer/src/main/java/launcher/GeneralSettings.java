package launcher;


import org.joml.Vector4f;

public class GeneralSettings
{
	public static final int WIDTH = 1280;
	public static final int HEIGHT = 960;
	public static final int UPS = 100;
	public static final int FPS_CAP = 200;
	public static final int GL_SWAP_INTERVALS = 1;
	public static final int ANTI_ALIASING_SAMPLES = 4;
	public static final boolean WINDOW_FULLSCREEN = false;
	public static final String MODEL_FOLDER = "models";
	public static final String FONT_FOLDER = "fonts";
	public static final String CENTURY_ATLAS = "century.png";
	public static final String CENTURY_FONT_FILE = "century.fnt";
	public static final String TEXTURE_FOLDER = "textures";
	public static final String SHADER_FOLDER = "shaders";
	public static final String CUBE_MODEL = "block.obj";
	public static final String FLOOR_MODEL = "floor.obj";
	public static final String AXIS_MODEL = "axis.obj";
	public static final String CUBE_TEXTURE = "tile.png";
	public static final String FLOOR_TEXTURE = "wood.png";
	public static final float WORLD_GRID_SIZE = 2.0f;
	public static final String TITLE = "Soma Solver - Viewer";
	public static final Vector4f UI_MAIN_COLOR = new Vector4f(.17f,.17f,.17f, .5f);
	public static final Vector4f UI_BORDER_COLOR = new Vector4f(.33f,.33f,.33f,.5f);
	public static final Vector4f UI_HOVER_COLOR = new Vector4f(0,0,0,.6f);
	public static boolean DEBUG_ENABLED = false;
}
