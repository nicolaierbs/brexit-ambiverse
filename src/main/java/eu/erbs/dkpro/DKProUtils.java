package eu.erbs.dkpro;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngine;

import java.util.HashMap;
import java.util.Map;

import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;

import de.tudarmstadt.ukp.dkpro.core.opennlp.OpenNlpPosTagger;
import de.tudarmstadt.ukp.dkpro.core.opennlp.OpenNlpSegmenter;
import de.tudarmstadt.ukp.dkpro.core.readability.ReadabilityAnnotator;
import de.tudarmstadt.ukp.dkpro.core.type.ReadabilityScore;

public class DKProUtils {

	private static AnalysisEngine tokenizer;
	private static AnalysisEngine tagger;
	private static AnalysisEngine readability;

	public static Map<String,Double> getReadabaility(String text) throws UIMAException
	{

		if(tokenizer == null) {
			tokenizer = createEngine(OpenNlpSegmenter.class);
		}
		if(tagger == null) {
			tagger = createEngine(OpenNlpPosTagger.class);
		}
		if (readability == null)
		{
			readability = createEngine(ReadabilityAnnotator.class);
		}

		JCas jCas = JCasFactory.createJCas();
		jCas.setDocumentLanguage("en");
		jCas.setDocumentText(text);

		SimplePipeline.runPipeline(jCas, tokenizer, tagger, readability);

		Map<String,Double> readability = new HashMap<String,Double>();
		for (ReadabilityScore score : JCasUtil.select(jCas, ReadabilityScore.class))
		{
			readability.put(score.getMeasureName(), score.getScore());
		}
		return readability;

	}
	
}