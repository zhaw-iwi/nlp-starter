package ch.zhaw.iwi.nlp.corenlp.extractiontofile;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

public class FileWriteHelper {

	private static final String FILE_T2F = "coreNLPOut/Text2Facts";
	private static final String FILE_F2T = "coreNLPOut/Facts2Text";

	public static void writeToFile(String text, String facts) throws IOException {
		BufferedWriter writer = new BufferedWriter(
				new OutputStreamWriter(new FileOutputStream(FileWriteHelper.FILE_T2F, true), StandardCharsets.UTF_8));
		writer.append("<item>");
		writer.append("<text>");
		writer.append(text);
		writer.append("</text>");
		writer.append("<facts>");
		writer.append(facts);
		writer.append("</facts>");
		writer.append("</item>");
		writer.newLine();
		writer.close();

		writer = new BufferedWriter(
				new OutputStreamWriter(new FileOutputStream(FileWriteHelper.FILE_F2T, true), StandardCharsets.UTF_8));
		writer.append("<item>");
		writer.append("<facts>");
		writer.append(facts);
		writer.append("</facts>");
		writer.append("<text>");
		writer.append(text);
		writer.append("</text>");
		writer.append("</item>");
		writer.newLine();
		writer.close();
	}
}
