package ch.zhaw.nlp.sources.pdf.pdfparser;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.text.PDFTextStripperByArea;

import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PdfParser {
  final File dummyFile = new File("");
  final String absolutePath = this.dummyFile.getAbsolutePath() + "/pdfOut/pdfParser";
  private final Map<String, String> map = new HashMap<>();

  public Map<String, String> parse() {

    try (final Stream<File> fileStream =
        Files.walk(Paths.get(this.absolutePath))
            .filter(path -> path.toString().contains(".pdf"))
            .map(Path::toFile)) {

      final List<File> filesToParse = fileStream.collect(Collectors.toList());
      System.out.println("Total files to parse: " + filesToParse.size());
      filesToParse.forEach(
          file -> {
            System.out.println("Parsing file: " + file.getName());

            this.map.put(file.getName(), extractTextFromFile(file));
          });
      return this.map;

    } catch (final IOException e) {
      e.printStackTrace();
      return null;
    }
  }

  private String extractTextFromFile(final File file) {
    String text = "";

    try (final PDDocument document = PDDocument.load(file); ) {

      // Rectangle2D region = new Rectangle2D.Double(x,y,width,height);
      final Rectangle2D region = new Rectangle2D.Double(0, 100, 550, 700);
      final String regionName = "content";
      final PDFTextStripperByArea stripper;
      stripper = new PDFTextStripperByArea();

      for (final PDPage page : document.getPages()) {

        stripper.addRegion(regionName, region);
        stripper.extractRegions(page);
        text += stripper.getTextForRegion(regionName);
      }

    } catch (final IOException e) {
      e.printStackTrace();
    }
    return text;
  }
}
