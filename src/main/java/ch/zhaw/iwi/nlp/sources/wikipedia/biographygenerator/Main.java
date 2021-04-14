package ch.zhaw.iwi.nlp.sources.wikipedia.biographygenerator;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.opencsv.exceptions.CsvValidationException;

import ch.zhaw.iwi.nlp.sources.wikipedia.HistoricalFigure;
import ch.zhaw.iwi.nlp.sources.wikipedia.PantheonHelper;

public class Main {

	public static void main(String[] args) throws CsvValidationException, IOException {

		List<HistoricalFigure> historicalFigures = PantheonHelper.readFromCSV("resources/pantheon/pantheon.tsv",
				new HistoricalFigureWithBioFactory());

		Map<String, String[]> templates = BioTemplates.getTemplatesMap();

		for (HistoricalFigure current : historicalFigures) {
			((HistoricalFigureWithBio) current).createBiography(templates);

			System.out.println(((HistoricalFigureWithBio) current).getBiography());
		}
	}

}
