package ch.zhaw.opennlp;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;

import opennlp.tools.cmdline.parser.ParserTool;
import opennlp.tools.parser.Parse;
import opennlp.tools.parser.Parser;
import opennlp.tools.parser.ParserFactory;
import opennlp.tools.parser.ParserModel;
import opennlp.tools.sentdetect.SentenceDetector;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;

public class ChunkParser {

	public static void main(String[] args) throws IOException, IllegalArgumentException, FeedException {
		URL feedSource = new URL("https://www.democracynow.org/democracynow.rss");
		SyndFeedInput input = new SyndFeedInput();
		SyndFeed feed = input.build(new XmlReader(feedSource));

		// System.out.println(feed);

		// for (Object current : feed.getEntries()) {
		SyndEntry entry = (SyndEntry) feed.getEntries().get(0);

		String news = entry.getTitle() + " " + entry.getDescription().getValue();
		String[] sentences = ChunkParser.detectSentence(news);

		for (String sentence : sentences) {
			System.out.println("> " + sentence);

			Parse[] parses = ChunkParser.parse(sentence);
			Parse parse = parses[0];

			parse.show();

			System.out.println();
		}

		System.out.println();
		// }
	}

	private static String[] detectSentence(String paragraph) throws IOException {
		InputStream modelStream = ChunkParser.class.getResourceAsStream("/en-sent.bin");
		final SentenceModel model = new SentenceModel(modelStream);
		modelStream.close();

		SentenceDetector sentenceDetector = new SentenceDetectorME(model);
		String sentences[] = sentenceDetector.sentDetect(paragraph);

		return sentences;
	}

	private static Parse[] parse(String sentence) throws IOException {
		InputStream modelStream = ChunkParser.class.getResourceAsStream("/en-parser-chunking.bin");
		ParserModel model = new ParserModel(modelStream);
		modelStream.close();

		Parser parser = ParserFactory.create(model);
		Parse[] parses = ParserTool.parseLine(sentence, parser, 1);

		return parses;
	}

}
