package ch.zhaw.iwi.nlp.sources.wikipedia.biographygenerator;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import ch.zhaw.iwi.nlp.sources.wikipedia.HistoricalFigure;

public class HistoricalFigureWithBio extends HistoricalFigure {

	private String biography;

	public HistoricalFigureWithBio(String name, String country, int birthYear, String birthCity, String gender,
			String occupation) {

		// gender.toLowerCase() required for the selection of gender-specific templates
		super(name, country, birthYear, birthCity, gender.toLowerCase(), occupation);
	}

	private void setBiography(String biography) {
		this.biography = biography;
	}

	public String getBiography() {
		return this.biography;
	}

	private String getRandomTemplate(Map<String, String[]> templates) {
		String[] set = templates.get(this.getGender());
		Random random = new Random();
		int index = random.nextInt(set.length);
		String template = set[index];

		return template;
	}

	public void createBiography(Map<String, String[]> templates) {
		String template = this.getRandomTemplate(templates);

		template = template.replace("__NAME__", this.getName());
		template = template.replace("__BIRTHYEAR__", Integer.toString(this.getBirthYear()));
		template = template.replace("__BIRTHCITY__", this.getBirthCity());
		template = template.replace("__COUNTRYNAME__", this.getCountry());
		template = template.replace("__OCCUPATION__", this.getOccupation());

		this.setBiography(template);
	}

	public String getFacts() {
		HashMap<String, String> pairs = new HashMap<String, String>();

		pairs.put("name", this.getName());
		pairs.put("birthYear", Integer.toString(this.getBirthYear()));
		pairs.put("birthCity", this.getBirthCity());
		pairs.put("country", this.getCountry());
		pairs.put("occupation", this.getOccupation());
		pairs.put("gender", this.getGender());

		StringBuffer result = new StringBuffer();

		for (Entry<String, String> pair : pairs.entrySet()) {
			result.append(pair.getKey() + "(" + pair.getValue() + ").\n)");
		}

		return result.toString();
	}
}