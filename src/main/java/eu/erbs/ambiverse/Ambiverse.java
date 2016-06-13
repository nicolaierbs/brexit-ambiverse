package eu.erbs.ambiverse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Logger;

import org.apache.oltu.oauth2.client.OAuthClient;
import org.apache.oltu.oauth2.client.URLConnectionClient;
import org.apache.oltu.oauth2.client.request.OAuthBearerClientRequest;
import org.apache.oltu.oauth2.client.request.OAuthClientRequest;
import org.apache.oltu.oauth2.client.response.OAuthJSONAccessTokenResponse;
import org.apache.oltu.oauth2.client.response.OAuthResourceResponse;
import org.apache.oltu.oauth2.common.OAuth;
import org.apache.oltu.oauth2.common.OAuth.ContentType;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.message.types.GrantType;
import org.json.JSONArray;
import org.json.JSONObject;

public class Ambiverse
{
	private static final Logger log = Logger.getLogger(Ambiverse.class.getName());

	private static String AUTHORIZATION_HOST = "https://api.ambiverse.com/oauth2/token";
	private static String ENTITY_LINKING_HOST = "https://api.ambiverse.com/entitylinking/v1beta1/analyze";
	private static String ENTITY_INFORMATION_HOST = "https://api.ambiverse.com/knowledgegraph/v1beta1/entities";
	private static String CATEGORY_NAMES_HOST = "https://api.ambiverse.com/knowledgegraph/v1beta1/categories";

	private static String REDIRECT_URI = "https://www.ambiverse.com";
	private static String CLIENT_ID = "27f2564a2dc64c8e8dc08b3ac0ef7323";
	private static String CLIENT_SECRET = "00e5a697863642ee8ccfabf3160795db";

	private static OAuthClient oAuthClient;

	public static void main(String... args) throws IOException, OAuthSystemException, OAuthProblemException
	{
		log.info("Started Ambiverse connection.");
		String accessToken = getAccessToken();

		String input = "Barcelona plays against Bilbao in Madrid.";
		String analysis = getEntites(input, accessToken);
		log.info(analysis);

		Collection<String> entities = new ArrayList<String>();
		entities.add("YAGO3:<The_Who>");
		entities.add("YAGO3:<Tommy_(album)>");
		String entityInformation = getEntityInformation(entities, accessToken);
		log.info(entityInformation);
		
		Collection<String> categories = new ArrayList<String>();
		categories.add("YAGO3:<wikicat_Polydor_Records_artists>");
		categories.add("YAGO3:<wikicat_British_Invasion_artists>");
		String categoryNames = getCategoryNames(entities, accessToken);
		log.info(categoryNames);
	}

	public static String getAccessToken() throws OAuthSystemException, OAuthProblemException
	{

		oAuthClient = new OAuthClient(new URLConnectionClient());

		OAuthClientRequest request = OAuthClientRequest
				.tokenLocation(AUTHORIZATION_HOST)
				.setClientId(CLIENT_ID)
				.setClientSecret(CLIENT_SECRET)
				.setRedirectURI(REDIRECT_URI)
				.setGrantType(GrantType.CLIENT_CREDENTIALS)
				.setScope("all")
				.buildBodyMessage();
		// create OAuth client that uses custom http client under the hood
		OAuthJSONAccessTokenResponse response = oAuthClient.accessToken(request);

		log.info("Access token expires in " + response.getExpiresIn() / 3600 + " minutes.");
		return response.getAccessToken();
	}

	public static String askAmbiverse(String json, String host, String accessToken)
			throws OAuthSystemException, OAuthProblemException
	{
		OAuthClientRequest bearerClientRequest = new OAuthBearerClientRequest(host)
				.setAccessToken(accessToken)
				.buildQueryMessage();

		bearerClientRequest.setHeader(OAuth.HeaderType.CONTENT_TYPE, ContentType.JSON);
		bearerClientRequest.setBody(json);

		OAuthResourceResponse resourceResponse = oAuthClient.resource(bearerClientRequest, OAuth.HttpMethod.POST,
				OAuthResourceResponse.class);

		return resourceResponse.getBody();
	}

	public static String getEntites(String text, String accessToken) throws OAuthSystemException, OAuthProblemException
	{
		JSONObject json = createJson(text);
		return askAmbiverse(json.toString(), ENTITY_LINKING_HOST, accessToken);
	}

	public static String getEntityInformation(Collection<String> entities, String accessToken)
			throws OAuthSystemException, OAuthProblemException
	{
		JSONArray json = createJson(entities);
		return askAmbiverse(json.toString(), ENTITY_INFORMATION_HOST, accessToken);
	}

	public static String getCategoryNames(Collection<String> categoryIds, String accessToken)
			throws OAuthSystemException, OAuthProblemException
	{
		JSONArray json = createJson(categoryIds);
		return askAmbiverse(json.toString(), CATEGORY_NAMES_HOST, accessToken);
	}

	private static JSONObject createJson(String text)
	{
		JSONObject json = new JSONObject();
		json.put("docId", "test");
		json.put("text", text);

		return json;
	}

	private static JSONArray createJson(Collection<String> entities)
	{
		JSONArray json = new JSONArray();
		for (String entity : entities)
		{
			json.put(entity);
		}
		return json;
	}
}
