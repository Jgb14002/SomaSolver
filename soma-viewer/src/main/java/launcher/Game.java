package launcher;

import gui.text.FontFactory;
import input.Input;
import lombok.Getter;
import org.lwjgl.glfw.GLFW;
import renderers.MasterRenderer;
import scene.SceneLoader;

public class Game extends GameLoop
{

	@Getter
	private static GameWindow gameWindow;

	private MasterRenderer renderer;

	public Game(int width, int height, String title)
	{
		Game.gameWindow = new GameWindow(width, height, title);
		this.renderer = new MasterRenderer(Game.gameWindow.getCapabilities());
		init();
		start();
		cleanUp();
	}

	public static GameWindow getGameWindow()
	{
		return gameWindow;
	}

	public void start()
	{
		Game.gameWindow.show();
		super.run();
	}

	@Override
	protected void init()
	{
		FontFactory.init();
		SceneLoader.loadScene(SceneLoader.createDefaultScene());
	}

	@Override
	protected void input()
	{

		if (GLFW.glfwWindowShouldClose(Game.gameWindow.getWindowHandler()))
		{
			super.stop();
		}

		if (Input.isKeyDown(GLFW.GLFW_KEY_F1) && Input.isKeyDown(GLFW.GLFW_KEY_LEFT_CONTROL))
		{
			GeneralSettings.DEBUG_ENABLED = !GeneralSettings.DEBUG_ENABLED;
		}

		SceneLoader.getCurrentScene().processInput();

	}

	@Override
	protected void update()
	{
		SceneLoader.getCurrentScene().update();
	}

	@Override
	protected void render()
	{
		if (gameWindow.hasSizeChanged())
		{
			SceneLoader.getCurrentScene().getCamera().updatePerspective(gameWindow.getAspect());
		}

		renderer.render(SceneLoader.getCurrentScene());

		GLFW.glfwSwapBuffers(Game.gameWindow.getWindowHandler());
		GLFW.glfwPollEvents();

	}

	@Override
	protected void cleanUp()
	{
		SceneLoader.getCurrentScene().cleanUp();
	}


}
