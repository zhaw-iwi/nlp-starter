package ch.zhaw.nlp.sources.wikipedia;

public class HistoricalFigure {

	private String name;
	private String country;
	private int birthYear;
	private String birthCity;
	private String gender;
	private String occupation;
	private String description;

	public HistoricalFigure(String name, String country, int birthYear, String birthCity, String gender,
			String occupation) {
		this.name = name;
		this.country = country;
		this.birthYear = birthYear;
		this.birthCity = birthCity;
		this.gender = gender;
		this.occupation = occupation;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getName() {
		return this.name;
	}

	public String getCountry() {
		return this.country;
	}

	public int getBirthYear() {
		return this.birthYear;
	}

	public String getBirthCity() {
		return this.birthCity;
	}

	public String getGender() {
		return this.gender;
	}

	public String getOccupation() {
		return this.occupation;
	}

}
