package ch.zhaw.opennlp;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;

import opennlp.tools.chunker.ChunkerME;
import opennlp.tools.chunker.ChunkerModel;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.sentdetect.SentenceDetector;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.SimpleTokenizer;
import opennlp.tools.util.InvalidFormatException;

public class Chunker {

	public static void main(String[] args) throws IllegalArgumentException, FeedException, IOException {
		URL feedSource = new URL("https://www.democracynow.org/democracynow.rss");
		SyndFeedInput input = new SyndFeedInput();
		SyndFeed feed = input.build(new XmlReader(feedSource));

		// System.out.println(feed);

		for (Object current : feed.getEntries()) {
			SyndEntry entry = (SyndEntry) current;

			String news = entry.getTitle() + " " + entry.getDescription().getValue();
			String[] sentences = Chunker.detectSentence(news);

			for (String sentence : sentences) {
				System.out.println("> " + sentence);

				String[] tokens = Chunker.tokenize(sentence);
				String[] posTags = Chunker.partOfSpeechTagger(tokens);

				String[] chunks = Chunker.chunk(tokens, posTags);

				for (int i = 0; i < tokens.length; i++) {
					System.out.println(tokens[i] + " [" + posTags[i] + ", " + chunks[i] + "]");
				}

				System.out.println();
			}

			System.out.println();
		}
	}

	private static String[] detectSentence(String paragraph) throws IOException {
		InputStream modelStream = Chunker.class.getResourceAsStream("/en-sent.bin");
		final SentenceModel model = new SentenceModel(modelStream);
		modelStream.close();

		SentenceDetector sentenceDetector = new SentenceDetectorME(model);
		String sentences[] = sentenceDetector.sentDetect(paragraph);

		return sentences;
	}

	private static String[] tokenize(String paragraph) {
		SimpleTokenizer tokenizer = SimpleTokenizer.INSTANCE;
		return tokenizer.tokenize(paragraph);
	}

	private static String[] partOfSpeechTagger(String[] tokens) throws IOException {
		InputStream modelStream = Chunker.class.getResourceAsStream("/en-pos-maxent.bin");
		POSModel model = new POSModel(modelStream);
		modelStream.close();

		POSTaggerME tagger = new POSTaggerME(model);
		String[] tags = tagger.tag(tokens);

		return tags;
	}

	private static String[] chunk(String[] tokens, String[] posTags) throws InvalidFormatException, IOException {
		InputStream modelStream = Chunker.class.getResourceAsStream("/en-chunker.bin");
		ChunkerModel model = new ChunkerModel(modelStream);
		modelStream.close();

		ChunkerME chunker = new ChunkerME(model);
		String[] chunks = chunker.chunk(tokens, posTags);

		return chunks;
	}
}
