package eu.erbs.brexit.analytics;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

public class ShakespearFilter
{

	public static void main(String[] args) throws IOException
	{
		File output = new File("src/main/resources/shakespeare_filtered.txt");
		for (String line : FileUtils.readLines(new File("src/main/resources/shakespeare.txt")))
		{
			FileUtils.write(output, line.replaceAll("<A NAME=[0-9\\.]+>", "").trim() + "\n", true);
		}

	}

}