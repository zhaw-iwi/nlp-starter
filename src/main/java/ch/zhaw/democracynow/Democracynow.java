package ch.zhaw.democracynow;

import java.io.IOException;
import java.net.URL;

import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;

public class Democracynow {

	public static void main(String[] args) throws IllegalArgumentException, FeedException, IOException {
		URL feedSource = new URL("https://www.democracynow.org/democracynow.rss");
		SyndFeedInput input = new SyndFeedInput();
		SyndFeed feed = input.build(new XmlReader(feedSource));

		for (Object current : feed.getEntries()) {
			SyndEntry entry = (SyndEntry) current;
			System.out.println(entry.getTitle());
			System.out.println(entry.getDescription().getValue());
		}
	}

}
