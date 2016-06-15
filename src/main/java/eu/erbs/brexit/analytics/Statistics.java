package eu.erbs.brexit.analytics;

import java.awt.Color;
import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.uima.UIMAException;
import org.json.JSONException;

import com.kennycason.kumo.CollisionMode;
import com.kennycason.kumo.WordCloud;
import com.kennycason.kumo.WordFrequency;
import com.kennycason.kumo.bg.PixelBoundryBackground;
import com.kennycason.kumo.font.scale.LinearFontScalar;
import com.kennycason.kumo.nlp.FrequencyAnalyzer;
import com.kennycason.kumo.palette.LinearGradientColorPalette;

import eu.erbs.ambiverse.Ambiverse;
import eu.erbs.ambiverse.AmbiverseUtils;
import eu.erbs.dkpro.DKProUtils;

public class Statistics
{

	private static final Logger log = Logger.getLogger(Statistics.class.getName());

	private static boolean hasHeader = false;

	public static void main(String... args)
			throws IOException, UIMAException, OAuthSystemException, OAuthProblemException, JSONException,
			InterruptedException
	{
		log.info("Started counter.");

		File johnson = new File("src/main/resources/johnson.txt");
		File cameron = new File("src/main/resources/cameron.txt");
		File shakespeare = new File("src/main/resources/shakespeare.txt");
		File utopia = new File("src/main/resources/utopia.txt");

		// String johnsonText = FileUtils.readFileToString(johnson);
		// log.info("Johnson:\t" + johnson.length());
		// FileUtils.write(new File("src/main/resources/johnson.txt"), johnson);
		// johnson = Jsoup.parse(johnson).text();
		// log.info("Johnson:\t" + johnson.length());

		// String cameron = FileUtils.readFileToString(new
		// File("src/main/resources/cameron.txt"));
		// log.info("Cameron:\t" + cameron.length());
		// cameron = Jsoup.parse(cameron).text();
		// cameron.replaceAll("\\. ", "\\.\n");
		// FileUtils.write(new File("src/main/resources/cameron.txt"), cameron);
		// 
		// FileUtils.write(new File("src/main/resources/cameron.txt"),
		// cameron.replaceAll(". ", ".\n"));
		// log.info("Cameron:\t" + cameron.length());

		// prettyPrint("Cameron", getDKProStatistics(cameron));
		// prettyPrint("Johnson", getDKProStatistics(johnson));
		// prettyPrint("Shakespeare", getDKProStatistics(shakespeare));
		// prettyPrint("Utopia'", getDKProStatistics(utopia));

		// getAmbiverseEntities(cameron, 0);
		getAmbiverseEntities(johnson, 0);

		// createWordCloud(cameron);
		// createWordCloud(johnson);
	}

	private static List<String> getAmbiverseEntities(File politician, int offset)
			throws OAuthSystemException, OAuthProblemException, IOException, JSONException, InterruptedException
	{
		List<String> entities = new ArrayList<String>();
		List<String> lines = FileUtils.readLines(politician);
		String accessToken = Ambiverse.getAccessToken();
		List<String> entitesForLine;
		for (int i=offset;i<lines.size();i++){
			
			Thread.sleep(1000);
			log.info("Analyzing line " + i);
			entitesForLine = AmbiverseUtils.getEntities(lines.get(i), accessToken);
			FileUtils.write(new File("output/" + politician.getName() + ".entities"),
					(StringUtils.join(entitesForLine, " @@@ ") + "\n"), true);
			entities.addAll(entitesForLine);
		}

		return entities;
	}

	private static Map<String, Double> getDKProStatistics(File politician)
			throws UIMAException, OAuthSystemException, OAuthProblemException, IOException
	{
		Map<String, Double> statistics = new HashMap<String, Double>();

		statistics.putAll(DKProUtils.getLengthStatistics(FileUtils.readFileToString(politician)));

		statistics.putAll(DKProUtils.getReadabaility(FileUtils.readFileToString(politician)));

		return statistics;
	}

	private static void createWordCloud(File politician) throws IOException
	{
		final FrequencyAnalyzer frequencyAnalyzer = new FrequencyAnalyzer();
		frequencyAnalyzer.setStopWords(FileUtils.readLines(new File("src/main/resources/stopwords.txt")));
		frequencyAnalyzer.setWordFrequenciesToReturn(200);

		final List<WordFrequency> wordFrequencies = frequencyAnalyzer
				.load(politician);
		final Dimension dimension = new Dimension(550, 550);
		final WordCloud wordCloud = new WordCloud(dimension, CollisionMode.RECTANGLE);
		wordCloud.setPadding(0);
		wordCloud
				.setBackground(new PixelBoundryBackground(
						new File("src/main/resources/background/" + politician.getName() + ".png")));
		// wordCloud.setBackground(new RectangleBackground(dimension));
		wordCloud.setColorPalette(new LinearGradientColorPalette(Color.green, Color.blue, 8));
		wordCloud.setFontScalar(new LinearFontScalar(8, 40));
		wordCloud.setBackgroundColor(Color.white);

		wordCloud.build(wordFrequencies);
		wordCloud.writeToFile("output/wordcloud_" + politician.getName() + ".png");

	}

	private static void prettyPrint(String name, Map<String, Double> statistics)
	{
		if (!hasHeader)
		{
			String stringFormat = "%-11s %7s %7s %7s %7s %7s %7s %7s %7s %7s %7s %7s  %n";
			System.out.printf(stringFormat,
					"Politician",
					"tokens",
					"c/w",
					"w/s",
					"lexdiv",
					"flesch",
					"smog",
					"ari",
					"lix",
					"col_lia",
					"kincaid",
					"fog");
			hasHeader = true;
		}

		String format = "%-11s %7d %7.2f %7.2f %7.2f %7.2f %7.2f %7.2f %7.2f %7.2f %7.2f %7.2f %n";
		System.out.printf(format,
				name,
				statistics.get("tokens").intValue(),
				statistics.get("avg. word length"),
				statistics.get("avg. sentence length"),
				statistics.get("lexical diversity"),
				statistics.get("flesch"),
				statistics.get("smog"),
				statistics.get("ari"),
				statistics.get("lix"),
				statistics.get("coleman_liau"),
				statistics.get("kincaid"),
				statistics.get("fog"));

	}

}
