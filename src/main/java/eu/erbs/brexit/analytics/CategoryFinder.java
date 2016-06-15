package eu.erbs.brexit.analytics;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;

public class CategoryFinder
{

	public static void main(String[] args) throws IOException
	{
		List<String> yagoEntities = new ArrayList<String>();
		for (String line : FileUtils.readLines(new File("src/main/resources/cameron.txt.entities")))
		{
			yagoEntities.addAll(line.split(" @@@ "));
		}

	}

}
