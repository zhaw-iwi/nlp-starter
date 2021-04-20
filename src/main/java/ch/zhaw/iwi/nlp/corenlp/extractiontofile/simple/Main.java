package ch.zhaw.iwi.nlp.corenlp.extractiontofile.simple;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Properties;

import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;

import ch.zhaw.iwi.nlp.corenlp.FileWriteHelper;
import edu.stanford.nlp.ie.util.RelationTriple;
import edu.stanford.nlp.naturalli.NaturalLogicAnnotations;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.CoreEntityMention;
import edu.stanford.nlp.pipeline.CoreSentence;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;

public class Main {

	public static void main(String[] args) throws IllegalArgumentException, FeedException, IOException {
		URL feedSource = new URL("https://www.democracynow.org/democracynow.rss");
		SyndFeedInput input = new SyndFeedInput();
		SyndFeed feed = input.build(new XmlReader(feedSource));

		ArrayList<Item> news = new ArrayList<Item>();

		for (Object current : feed.getEntries()) {
			SyndEntry entry = (SyndEntry) current;

			news.add(new Item(entry.getTitle(), entry.getDescription().getValue()));

		}

		int nTotal = news.size();

		Properties properties = new Properties();
		properties.setProperty("annotators", "tokenize,ssplit,pos,lemma,ner,depparse,natlog,openie");
		StanfordCoreNLP pipeline = new StanfordCoreNLP(properties);

		String text;
		CoreDocument document;
		int nCurrent = 1;
		String factsCollection;
		for (Item current : news) {
			System.out.println("> Processing " + nCurrent + "/" + nTotal);
			nCurrent++;

			text = current.getTitle();
			System.out.println(text);

			document = new CoreDocument(text);
			pipeline.annotate(document);

			for (CoreSentence sentence : document.sentences()) {
				factsCollection = "";

				// Natural Entities
				for (CoreEntityMention entityMention : sentence.entityMentions()) {
					factsCollection += entityMention.entityType().toLowerCase() + "(" + entityMention.text() + "). ";
				}

				// Triples
				for (RelationTriple relationTriple : sentence.coreMap()
						.get(NaturalLogicAnnotations.RelationTriplesAnnotation.class)) {
					factsCollection += relationTriple.relationLemmaGloss() + "(" + relationTriple.subjectLemmaGloss()
							+ ", " + relationTriple.objectLemmaGloss() + "). ";
				}

				FileWriteHelper.writeToFile(text, factsCollection);

			}
			System.out.println();

		}
	}

}
