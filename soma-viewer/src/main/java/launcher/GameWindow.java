package launcher;

import input.Input;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import lombok.extern.slf4j.Slf4j;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import static org.lwjgl.glfw.GLFW.GLFW_CLIENT_API;
import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_VERSION_MAJOR;
import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_VERSION_MINOR;
import static org.lwjgl.glfw.GLFW.GLFW_DEPTH_BITS;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_API;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_CORE_PROFILE;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_FORWARD_COMPAT;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_PROFILE;
import static org.lwjgl.glfw.GLFW.GLFW_SAMPLES;
import static org.lwjgl.glfw.GLFW.GLFW_TRUE;
import static org.lwjgl.glfw.GLFW.glfwSetWindowIcon;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLCapabilities;
import org.lwjgl.stb.STBImage;
import static org.lwjgl.system.MemoryUtil.NULL;
import static org.lwjgl.system.MemoryUtil.memAllocInt;
import static org.lwjgl.system.MemoryUtil.memFree;
import utilities.ResourceFile;

@Slf4j
public class GameWindow
{
	private long handler;

	private int width;

	private int height;

	private String title;

	private GLCapabilities glCapabilities;

	private boolean hasSizeChanged;


	public GameWindow(final int width, final int height, final String title)
	{
		this(width, height, title, new Input());
	}

	public GameWindow(final int width, final int height, final String title, Input input)
	{
		this.width = width;
		this.height = height;
		this.title = title;

		this.initGameWindow();
		this.initWindowCallbacks();
		this.initControls();
	}


	private void initGameWindow()
	{
		log.info("Initializing system");
		boolean glfwStatus = GLFW.glfwInit();
		if (!glfwStatus)
		{
			throw new IllegalStateException("Unable to initialize GLFW");
		}

		GLFW.glfwDefaultWindowHints();
		GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE);
		GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GLFW.GLFW_FALSE);
		glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
		glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 2);
		glfwWindowHint(GLFW_CLIENT_API, GLFW_OPENGL_API);
		glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
		glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GLFW_TRUE);
		glfwWindowHint(GLFW_DEPTH_BITS, 24);
		GLFW.glfwWindowHint(GLFW_SAMPLES, GeneralSettings.ANTI_ALIASING_SAMPLES);

		long monitor = NULL;
		if (GeneralSettings.WINDOW_FULLSCREEN)
		{
			monitor = GLFW.glfwGetPrimaryMonitor();
		}

		handler = GLFW.glfwCreateWindow(width, height, title, monitor, NULL);
		if (handler == NULL)
		{
			throw new RuntimeException("Error creating GLFW window");
		}

		GLFW.glfwMakeContextCurrent(handler);
		glCapabilities = GL.createCapabilities();

		GLFW.glfwSwapInterval(GeneralSettings.GL_SWAP_INTERVALS);

		try
		{
			setIcon(new ResourceFile("icons", "icon.png"));
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

	}


	private void initWindowCallbacks()
	{

		log.info("Initializing window callbacks");

		GLFW.glfwSetWindowSizeCallback(handler, (window, width, height) -> {
			this.width = width;
			this.height = height;
			this.hasSizeChanged = true;

		});

	}

	private void initControls()
	{

		log.info("Initializing input callbacks");

		GLFW.glfwSetKeyCallback(handler, (window, key, scancode, action, mods) -> Input.keyboardCallback(key, scancode, action, mods));

		GLFW.glfwSetMouseButtonCallback(handler, (window, button, action, mods) -> Input.mouseButtonCallback(button, action, mods));

		GLFW.glfwSetCursorPosCallback(handler, (window, xpos, ypos) -> Input.mouseMoveCallback(xpos, ypos));

		GLFW.glfwSetScrollCallback(handler, (win, dx, dy) -> Input.mouseScrollCallback(dx, dy));

	}

	public long getWindowHandler()
	{
		return handler;
	}

	public void show()
	{
		GLFW.glfwShowWindow(handler);
	}

	public GLCapabilities getCapabilities()
	{
		return this.glCapabilities;
	}

	public boolean hasSizeChanged()
	{
		return hasSizeChanged;
	}

	public float getAspect()
	{
		hasSizeChanged = false;
		GL11.glViewport(0, 0, width, height);
		return ((float) this.width) / this.height;
	}

	public void setIcon(ResourceFile res) throws Exception{
		IntBuffer w = memAllocInt(1);
		IntBuffer h = memAllocInt(1);
		IntBuffer comp = memAllocInt(1);

		// Icons
		{
			ByteBuffer icon16;
			ByteBuffer icon32;
			try {
				icon16 = res.getByteBuffer();
				icon32 = res.getByteBuffer();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}

			try ( GLFWImage.Buffer icons = GLFWImage.malloc(2) ) {
				ByteBuffer pixels16 = STBImage.stbi_load_from_memory(icon16, w, h, comp, 4);
				icons
					.position(0)
					.width(w.get(0))
					.height(h.get(0))
					.pixels(pixels16);

				ByteBuffer pixels32 = STBImage.stbi_load_from_memory(icon32, w, h, comp, 4);
				icons
					.position(1)
					.width(w.get(0))
					.height(h.get(0))
					.pixels(pixels32);

				icons.position(0);
				glfwSetWindowIcon(handler, icons);

				STBImage.stbi_image_free(pixels32);
				STBImage.stbi_image_free(pixels16);
			}
		}

		memFree(comp);
		memFree(h);
		memFree(w);

	}
}
