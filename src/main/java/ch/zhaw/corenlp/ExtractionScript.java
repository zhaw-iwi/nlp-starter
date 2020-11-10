package ch.zhaw.corenlp;

import java.io.IOException;
import java.net.URL;
import java.util.Properties;

import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;

import edu.stanford.nlp.ie.util.RelationTriple;
import edu.stanford.nlp.naturalli.NaturalLogicAnnotations;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.CoreEntityMention;
import edu.stanford.nlp.pipeline.CoreSentence;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;

public class ExtractionScript {

	public static void main(String[] args) throws IllegalArgumentException, FeedException, IOException {

		System.out.println("> Hello :-)");

		Properties properties = new Properties();
		properties.setProperty("annotators", "tokenize,ssplit,pos,lemma,ner,depparse,natlog,openie");
		StanfordCoreNLP pipeline = new StanfordCoreNLP(properties);

		System.out.println("> Pipeline was build");

		URL feedSource = new URL("https://www.democracynow.org/democracynow.rss");
		SyndFeedInput input = new SyndFeedInput();
		SyndFeed feed = input.build(new XmlReader(feedSource));

		String title;
		String content;
		CoreDocument document;
		for (Object current : feed.getEntries()) {
			// 1. Extract text from RSS item
			title = ((SyndEntry) current).getTitle();
			// content = ((SyndEntry) current).getDescription().getValue();

			System.out.println("<entry>");
			System.out.println("<text>" + title + "</text>");
			System.out.println("<extracted>");

			// 2. Do annotation
			document = new CoreDocument(title);
			pipeline.annotate(document);

			// 3. Extract annotations
			for (CoreSentence sentence : document.sentences()) {

				// 3.1 Natural Entities
				for (CoreEntityMention entityMention : sentence.entityMentions()) {
					System.out.println(entityMention.entityType().toLowerCase() + "(" + entityMention.text() + ")");
				}

				// 3.2 Triples
				for (RelationTriple relationTriple : sentence.coreMap()
						.get(NaturalLogicAnnotations.RelationTriplesAnnotation.class)) {
					System.out.println(relationTriple.relationLemmaGloss() + "(" + relationTriple.subjectLemmaGloss()
							+ ", " + relationTriple.objectLemmaGloss() + ")");
				}
			}
			System.out.println("</extracted>");
			System.out.println("</entry>");

		}

	}

}
