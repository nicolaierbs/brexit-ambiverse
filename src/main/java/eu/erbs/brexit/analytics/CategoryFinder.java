package eu.erbs.brexit.analytics;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;

public class CategoryFinder
{

	public static void main(String[] args) throws IOException
	{
		// cleanEntities();
		// createEntitySet();

	}

	private static void createEntitySet() throws IOException
	{
		Set<String> entities = new HashSet<String>();
		entities.addAll(FileUtils.readLines(new File("output/ambiverse/johnson.entities")));
		entities.addAll(FileUtils.readLines(new File("output/ambiverse/cameron.entities")));
		FileUtils.writeLines(new File("output/ambiverse/entities.set"), entities);
	}

	private static void cleanEntities() throws IOException
	{
		List<String> yagoEntities = new ArrayList<String>();
		for (String line : FileUtils.readLines(new File("output/johnson.txt.entities")))
		{
			if (line.length() > 3)
			{
				yagoEntities.addAll(Arrays.asList(line.split(" @@@ ")));
			}
		}
		FileUtils.writeLines(new File("output/ambiverse/johnson.entities"), yagoEntities);
	}

}
