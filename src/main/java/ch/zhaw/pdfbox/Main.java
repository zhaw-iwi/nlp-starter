package ch.zhaw.pdfbox;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDDocumentOutline;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDOutlineItem;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDOutlineNode;
import org.apache.pdfbox.text.PDFTextStripper;

public class Main {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub

		File file = new File("The_Merck_Manual_of_Diagnosis_and_Therap.pdf");
		PDDocument document = PDDocument.load(file);
		
MyPDFParser parser = new MyPDFParser("deleteme");
String text = parser.getText(document);
System.out.println(text);
		
		/*
		PDDocument document = PDDocument.load(file);

		PDDocumentOutline outline = document.getDocumentCatalog().getDocumentOutline();
		List<PDOutlineItem> chapters = new ArrayList<PDOutlineItem>();
		Main.extractChapters(outline, chapters);

		List<Disease> diseases = new ArrayList<Disease>();
		PDFTextStripper stripper = new PDFTextStripper();
		for (int i = 0; i < chapters.size() - 1; i++) {
			// System.out.println(current.getTitle());
			stripper.setStartBookmark(chapters.get(i));
			stripper.setEndBookmark(chapters.get(i + 1));
			diseases.add(new DiseaseExtractor(stripper.getText(document)).extract());
		}

		document.close();

		for (Disease current : diseases) {
			System.out.println(current);
		}
*/
	}

	private static void extractChapters(PDOutlineNode bookmark, List<PDOutlineItem> chapters) throws IOException {
		PDOutlineItem current = bookmark.getFirstChild();
		while (current != null) {
			if (current.getTitle().startsWith("Chapter")) {
				chapters.add(current);
			} else if (current.getTitle().startsWith("Appendixes")) {
				chapters.add(current);
				return;
			} else {
				extractChapters(current, chapters);
			}
			current = current.getNextSibling();
		}
	}

	private static void deprecated() throws IOException {
		File file = new File("The_Merck_Manual_of_Diagnosis_and_Therap.pdf");
		PDDocument document = PDDocument.load(file);

		PDDocumentOutline outline = document.getDocumentCatalog().getDocumentOutline();
		List<PDOutlineItem> chapters = new ArrayList<PDOutlineItem>();
		Main.extractChapters(outline, chapters);

		List<String> chapterTexts = new ArrayList<String>();
		PDFTextStripper stripper = new PDFTextStripper();
		PDOutlineItem current;
		for (int i = 0; i < chapters.size() - 1; i++) {
			// System.out.println(current.getTitle());
			stripper.setStartBookmark(chapters.get(i));
			stripper.setEndBookmark(chapters.get(i + 1));
			chapterTexts.add(stripper.getText(document));
		}

		String[] lines;
		List<String> linesCleaned = new ArrayList<String>();
		for (String chapterText : chapterTexts) {
			lines = chapterText.split("\n");
			for (String line : lines) {
				String lineNoEnd = line.replaceAll("(\\r)", "");
				if (Main.isLineOfInterest(lineNoEnd)) {
					linesCleaned.add(lineNoEnd);
				}
			}
		}

		String line;
		for (int i = 0; i < linesCleaned.size(); i++) {
			line = linesCleaned.get(i);
			if (line.startsWith("Chapter ")) {
				System.out.println(line);
				while (i < linesCleaned.size() - 1 && !line.startsWith("Symptoms and Signs")) {
					i++;
					line = linesCleaned.get(i);
				}
				System.out.println(line);
				while (i < linesCleaned.size() - 1 && !line.startsWith("Diagnosis")) {
					i++;
					line = linesCleaned.get(i);
					System.out.println(line);
				}
			}

		}

		document.close();
	}

	private static boolean isLineOfInterest(String line) {
		if (line.startsWith("The Merck Manual of Diagnosis & Therapy")) {
			return false;
		}
		if (line.matches("^[0-9]*$")) {
			return false;
		}
		return true;
	}

}
