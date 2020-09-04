package ch.zhaw.corenlp;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;

import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;

public class Main {

	private static final String FILE_T2F = "trainingText2Facts";
	private static final String FILE_F2T = "trainingFacts2Text";

	public static void main(String[] args) throws IllegalArgumentException, FeedException, IOException {

		URL feedSource = new URL("https://www.democracynow.org/democracynow.rss");
		SyndFeedInput input = new SyndFeedInput();
		SyndFeed feed = input.build(new XmlReader(feedSource));

		Manager mgmt;
		Extractor extractor;
		String text;
		for (Object current : feed.getEntries()) {
			mgmt = new Manager();
			extractor = new Extractor(mgmt);

			text = ((SyndEntry) current).getDescription().getValue();

			System.out.println("> Processing\n" + text.replace(". ", ".\n") + "\n");

			extractor.getFacts(text);

			System.out.println(mgmt);

			Main.writeToFile(text, mgmt.toString());
		}

	}

	private static void writeToFile(String news, String facts) throws IOException {
		BufferedWriter writer = new BufferedWriter(new FileWriter(Main.FILE_F2T, true));
		writer.append("<|startoftext|>");
		writer.append("[NEWS_PROMPT]");
		writer.append(news);
		writer.newLine();
		writer.append("[FACTS_PROMPT]");
		writer.append(facts);
		writer.newLine();
		writer.append("<|endoftext|>");
		writer.close();

		writer = new BufferedWriter(new FileWriter(Main.FILE_T2F, true));
		writer.append("<|startoftext|>");
		writer.append("[FACTS_PROMPT]");
		writer.append(facts);
		writer.newLine();
		writer.append("[NEWS_PROMPT]");
		writer.append(news);
		writer.append("<|endoftext|>");
		writer.newLine();
		writer.close();

	}

}
