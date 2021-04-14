package ch.zhaw.iwi.nlp.sources.wikipedia;

public class HistoricalFigureFactory {

	public HistoricalFigure create(String name, String country, int birthYear, String birthCity, String gender,
			String occupation) {
		return new HistoricalFigure(name, country, birthYear, birthCity, gender, occupation);
	}
}
