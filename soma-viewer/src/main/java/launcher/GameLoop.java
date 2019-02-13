package launcher;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class GameLoop
{
	private static final long SECOND_IN_NANOS = 1_000_000_000L;
	private static final long MAX_UPDATE_TIME = SECOND_IN_NANOS / GeneralSettings.UPS;
	private static final long MAX_FPS_TIME = SECOND_IN_NANOS / GeneralSettings.FPS_CAP;
	private static int currentUPS;
	private static int currentFPS;
	private static int gameUpdateTime;
	private static int gameRenderTime;
	private boolean isRunning = false;
	private long overSleep = 0;

	public static int getCurrentUPS()
	{
		return currentUPS;
	}

	public static int getCurrentFPS()
	{
		return currentFPS;
	}

	public static int getGameUpdateTime()
	{
		return gameUpdateTime;
	}

	public static int getGameRenderTime()
	{
		return gameRenderTime;
	}

	protected void run()
	{

		int updates = 0;
		int frames = 0;
		long timer = System.nanoTime();

		long beforeTime = timer;
		long afterTime = timer;

		long updateTime = 0;
		long renderTime = 0;

		log.info("Initializing maine game loop");

		isRunning = true;
		while (isRunning)
		{
			long deltaTime = afterTime - beforeTime;
			if (deltaTime >= MAX_UPDATE_TIME - deltaTime)
			{
				updateTime = System.nanoTime();

				this.input();
				this.update();
				updates++;

				updateTime = System.nanoTime() - updateTime;
				GameLoop.gameUpdateTime = (int) (updateTime / 1_000_000L);
				beforeTime = System.nanoTime() - (deltaTime - MAX_UPDATE_TIME + updateTime);
			}
			else
			{
				renderTime = System.nanoTime();

				this.render();
				frames++;

				renderTime = System.nanoTime() - renderTime;
				GameLoop.gameRenderTime = (int) (renderTime / 1_000_000L);

				if (renderTime < MAX_FPS_TIME - updateTime)
				{
					long diff = MAX_FPS_TIME - renderTime - updateTime - overSleep;
					if (diff > 1000)
					{
						this.sleep(diff);
					}
				}
			}

			if (System.nanoTime() - timer >= SECOND_IN_NANOS)
			{
				if (GeneralSettings.DEBUG_ENABLED)
				{
					log.debug("UPS: " + updates + "\tFPS: " + frames);
				}

				timer = System.nanoTime();

				currentUPS = updates;
				currentFPS = frames;

				updates = 0;
				frames = 0;
			}
			afterTime = System.nanoTime();
		}

	}

	protected void stop()
	{
		isRunning = false;
	}

	protected abstract void init();

	protected abstract void input();

	protected abstract void update();

	protected abstract void render();

	protected abstract void cleanUp();

	private void sleep(long nanos)
	{
		try
		{
			long beforeSleep = System.nanoTime();
			Thread.sleep(nanos / 1_000_000L, (int) (nanos % 1_000_000L));
			this.overSleep = System.nanoTime() - beforeSleep - nanos;
		}
		catch (Exception e)
		{
			log.debug("Error sleeping between frames and updates" + e.getMessage());
		}
	}
}

