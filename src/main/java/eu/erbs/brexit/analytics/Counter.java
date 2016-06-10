package eu.erbs.brexit.analytics;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;

public class Counter
{

	private static final Logger log = Logger.getLogger(Counter.class.getName());

	public static void main(String... args) throws IOException
	{
		log.info("Started counter.");
		String johnson = FileUtils.readFileToString(new File("src/main/resources/johnson.txt"));
		log.info("Johnson:\t" + johnson.length());
		String cameron = FileUtils.readFileToString(new File("src/main/resources/cameron.txt"));
		log.info("Cameron:\t" + cameron.length());
	}

}
