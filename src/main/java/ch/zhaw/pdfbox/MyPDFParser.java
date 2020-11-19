package ch.zhaw.pdfbox;

import java.io.IOException;
import java.io.PrintWriter;

import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.TextPosition;

public class MyPDFParser extends PDFTextStripper {

	PrintWriter writer;
	boolean wasBold = false;

	public MyPDFParser(String filepath) throws IOException {
		writer = new PrintWriter(filepath, "UTF-8");
		writer.println("Dictionary Source:");
	}

	@Override
	protected void processTextPosition(TextPosition text) {
		if (text.getFont().getFontDescriptor() != null) {
			if (text.getFont().getFontDescriptor().isForceBold()
					|| text.getFont().getFontDescriptor().getFontWeight() > 680 && wasBold) {
				writer.print(text.toString().toUpperCase());
				wasBold = true;
			} else if (text.getFont().getFontDescriptor().isForceBold()
					|| text.getFont().getFontDescriptor().getFontWeight() > 680 && !wasBold) {
				writer.println();
				writer.print(text.toString().toUpperCase());
				wasBold = true;
			} else {
				writer.print(text.toString());
				wasBold = false;
			}
		}
	}

	public void closeParser() {
		writer.close();
	}
}