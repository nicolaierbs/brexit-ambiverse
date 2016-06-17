package eu.erbs.brexit.analytics;

import java.awt.Color;
import java.awt.Dimension;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.uima.UIMAException;
import org.json.JSONException;

import com.kennycason.kumo.CollisionMode;
import com.kennycason.kumo.PolarBlendMode;
import com.kennycason.kumo.PolarWordCloud;
import com.kennycason.kumo.WordCloud;
import com.kennycason.kumo.WordFrequency;
import com.kennycason.kumo.bg.CircleBackground;
import com.kennycason.kumo.bg.PixelBoundryBackground;
import com.kennycason.kumo.font.scale.LinearFontScalar;
import com.kennycason.kumo.font.scale.SqrtFontScalar;
import com.kennycason.kumo.nlp.FrequencyAnalyzer;
import com.kennycason.kumo.palette.LinearGradientColorPalette;

import de.tudarmstadt.ukp.dkpro.core.api.frequency.util.FrequencyDistribution;
import eu.erbs.ambiverse.Ambiverse;
import eu.erbs.ambiverse.AmbiverseUtils;
import eu.erbs.ambiverse.model.EntityRepresentation;
import eu.erbs.dkpro.DKProUtils;

public class Statistics
{

	private static final Logger log = Logger.getLogger(Statistics.class.getName());

	private static boolean hasHeader = false;

	public static void main(String... args)
			throws IOException, UIMAException, OAuthSystemException, OAuthProblemException, JSONException,
			InterruptedException, ClassNotFoundException
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
		// getAmbiverseEntities(johnson, 0);

		// getEntityRepresentations();
		//
		analyzeEntityRepresentations();

		// createWordCloud(cameron);
		// createWordCloud(johnson);
	}

	@SuppressWarnings("unchecked")
	private static void analyzeEntityRepresentations() throws IOException, ClassNotFoundException
	{
		List<EntityRepresentation> entityRepresentations;
		FileInputStream fileIn = new FileInputStream("output/ambiverse/entities.representations.ser");
		ObjectInputStream in = new ObjectInputStream(fileIn);
		entityRepresentations = (List<EntityRepresentation>) in.readObject();
		in.close();
		fileIn.close();

		log.info("Analyze " + entityRepresentations.size() + " entity representations");

		List<String> johnsonEntities = FileUtils.readLines(new File("output/ambiverse/johnson.entities"));
		List<String> cameronEntities = FileUtils.readLines(new File("output/ambiverse/cameron.entities"));

		Map<String, EntityRepresentation> representationMap = getEntityRepresentationMap(entityRepresentations);

		List<String> johnsonEntityList = getEntityList(johnsonEntities, representationMap);
		List<String> cameronEntityList = getEntityList(cameronEntities, representationMap);
		List<String> johnsonCategoryList = getCategoryList(johnsonEntities, representationMap);
		List<String> cameronCategoryList = getCategoryList(cameronEntities, representationMap);
		List<String> johnsonLocationList = getLocationList(johnsonEntities, representationMap);
		List<String> cameronLocationList = getLocationList(cameronEntities, representationMap);

		createDifferenceWordCloud(johnsonEntityList, cameronEntityList, "entities");
		createDifferenceWordCloud(johnsonCategoryList, cameronCategoryList, "categories");

		log.info("Johnson Entities: \t" + johnsonEntityList.size());
		log.info("Cameron Entities: \t" + cameronEntityList.size());
		log.info("Johnson Categories:\t" + johnsonCategoryList.size());
		log.info("Cameron Categories:\t" + cameronCategoryList.size());
		log.info("Johnson Locations:\t" + johnsonLocationList.size());
		log.info("Cameron Locations:\t" + cameronLocationList.size());

		FrequencyDistribution<String> johnsonEntityFrequencies = new FrequencyDistribution<String>(johnsonEntityList);
		FrequencyDistribution<String> cameronEntityFrequencies = new FrequencyDistribution<String>(cameronEntityList);

		FrequencyDistribution<String> johnsonCategoryFrequencies = new FrequencyDistribution<String>(
				johnsonCategoryList);
		FrequencyDistribution<String> cameronCategoryFrequencies = new FrequencyDistribution<String>(
				cameronCategoryList);

		FrequencyDistribution<String> johnsonLocationFrequencies = new FrequencyDistribution<String>(
				johnsonLocationList);
		FrequencyDistribution<String> cameronLocationFrequencies = new FrequencyDistribution<String>(
				cameronLocationList);

		printFrequencyTable(
				johnsonEntityFrequencies, cameronEntityFrequencies,
				johnsonCategoryFrequencies, cameronCategoryFrequencies,
				johnsonLocationFrequencies, cameronLocationFrequencies,
				50);

		// createWordCloud(johnsonEntityList, "johnson", "JohnsonEntity");
		// createWordCloud(cameronEntityList, "cameron", "CameronEntity");
		// createWordCloud(johnsonCategoryList, "johnson", "JohnsonCategory");
		// createWordCloud(cameronCategoryList, "cameron", "CameronCategory");

	}

	private static void printFrequencyTable(
			FrequencyDistribution<String> johnsonEntityFrequencies,
			FrequencyDistribution<String> cameronEntityFrequencies,
			FrequencyDistribution<String> johnsonCategoryFrequencies,
			FrequencyDistribution<String> cameronCategoryFrequencies,
			FrequencyDistribution<String> johnsonLocationFrequencies,
			FrequencyDistribution<String> cameronLocationFrequencies,
			int i)
	{
		String headerFormat = "%2s || %-45s %6s | %-45s %6s || %-45s %6s | %-45s %6s || %-45s %6s | %-45s %6s |   %n";
		System.out.printf(headerFormat,
				"#",
				"Johnson Entity",
				"%",
				"Cameron Entity",
				"%",
				"Johnson Category",
				"%",
				"Cameron Category",
				"%",
				"Johnson Location",
				"%",
				"Cameron Location",
				"%");

		List<String> jE = johnsonEntityFrequencies.getMostFrequentSamples(i);
		List<String> cE = cameronEntityFrequencies.getMostFrequentSamples(i);
		List<String> jC = johnsonCategoryFrequencies.getMostFrequentSamples(i);
		List<String> cC = cameronCategoryFrequencies.getMostFrequentSamples(i);
		List<String> jL = johnsonLocationFrequencies.getMostFrequentSamples(i);
		List<String> cL = cameronLocationFrequencies.getMostFrequentSamples(i);

		for (int n = 0; n < i; n++)
		{
			String format = "%2d || %-45s %2.4f | %-45s %2.4f || %-45s %2.4f | %-45s %2.4f || %-45s %2.4f | %-45s %2.4f |   %n";
			System.out.printf(format,
					n,
					jE.get(n),
					(johnsonEntityFrequencies.getCount(jE.get(n)) / (double) johnsonEntityFrequencies.getN()),
					cE.get(n),
					(cameronEntityFrequencies.getCount(cE.get(n)) / (double) cameronEntityFrequencies.getN()),
					jC.get(n),
					(johnsonCategoryFrequencies.getCount(jC.get(n)) / (double) johnsonCategoryFrequencies.getN()),
					cC.get(n),
					(cameronCategoryFrequencies.getCount(cC.get(n)) / (double) cameronCategoryFrequencies.getN()),
					jL.get(n),
					(johnsonLocationFrequencies.getCount(jL.get(n)) / (double) johnsonLocationFrequencies.getN()),
					cL.get(n),
					(cameronLocationFrequencies.getCount(cL.get(n)) / (double) cameronLocationFrequencies.getN()));
			// String format = "%d, %s, %2.2f, %s, %2.2f%n";
			// System.out.printf(format,
			// (n + 1),
			// cE.get(n),
			// (100 * cameronEntityFrequencies.getCount(cE.get(n)) / (double)
			// cameronEntityFrequencies.getN()),
			// jE.get(n),
			// (100 * johnsonEntityFrequencies.getCount(jE.get(n)) / (double)
			// johnsonEntityFrequencies.getN()));
		}

	}

	private static List<String> getEntityList(List<String> entities,
			Map<String, EntityRepresentation> representationMap)
	{
		List<String> entityList = new ArrayList<String>();
		for (String entity : entities)
		{
			if (representationMap.get(entity) != null)
			{
				entityList.add(representationMap.get(entity).getName());
			}
			else
			{
				log.warning("No entry for " + entity);
			}
		}
		return entityList;
	}

	private static List<String> getCategoryList(List<String> entities,
			Map<String, EntityRepresentation> representationMap)
	{
		List<String> categoryList = new ArrayList<String>();
		for (String entity : entities)
		{
			// log.info(StringUtils.join(representationMap.get(entity).getCategoryNames(),
			// ", "));
			categoryList.addAll(
					representationMap.get(entity).getCategoryNames());
		}

		List<String> notNullCategoryList = new ArrayList<String>();
		for (String category : categoryList)
		{
			if (category != null && category.length() > 1)
			{
				notNullCategoryList.add(category);
			}
		}
		return notNullCategoryList;
	}

	private static List<String> getLocationList(List<String> entities,
			Map<String, EntityRepresentation> representationMap)
	{
		List<String> countryList = new ArrayList<String>();
		for (String entity : entities)
		{
			// log.info(StringUtils.join(representationMap.get(entity).getCategoryNames(),
			// ", "));
			if (representationMap.get(entity).getCategoryNames().contains("yagoPermanentlyLocatedEntity")
					|| representationMap.get(entity).getCategoryNames().contains("yagoGeoEntity")
					|| representationMap.get(entity).getCategoryNames().contains("region")
					|| representationMap.get(entity).getCategoryNames().contains("location")
					|| representationMap.get(entity).getCategoryNames().contains("Countries in Europe")
					|| representationMap.get(entity).getCategoryNames()
							.contains("English-speaking countries and territories")
					|| representationMap.get(entity).getCategoryNames().contains("Island countries")
					|| representationMap.get(entity).getCategoryNames().contains("British Islands")
					|| representationMap.get(entity).getCategoryNames().contains("Germanic countries and territories")
					|| representationMap.get(entity).getCategoryNames().contains("Continents")
					|| representationMap.get(entity).getCategoryNames()
							.contains("German-speaking countries and territories")
					|| representationMap.get(entity).getCategoryNames().contains("colony")
					|| representationMap.get(entity).getCategoryNames().contains("Former British colonies")
					|| representationMap.get(entity).getCategoryNames().contains("Capitals in Europe")
					|| representationMap.get(entity).getCategoryNames().contains("area")
					|| representationMap.get(entity).getCategoryNames().contains("capital")
					|| representationMap.get(entity).getCategoryNames().contains("municipality")
					|| representationMap.get(entity).getCategoryNames().contains("Autonomous regions"))
			{
				if (!representationMap.get(entity).getName().equals("Labour Party (UK)")
						&& !representationMap.get(entity).getName().equals("Conservative Party (UK)")
						&& !representationMap.get(entity).getName().equals("Bloomberg L.P.")
						&& !representationMap.get(entity).getName().equals("National Health Service")
						&& !representationMap.get(entity).getName().equals("United Nations Security Council")
						&& !representationMap.get(entity).getName().equals("Battle of Britain")
						&& !representationMap.get(entity).getName().equals("International Monetary Fund")
						&& !representationMap.get(entity).getName().equals("Home Office")
						&& !representationMap.get(entity).getName().equals("European Council")
						&& !representationMap.get(entity).getName().equals("European Union")
						&& !representationMap.get(entity).getName().equals("National Health Service (England)")
						&& !representationMap.get(entity).getName().equals("National Front (France)")
						&& !representationMap.get(entity).getName().equals("Palestinian National Authority")
						&& !representationMap.get(entity).getName().equals("Home Secretary")
						&& !representationMap.get(entity).getName().equals("World Trade Organization")
						&& !representationMap.get(entity).getName().equals("European Economic Community")
						&& !representationMap.get(entity).getName().equals("United Nations")
						&& !representationMap.get(entity).getName().equals("Recording Industry Association of America"))
				{
					countryList.add(representationMap.get(entity).getName());
				}
			}
		}
		return countryList;

	}

	private static Map<String, EntityRepresentation> getEntityRepresentationMap(
			List<EntityRepresentation> entityRepresentations)
	{
		Map<String, EntityRepresentation> entityRepresentationMap = new HashMap<String, EntityRepresentation>();

		for (EntityRepresentation representation : entityRepresentations)
		{
			entityRepresentationMap.put(representation.getId(), representation);
		}

		return entityRepresentationMap;
	}

	private static void getEntityRepresentations()
			throws IOException, OAuthSystemException, OAuthProblemException, InterruptedException, FileNotFoundException
	{
		List<String> entities = new ArrayList<String>(
				new HashSet<String>(FileUtils.readLines(new File("output/ambiverse/entities.set"))));
		log.info(entities.size() + " entities");

		List<EntityRepresentation> entityRepresentations = new ArrayList<EntityRepresentation>();
		int step = 50;
		for (int i = 0; i < entities.size(); i += step)
		{

			log.fine("Getting entity representations names @ i=" + i);
			entityRepresentations.addAll(
					AmbiverseUtils.getEntityRepresentations(entities.subList(i, Math.min(i + step, entities.size()))));

		}

		log.info(entityRepresentations.size() + " entity representations");

		List<String> categoryIds = new ArrayList<String>(getCategoryIdSet(entityRepresentations));
		log.info(categoryIds.size() + " categoryIds");
		log.info(StringUtils.join(categoryIds, ", "));
		Thread.sleep(1000);

		Map<String, String> categoryNames = new HashMap<String, String>();

		step = 200;
		String accessToken = Ambiverse.getAccessToken();

		for (int i = 0; i < categoryIds.size(); i += step)
		{

			log.fine("Getting category names @ i=" + i);
			categoryNames.putAll(
					AmbiverseUtils.getCategoryNames(
							categoryIds.subList(i, Math.min(i + step, categoryIds.size())),
							accessToken));

		}

		// add category names to EntityRepresentation
		addCategoryNames(entityRepresentations, categoryNames);
		log.info(entityRepresentations.size() + " entity representations");

		// serialize EntityRepresentation
		for (EntityRepresentation entityRepresentation : entityRepresentations)
		{
			FileUtils.write(
					new File("output/ambiverse/entities.representations"),
					entityRepresentation.toString() + "\n",
					true);
		}
		FileOutputStream fileOut = new FileOutputStream("output/ambiverse/entities.representations.ser");
		ObjectOutputStream out = new ObjectOutputStream(fileOut);
		out.writeObject(entityRepresentations);
		out.close();
		fileOut.close();
	}

	private static void addCategoryNames(List<EntityRepresentation> entityRepresentations,
			Map<String, String> categoryNameMap)
	{
		List<String> categoryNames;
		for (EntityRepresentation entityRepresentation : entityRepresentations)
		{
			categoryNames = new ArrayList<String>();
			for (String id : entityRepresentation.getCategories())
			{
				if (categoryNameMap.get(id) != null)
				{
					categoryNames.add(categoryNameMap.get(id));
				}
			}
			entityRepresentation.setCategoryNames(categoryNames);
		}

	}

	private static Set<String> getCategoryIdSet(List<EntityRepresentation> entityRepresentations)
	{
		Set<String> categoryIds = new HashSet<String>();
		for (EntityRepresentation entityRepresentation : entityRepresentations)
		{
			categoryIds.addAll(entityRepresentation.getCategories());
		}
		return categoryIds;
	}

	private static List<String> getAmbiverseEntities(File politician, int offset)
			throws OAuthSystemException, OAuthProblemException, IOException, JSONException, InterruptedException
	{
		List<String> entities = new ArrayList<String>();
		List<String> lines = FileUtils.readLines(politician);
		String accessToken = Ambiverse.getAccessToken();
		List<String> entitesForLine;
		for (int i = offset; i < lines.size(); i++)
		{

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

	private static void createDifferenceWordCloud(List<String> words1, List<String> words2, String name)
			throws IOException
	{
		final FrequencyAnalyzer frequencyAnalyzer = new FrequencyAnalyzer();
		frequencyAnalyzer.setWordFrequenciesToReturn(750);
		frequencyAnalyzer.setMinWordLength(4);
		// frequencyAnalyzer.setStopWords(loadStopWords());
		frequencyAnalyzer.setStopWords(FileUtils.readLines(new File("src/main/resources/stopwords.txt")));

		final List<WordFrequency> wordFrequencies1 = frequencyAnalyzer.load(words1);
		final List<WordFrequency> wordFrequencies2 = frequencyAnalyzer.load(words2);
		final Dimension dimension = new Dimension(600, 600);
		final PolarWordCloud wordCloud = new PolarWordCloud(dimension, CollisionMode.PIXEL_PERFECT, PolarBlendMode.BLUR);
		wordCloud.setPadding(2);
		wordCloud.setBackground(new CircleBackground(300));
		wordCloud.setFontScalar(new SqrtFontScalar(10, 40));
		wordCloud.build(wordFrequencies1, wordFrequencies2);
		wordCloud.writeToFile("output/wordcloud/difference_" + name + ".png");
	}

	private static void createWordCloud(List<String> words, String background, String politician) throws IOException
	{
		final FrequencyAnalyzer frequencyAnalyzer = new FrequencyAnalyzer();
		// frequencyAnalyzer.setStopWords(FileUtils.readLines(new
		// File("src/main/resources/stopwords.txt")));
		frequencyAnalyzer.setWordFrequenciesToReturn(200);

		final List<WordFrequency> wordFrequencies = frequencyAnalyzer.load(words);
		final Dimension dimension = new Dimension(550, 550);
		final WordCloud wordCloud = new WordCloud(dimension, CollisionMode.RECTANGLE);
		wordCloud.setPadding(0);
		wordCloud
				.setBackground(new PixelBoundryBackground(
						new File("src/main/resources/background/" + background + ".txt.png")));
		// wordCloud.setBackground(new RectangleBackground(dimension));
		wordCloud.setColorPalette(new LinearGradientColorPalette(Color.green, Color.blue, 8));
		wordCloud.setFontScalar(new LinearFontScalar(8, 40));
		wordCloud.setBackgroundColor(Color.white);

		wordCloud.build(wordFrequencies);
		wordCloud.writeToFile("output/wordcloud/" + politician + ".entities.png");
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
