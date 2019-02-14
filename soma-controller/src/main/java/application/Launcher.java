package application;

import javafx.application.Application;

public class Launcher
{
	public static void main(String[] args)
	{
		final Thread controllerThread = new Thread(() -> Application.launch(SomaController.class, args));
		controllerThread.setDaemon(true);
		controllerThread.start();

		launcher.Launcher.main(args);
	}
}
