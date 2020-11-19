package ch.zhaw.pdfbox;

public class DiseaseExtractor {

	private final String[] chapterLines;

	public DiseaseExtractor(String chapter) {
		this.chapterLines = chapter.split("\\n");
	}

	public Disease extract() {
		for (String current : this.chapterLines) {
			System.out.println(current);

		}
		return null;
	}

}
