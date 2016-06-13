package eu.erbs.ambiverse;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;
import java.util.Map;

import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.junit.Test;

public class AmbiverseUtilsTest
{

	// @Test
	// public void test() throws OAuthSystemException, OAuthProblemException
	// {
	// List<String> entities = AmbiverseUtils.getEntities("Barcelona plays
	// against Bilbao in Madrid.");
	// System.out.println(StringUtils.join(entities, ", "));
	// assertNotNull(entities);
	// assertEquals(3, entities.size());
	// }

	@Test
	public void test() throws OAuthSystemException, OAuthProblemException
	{
		Map<String, List<String>> entities = AmbiverseUtils
				.getEntitesAndCategories("Barcelona plays against Bilbao in Madrid.");
		System.out.println(entities);
		// System.out.println(StringUtils.join(entities, ", "));
		assertNotNull(entities);
		assertEquals(3, entities.size());
	}

}
