package application;

import javafx.application.Application;

public class Launcher
{
	public static void main(String[] args)
	{
		final Thread controllerThread = new Thread(() -> launcher.Launcher.main(args));
		final Thread viewerThread = new Thread(() -> Application.launch(SomaController.class, args));

		controllerThread.start();
		viewerThread.start();
	}
}
