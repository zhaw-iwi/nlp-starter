package ch.zhaw.pdf.coreapi;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

public class Main {

	public static void main(final String[] args) {

		try {
			final CoreApiConnector coreApiConnector = new CoreApiConnector();
			for (final String searchQuery : Arrays.asList("cryptocurrency", "platform+economy",
					"artificial+intelligence")) {

				// for every searchquery
				final File file = new File("coreApiParser/" + searchQuery + "_" + System.currentTimeMillis() + ".txt");
				final FileOutputStream outputStream = new FileOutputStream(file.getPath());
				System.out.println("writing test file to " + file.getPath());

				final Map<String, CoreApiArticle> data = coreApiConnector.getDocuments(searchQuery, 100);
				data.forEach((id, article) -> {
					try {

						final String documentText = getText(article.getFile());
						final String documentTextWithoutAbstractAndTitle = documentText.replace("\n", "")
								.replace("\r", "").replace(article.getDescription(), "")
								.replace(article.getTitle(), "");

						// Hier Stoppwörter und Textzusammenstellung anpassen!!
						outputStream.write("documentstart".getBytes());
						outputStream.write(documentTextWithoutAbstractAndTitle.getBytes());
						outputStream.write("documentend".getBytes());

						// description == abstract
						outputStream.write("abstractstart".getBytes());
						outputStream.write(article.getDescription().getBytes());
						outputStream.write("abstractend".getBytes());
						outputStream.write("titlestart".getBytes());
						outputStream.write(article.getTitle().getBytes());
						outputStream.write("titleend".getBytes());

					} catch (final IOException e) {
						e.printStackTrace();
					}
				});
				System.out.println("created testfile " + file.getAbsolutePath());
				outputStream.close();
			}

		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	private static String getText(final File file) throws IOException {
		// Loading an existing document
		try (final PDDocument document = PDDocument.load(file)) {

			// Instantiate PDFTextStripper class
			final PDFTextStripper pdfStripper = new PDFTextStripper();

			// Retrieving text from PDF document
			return pdfStripper.getText(document);
		}
	}
}
