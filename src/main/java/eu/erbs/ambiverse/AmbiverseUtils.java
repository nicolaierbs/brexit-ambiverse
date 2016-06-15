package eu.erbs.ambiverse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class AmbiverseUtils
{

	// INFO:
	// {"docId":"test","matches":[{"charLength":9,"charOffset":0,"entity":{"kgId":"YAGO3:<Barcelona>","score":0.0890470946201384},"text":"Barcelona"},{"charLength":6,"charOffset":24,"entity":{"kgId":"YAGO3:<Bilbao>","score":0.7586878628571405},"text":"Bilbao"},{"charLength":6,"charOffset":34,"entity":{"kgId":"YAGO3:<Madrid>","score":0.6119864599933966},"text":"Madrid"}]}

	// INFO: {"entities":[{"id":"YAGO3:<The_Who>","name":"The
	// Who","links":[{"source":"Wikipedia","url":"http://en.wikipedia.org/wiki/The%20Who"}],"imageUrl":"http://upload.wikimedia.org/wikipedia/commons/thumbWho_-_1975.jpg/200px-Who_-_1975.jpg","description":"The
	// Who are an English rock band formed in 1964 by Roger Daltrey (lead
	// vocals, guitar, harmonica), Pete Townshend (guitar, vocals, keyboards),
	// John Entwistle (bass, brass, vocals) and Keith Moon (drums, vocals). They
	// became known for energetic live performances which often included
	// instrument
	// destruction.","categories":["YAGO3:<wordnet_physical_entity_100001930>","YAGO3:<wikicat_Atco_Records_artists>","YAGO3:<wikicat_Grammy_Lifetime_Achievement_Award_winners>","YAGO3:<wikicat_British_Invasion_artists>","YAGO3:<wordnet_whole_100003553>","YAGO3:<wordnet_person_100007846>","YAGO3:<wordnet_artist_109812338>","YAGO3:<wikicat_Brit_Award_winners>","YAGO3:<wikicat_Decca_Records_artists>","YAGO3:<wikicat_Brunswick_Records_artists>","YAGO3:<wordnet_living_thing_100004258>","YAGO3:<yagoLegalActorGeo>","YAGO3:<wikicat_Polydor_Records_artists>","YAGO3:<wordnet_causal_agent_100007347>","YAGO3:<wordnet_contestant_109613191>","YAGO3:<wikicat_Warner_Bros._Records_artists>","YAGO3:owl:Thing","YAGO3:<wikicat_Geffen_Records_artists>","YAGO3:<wordnet_object_100002684>","YAGO3:<wordnet_creator_109614315>","YAGO3:<wordnet_winner_110782940>","YAGO3:<wordnet_organism_100004475>","YAGO3:<yagoLegalActor>"]},{"id":"YAGO3:<Tommy_(album)>","name":"Tommy
	// (album)","links":[{"source":"Wikipedia","url":"http://en.wikipedia.org/wiki/Tommy%20%28album%29"}],"imageUrl":"http://upload.wikimedia.org/wikipedia/en/thumbTommyalbumcover.jpg/200px-Tommyalbumcover.jpg","description":"Tommy
	// is the fourth album by English rock band The Who, released by Track
	// Records and Polydor Records in the UK and Decca Records/MCA in the US. A
	// double album telling a loose story about a \"deaf, dumb and blind boy\",
	// Tommy was the first musical work to be billed overtly as a rock opera.
	// Released in 1969, the album was mostly composed by Pete Townshend. In
	// 1998, it was inducted into the Grammy Hall of Fame for \"historical,
	// artistic and significant
	// value\".","categories":["YAGO3:<wordnet_physical_entity_100001930>","YAGO3:<wordnet_product_104007894>","YAGO3:<wikicat_The_Who_albums>","YAGO3:<wikicat_Rock_operas>","YAGO3:<wikicat_1969_albums>","YAGO3:<wordnet_album_106591815>","YAGO3:<wordnet_instrumentality_103575240>","YAGO3:<wordnet_rock_opera_106592281>","YAGO3:owl:Thing","YAGO3:<wordnet_impression_106590210>","YAGO3:<wordnet_work_104599396>","YAGO3:<wikicat_Track_Records_albums>","YAGO3:<wikicat_MCA_Records_albums>","YAGO3:<wikicat_Albums_produced_by_Kit_Lambert>","YAGO3:<wordnet_whole_100003553>","YAGO3:<wordnet_publication_106589574>","YAGO3:<wikicat_Concept_albums>","YAGO3:<wordnet_artifact_100021939>","YAGO3:<wikicat_English-language_albums>","YAGO3:<wikicat_Universal_Deluxe_Editions>","YAGO3:<wordnet_medium_106254669>","YAGO3:<wikicat_Decca_Records_albums>","YAGO3:<wordnet_edition_106590446>","YAGO3:<wordnet_creation_103129123>","YAGO3:<wordnet_object_100002684>","YAGO3:<wikicat_Polydor_Records_albums>","YAGO3:<wordnet_concept_album_106592078>"]}]}

	// INFO: {"categories":[{"id":"YAGO3:<The_Who>","name":"The
	// Who"},{"id":"YAGO3:<Tommy_(album)>","name":"Tommy (album)"}]}

	private static final Logger log = Logger.getLogger(AmbiverseUtils.class.getName());


	public static List<String> getEntities(String text) throws OAuthSystemException, OAuthProblemException
	{
		String accessToken = Ambiverse.getAccessToken();
		return getEntities(text, accessToken);

	}

	public static List<String> getEntities(String text, String accessToken)
			throws OAuthSystemException, OAuthProblemException
	{
		JSONObject json = new JSONObject(Ambiverse.getEntites(text, accessToken));

		List<String> entities = new ArrayList<String>();
		// {"docId":"test","matches":[{"charLength":9,"charOffset":0,"entity":{"kgId":"YAGO3:<Barcelona>","score":0.0890470946201384},"text":"Barcelona"},{"charLength":6,"charOffset":24,"entity":{"kgId":"YAGO3:<Bilbao>","score":0.7586878628571405},"text":"Bilbao"},{"charLength":6,"charOffset":34,"entity":{"kgId":"YAGO3:<Madrid>","score":0.6119864599933966},"text":"Madrid"}]}

		JSONObject match;
		log.fine("Received JSON:\t" + json.toString());
		JSONArray matches = json.getJSONArray("matches");
		log.fine("Matches:\t" + matches.toString());
		for (int position = 0; position < matches.length(); position++)
		{
			match = matches.getJSONObject(position);
			log.finer("Match:\t" + match.toString());
			entities.add(match.getJSONObject("entity").getString("kgId"));
		}

		return entities;
	}

	public static Map<String, List<String>> getEntitesAndCategories(String text)
			throws JSONException, OAuthSystemException, OAuthProblemException, InterruptedException
	{
		Map<String, List<String>> categories = new HashMap<String, List<String>>();

		String accessToken = Ambiverse.getAccessToken();
		JSONArray entities = new JSONObject(Ambiverse.getEntityInformation(getEntities(text, accessToken), accessToken))
				.getJSONArray("entities");

		JSONObject entity;
		JSONArray categoryArray;
		List<String> categoryIds;
		for (int position = 0; position < entities.length(); position++)
		{
			entity = entities.getJSONObject(position);
			log.finer("Entity:\t" + entity.toString());

			categoryArray = entity.getJSONArray("categories");
			categoryIds = new ArrayList<String>();
			for (int i = 0; i < categoryArray.length(); i++)
			{
				categoryIds.add(categoryArray.getString(i));
			}
			categories.put(entity.getString("name"), getCategoryNames(categoryIds, accessToken));
		}
		return categories;
	}

	private static List<String> getCategoryNames(List<String> categoryIds, String accessToken)
			throws JSONException, OAuthSystemException, OAuthProblemException, InterruptedException
	{
		List<String> categories = new ArrayList<String>();
		Thread.sleep(1000l);
		JSONArray array = new JSONObject(Ambiverse.getCategoryNames(categoryIds, accessToken))
				.getJSONArray("categories");
		for (int i = 0; i < array.length(); i++)
		{
			categories.add(array.getJSONObject(i).getString("name"));
		}
		// INFO: {"categories":[{"id":"YAGO3:<The_Who>","name":"The
		// Who"},{"id":"YAGO3:<Tommy_(album)>","name":"Tommy (album)"}]}
		return categories;
	}

}
