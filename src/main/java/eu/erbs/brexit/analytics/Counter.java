package eu.erbs.brexit.analytics;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;
import org.apache.uima.UIMAException;

import eu.erbs.dkpro.DKProUtils;

public class Counter
{

	private static final Logger log = Logger.getLogger(Counter.class.getName());

	public static void main(String... args) throws IOException, UIMAException
	{
		log.info("Started counter.");

		String johnson = FileUtils.readFileToString(new File("src/main/resources/johnson.txt"));
		log.info("Johnson:\t" + johnson.length());
		// FileUtils.write(new File("src/main/resources/johnson.txt"), johnson);
		// johnson = Jsoup.parse(johnson).text();
		// log.info("Johnson:\t" + johnson.length());

		String cameron = FileUtils.readFileToString(new File("src/main/resources/cameron.txt"));
		log.info("Cameron:\t" + cameron.length());
		// cameron = Jsoup.parse(cameron).text();
		// cameron.replaceAll("\\. ", "\\.\n");
		// FileUtils.write(new File("src/main/resources/cameron.txt"), cameron);
		// 
		// FileUtils.write(new File("src/main/resources/cameron.txt"),
		// cameron.replaceAll(". ", ".\n"));
		// log.info("Cameron:\t" + cameron.length());
		
		System.out.println("Cameron:\t" + DKProUtils.getReadabaility(cameron));
		System.out.println("Johnson:\t" + DKProUtils.getReadabaility(johnson));
	}
	
}
