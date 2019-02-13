package launcher;

import lombok.extern.slf4j.Slf4j;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import org.slf4j.LoggerFactory;


@Slf4j
public class Launcher
{

	public static void main(String[] args)
	{
		final Logger logger = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
		if(GeneralSettings.DEBUG_ENABLED)
			logger.setLevel(Level.DEBUG);

		new Game(GeneralSettings.WIDTH, GeneralSettings.HEIGHT, GeneralSettings.TITLE);
	}

}


