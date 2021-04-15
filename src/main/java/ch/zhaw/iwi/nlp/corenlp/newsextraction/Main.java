package ch.zhaw.iwi.nlp.corenlp.newsextraction;

// https://rss.com/blog/popular-rss-feeds/

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;

import com.sun.syndication.feed.synd.SyndContent;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;

import ch.zhaw.iwi.nlp.sources.rssnews.RssFeeds;

public class Main {

	private static final String FILE_T2F = "Text2Facts";
	private static final String FILE_F2T = "Facts2Text";

	public static void main(String[] args) throws IllegalArgumentException, FeedException, IOException {

		// URL feedSource = new URL("https://www.democracynow.org/democracynow.rss");

		// outer loop variables
		URL feedSource;
		SyndFeedInput input;
		SyndFeed feed;

		// inner loop variables
		Manager mgmt;
		Extractor extractor;
		SyndEntry entry;
		SyndContent description;
		String text;

		for (String source : RssFeeds.SOURCES) {

			System.out.println("> Processing SOURCE " + source);

			feedSource = new URL(source);
			input = new SyndFeedInput();

			try {
				feed = input.build(new XmlReader(feedSource));

				for (Object current : feed.getEntries()) {
					mgmt = new Manager();
					extractor = new Extractor(mgmt);

					entry = ((SyndEntry) current);
					if (entry != null) {
						description = entry.getDescription();
						if (description != null) {
							text = description.getValue().replaceAll("\\<.*?\\>", "");
							System.out.println("> Processing ENTRY\n" + text + "\n");
							extractor.getFacts(text);
							System.out.println(mgmt);
							Main.writeToFile(text, mgmt.toString());
						}
					}
				}
			} catch (Exception e) {
				System.out.println("> Exception while processing " + source);
				e.printStackTrace();
			}
		}

	}

	private static void writeToFile(String news, String facts) throws IOException {
		BufferedWriter writer = new BufferedWriter(new FileWriter(Main.FILE_T2F, true));
		writer.append("<item>");
		writer.newLine();
		writer.append("<text>");
		writer.append(news);
		writer.append("</text>");
		writer.newLine();
		writer.append("<facts>");
		writer.append(facts);
		writer.append("</facts>");
		writer.newLine();
		writer.append("</item>");
		writer.newLine();
		writer.close();

		writer = new BufferedWriter(new FileWriter(Main.FILE_F2T, true));
		writer.append("<item>");
		writer.newLine();
		writer.append("<facts>");
		writer.append(facts);
		writer.append("</facts>");
		writer.newLine();
		writer.append("<text>");
		writer.append(news);
		writer.append("</text>");
		writer.newLine();
		writer.append("</item>");
		writer.newLine();
		writer.close();

	}

}
