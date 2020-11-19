package ch.zhaw.pdfbox;

public class Disease {

	private final String name;
	private final String symptoms;
	private final String diagnosis;
	private final String treatment;

	public Disease(final String name, final String symptoms, final String diagnosis, final String treatment) {
		this.name = name;
		this.symptoms = symptoms;
		this.diagnosis = diagnosis;
		this.treatment = treatment;
	}

	public String getName() {
		return this.name;
	}

	public String getSymptoms() {
		return this.symptoms;
	}

	public String getDiagnosis() {
		return this.diagnosis;
	}

	public String getTreatment() {
		return this.treatment;
	}

	public String toString() {
		StringBuffer result = new StringBuffer();
		result.append("<disease>");

		result.append("<name>" + this.name + "</name>");
		result.append("<symptoms>" + this.symptoms + "</symptoms>");
		result.append("<diagnosis>" + this.diagnosis + "</diagnosis>");
		result.append("<treatment>" + this.symptoms + "</treatment>");

		result.append("</disease>");
		return result.toString();
	}
}
