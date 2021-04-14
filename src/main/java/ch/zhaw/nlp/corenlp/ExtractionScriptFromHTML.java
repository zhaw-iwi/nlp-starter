package ch.zhaw.nlp.corenlp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import edu.stanford.nlp.ie.util.RelationTriple;
import edu.stanford.nlp.naturalli.NaturalLogicAnnotations;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.CoreEntityMention;
import edu.stanford.nlp.pipeline.CoreSentence;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;

public class ExtractionScriptFromHTML {

	public static void main(String[] args) throws IOException {

		System.out.println("> Hello :-)");

		Properties properties = new Properties();
		properties.setProperty("annotators", "tokenize,ssplit,pos,lemma,ner,depparse,natlog,openie");
		StanfordCoreNLP pipeline = new StanfordCoreNLP(properties);

		System.out.println("> Pipeline was build");

		File file = new File("resources/wiredarticle.html");
		Document htmlDocument = Jsoup.parse(file, "UTF-8");
		Elements paragraphs = htmlDocument.select("p");

		String paragraph;
		CoreDocument document;
		for (Element current: paragraphs)	{
			
			paragraph = current.text();

			System.out.println("<entry>");
			System.out.println("<text>" + paragraph + "</text>");
			System.out.println("<extracted>");

			// 2. Do annotation
			document = new CoreDocument(paragraph);
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
