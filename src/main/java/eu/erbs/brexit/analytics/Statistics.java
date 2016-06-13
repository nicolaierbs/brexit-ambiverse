package eu.erbs.brexit.analytics;

import java.awt.Color;
import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.uima.UIMAException;

import com.kennycason.kumo.CollisionMode;
import com.kennycason.kumo.WordCloud;
import com.kennycason.kumo.WordFrequency;
import com.kennycason.kumo.bg.PixelBoundryBackground;
import com.kennycason.kumo.font.scale.LinearFontScalar;
import com.kennycason.kumo.nlp.FrequencyAnalyzer;
import com.kennycason.kumo.palette.LinearGradientColorPalette;

public class Statistics
{

	private static final Logger log = Logger.getLogger(Statistics.class.getName());

	public static void main(String... args)
			throws IOException, UIMAException, OAuthSystemException, OAuthProblemException
	{
		log.info("Started counter.");

		String johnson = FileUtils.readFileToString(new File("src/main/resources/johnson.txt"));
		// log.info("Johnson:\t" + johnson.length());
		// FileUtils.write(new File("src/main/resources/johnson.txt"), johnson);
		// johnson = Jsoup.parse(johnson).text();
		// log.info("Johnson:\t" + johnson.length());

		String cameron = FileUtils.readFileToString(new File("src/main/resources/cameron.txt"));
		// log.info("Cameron:\t" + cameron.length());
		// cameron = Jsoup.parse(cameron).text();
		// cameron.replaceAll("\\. ", "\\.\n");
		// FileUtils.write(new File("src/main/resources/cameron.txt"), cameron);
		// 
		// FileUtils.write(new File("src/main/resources/cameron.txt"),
		// cameron.replaceAll(". ", ".\n"));
		// log.info("Cameron:\t" + cameron.length());

		// System.out.println("---------");
		// System.out.println("Length:");
		// System.out.println("Cameron:\t" +
		// DKProUtils.getLengthStatistics(cameron));
		// System.out.println("Johnson:\t" +
		// DKProUtils.getLengthStatistics(johnson));
		//
		// System.out.println("---------");
		// System.out.println("Readability:");
		// System.out.println("Cameron:\t" +
		// DKProUtils.getReadabaility(cameron));
		// System.out.println("Johnson:\t" +
		// DKProUtils.getReadabaility(johnson));

		// System.out.println("---------");
		// System.out.println("Ambiverse:");
		// System.out.println("Cameron:\t" +
		// StringUtils.join(AmbiverseUtils.getEntities(cameron), "\t"));
		// System.out.println("Johnson:\t" +
		// StringUtils.join(AmbiverseUtils.getEntities(johnson), "\t"));

		createWordCloud("johnson");
		createWordCloud("cameron");
	}

	private static void createWordCloud(String name) throws IOException
	{
		final FrequencyAnalyzer frequencyAnalyzer = new FrequencyAnalyzer();
		frequencyAnalyzer.setStopWords(FileUtils.readLines(new File("src/main/resources/stopwords.txt")));
		final List<WordFrequency> wordFrequencies = frequencyAnalyzer
				.load(new File("src/main/resources/" + name + ".txt"));
		final Dimension dimension = new Dimension(550, 550);
		final WordCloud wordCloud = new WordCloud(dimension, CollisionMode.RECTANGLE);
		wordCloud.setPadding(0);
		wordCloud
				.setBackground(new PixelBoundryBackground(new File("src/main/resources/background/" + name + ".png")));
		// wordCloud.setBackground(new RectangleBackground(dimension));
		wordCloud.setColorPalette(new LinearGradientColorPalette(Color.magenta, Color.blue, 5));
		wordCloud.setFontScalar(new LinearFontScalar(8, 40));
		wordCloud.setBackgroundColor(Color.white);

		wordCloud.build(wordFrequencies);
		wordCloud.writeToFile("output/wordcloud_" + name + ".png");

	}
	
}
