package scene;

import com.google.common.eventbus.EventBus;
import events.ControllerLinkEvent;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SceneLoader
{
	@Getter
	private static Scene currentScene;

	@Getter
	private static EventBus eventBus = new EventBus();


	public static void linkController(Object controller)
	{
		eventBus.register(controller);
		eventBus.post(new ControllerLinkEvent());
	}


	public static void loadScene(Scene scene)
	{
		if(currentScene != null)
		{
			eventBus.unregister(currentScene);
		}

		currentScene = scene;
		currentScene.init();
		eventBus.register(currentScene);
	}

	public static Scene createDefaultScene()
	{
		final ICamera camera = new Camera();
		return new SomaScene(camera);
	}

}
