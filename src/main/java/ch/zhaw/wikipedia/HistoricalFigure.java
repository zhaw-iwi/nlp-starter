package ch.zhaw.wikipedia;

public class HistoricalFigure {

	private String name;
	private int birthyear;
	private String gender;
	private String occupation;
	private String description;

	public HistoricalFigure(String name, int birthyear, String gender, String occupation) {
		this.name = name;
		this.birthyear = birthyear;
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

	public int getBirthyear() {
		return this.birthyear;
	}

	public String getGender() {
		return this.gender;
	}

	public String getOccupation() {
		return this.occupation;
	}

}
