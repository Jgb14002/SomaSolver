package input;

import com.google.common.eventbus.EventBus;
import events.MouseClickedEvent;
import launcher.GeneralSettings;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.joml.Vector2f;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;

@Slf4j
public class Input
{
	private static final int MAX_KEYS = 350;
	private static final int MAX_BUTTONS = 5;
	private static boolean[] keys = new boolean[MAX_KEYS];
	private static boolean[] buttons = new boolean[MAX_BUTTONS];
	@Getter
	private static Vector2f mousePosition;
	private static float dX;
	private static float dY;
	private static float dWheel;
	@Getter
	private static EventBus eventBus = new EventBus();

	static
	{
		for (int i = 0; i < keys.length; i++)
		{
			keys[i] = false;
		}

		for (int i = 0; i < buttons.length; i++)
		{
			buttons[i] = false;
		}

		mousePosition = new Vector2f(0, 0);
		dX = 0;
		dY = 0;
	}

	public static void keyboardCallback(int key, int scancode, int action, int mods)
	{
		if (GeneralSettings.DEBUG_ENABLED)
		{
			log.debug("KEY PRESSED: " + key);
		}

		keys[key] = action != GLFW_RELEASE;
	}

	public static void mouseButtonCallback(int button, int action, int mods)
	{
		if (GeneralSettings.DEBUG_ENABLED)
		{
			log.debug("MOUSE EVENT - BUTTON: " + button);
		}
		buttons[button] = action != GLFW_RELEASE;

		eventBus.post(new MouseClickedEvent(action,button));
	}

	public static void mouseMoveCallback(double xpos, double ypos)
	{
		if (GeneralSettings.DEBUG_ENABLED)
		{
			//log.debug("MOUSE IN POSITION: " + xpos + ", " + ypos);
		}

		dX = (float) xpos - mousePosition.x;
		dY = (float) ypos - mousePosition.y;

		mousePosition.x = (float) xpos;
		mousePosition.y = (float) ypos;
	}

	public static float getDX()
	{
		float result = dX;
		dX = 0;
		return result;
	}

	public static float getDY()
	{
		float result = dY;
		dY = 0;
		return result;
	}

	public static float getDWheel()
	{
		float result = dWheel;
		dWheel = 0;
		return result;
	}

	public static boolean isKeyDown(int key)
	{
		return keys[key];
	}

	public static boolean isKeyPressed(int key)
	{
		boolean result = keys[key];
		keys[key] = false;
		return result;
	}

	public static boolean isButtonDown(int button)
	{
		return buttons[button];
	}

	public static void mouseScrollCallback(double dx, double dy)
	{
		dWheel += (float) dy;
	}
}
