package ch.zhaw.iwi.nlp.corenlp.extractiontofile.oo;

import java.io.IOException;
import java.net.URL;
import java.util.Properties;

import com.sun.syndication.feed.synd.SyndContent;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;

import ch.zhaw.iwi.nlp.corenlp.extractiontofile.FileWriteHelper;
import ch.zhaw.iwi.nlp.sources.rssnews.RssFeeds;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;

public class Main {

	public static void main(String[] args) throws IllegalArgumentException, FeedException, IOException {

		// URL feedSource = new URL("https://www.democracynow.org/democracynow.rss");

		// one pipeline
		Properties properties = new Properties();
		properties.setProperty("annotators", "tokenize,ssplit,pos,lemma,ner,depparse,natlog,openie");
		StanfordCoreNLP pipeline = new StanfordCoreNLP(properties);

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
					extractor = new Extractor(mgmt, pipeline);

					entry = ((SyndEntry) current);
					if (entry != null) {
						description = entry.getDescription();
						if (description != null) {
							text = description.getValue().replaceAll("\\<.*?\\>", "");
							System.out.println("> Processing ENTRY\n" + text + "\n");
							extractor.getFacts(text);
							System.out.println(mgmt);
							FileWriteHelper.writeToFile(text, mgmt.toString());
						}
					}
				}
			} catch (Exception e) {
				System.out.println("> Exception while processing " + source);
				e.printStackTrace();
			}
		}
	}
}
