package ch.zhaw.corenlp;

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

public class Main {

	private static final String FILE_T2F = "Text2Facts";
	private static final String FILE_F2T = "Facts2Text";

	private static final String[] SOURCES = new String[] { new String("https://www.democracynow.org/democracynow.rss"),
			new String("http://feeds.bbci.co.uk/news/world/rss.xml"),
			new String("http://feeds.bbci.co.uk/news/rss.xml"),
			new String("http://feeds.bbci.co.uk/news/business/rss.xml"),
			new String("http://feeds.bbci.co.uk/news/politics/rss.xml"),
			new String("http://feeds.bbci.co.uk/news/health/rss.xml"),
			new String("http://feeds.bbci.co.uk/news/education/rss.xml"),
			new String("http://feeds.bbci.co.uk/news/science_and_environment/rss.xml"),
			new String("http://feeds.bbci.co.uk/news/technology/rss.xml"),
			new String("http://feeds.bbci.co.uk/news/entertainment_and_arts/rss.xml"),
			new String("http://rss.cnn.com/rss/edition_world.rss"), new String("http://rss.cnn.com/rss/edition.rss"),
			new String("http://rss.cnn.com/rss/cnn_latest.rss"),
			new String("http://rss.cnn.com/rss/money_news_international.rss"),
			new String("http://rss.cnn.com/rss/money_latest.rss"),
			new String("http://rss.cnn.com/rss/edition_technology.rss"),
			new String("http://rss.cnn.com/rss/edition_space.rss"),
			new String("http://rss.cnn.com/rss/edition_entertainment.rss"),
			new String("http://rss.cnn.com/rss/cnn_allpolitics.rss"), new String("http://rss.cnn.com/rss/cnn_tech.rss"),
			new String("http://rss.cnn.com/rss/cnn_health.rss"), new String("http://rss.cnn.com/rss/cnn_showbiz.rss"),
			new String("http://rss.cnn.com/cnn-underscored.rss"),
			new String("https://rss.nytimes.com/services/xml/rss/nyt/World.xml"),
			new String("https://rss.nytimes.com/services/xml/rss/nyt/HomePage.xml"),
			new String("https://rss.nytimes.com/services/xml/rss/nyt/Economy.xml"),
			new String("https://rss.nytimes.com/services/xml/rss/nyt/EnergyEnvironment.xml"),
			new String("https://rss.nytimes.com/services/xml/rss/nyt/Business.xml"),
			new String("https://rss.nytimes.com/services/xml/rss/nyt/Technology.xml"),
			new String("https://rss.nytimes.com/services/xml/rss/nyt/PersonalTech.xml"),
			new String("https://rss.nytimes.com/services/xml/rss/nyt/Science.xml"),
			new String("https://rss.nytimes.com/services/xml/rss/nyt/Climate.xml"),
			new String("https://rss.nytimes.com/services/xml/rss/nyt/Space.xml"),
			// new String("https://www.nytimes.com/services/xml/rss/nyt/Health.xml"),
			new String("https://rss.nytimes.com/services/xml/rss/nyt/sunday-review.xml") };

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

		for (String source : Main.SOURCES) {

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
