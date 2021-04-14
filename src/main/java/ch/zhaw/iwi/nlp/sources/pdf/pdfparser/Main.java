package ch.zhaw.iwi.nlp.sources.pdf.pdfparser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class Main {

  public static void main(final String[] args) {
    final PdfParser pdfParser = new PdfParser();
    System.out.println("");

    final File file = new File("pdfOut/pdfParser/" + System.currentTimeMillis() + ".txt");
    final FileOutputStream outputStream;
    try {
      outputStream = new FileOutputStream(file.getPath());
      pdfParser
          .parse()
          .forEach(
              (key, content) -> {
                try {
                  outputStream.write(("Title: " + key + "\n\n").getBytes());
                  outputStream.write(content.getBytes());
                } catch (final IOException e) {
                  e.printStackTrace();
                }
              });

    } catch (final FileNotFoundException e) {
      e.printStackTrace();
    }
  }
}
