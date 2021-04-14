package ch.zhaw.iwi.nlp.sources.wikipedia.biographygenerator;

import ch.zhaw.iwi.nlp.sources.wikipedia.HistoricalFigure;
import ch.zhaw.iwi.nlp.sources.wikipedia.HistoricalFigureFactory;

public class HistoricalFigureWithBioFactory extends HistoricalFigureFactory {

	public HistoricalFigure create(String name, String country, int birthYear, String birthCity, String gender,
			String occupation) {
		return new HistoricalFigureWithBio(name, country, birthYear, birthCity, gender, occupation);
	}
}
