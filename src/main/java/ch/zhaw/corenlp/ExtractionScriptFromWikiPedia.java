package ch.zhaw.corenlp;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderHeaderAwareBuilder;
import com.opencsv.exceptions.CsvValidationException;

import ch.zhaw.wikipedia.WikiPediaHelper;
import edu.stanford.nlp.ie.util.RelationTriple;
import edu.stanford.nlp.naturalli.NaturalLogicAnnotations;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.CoreEntityMention;
import edu.stanford.nlp.pipeline.CoreSentence;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;

public class ExtractionScriptFromWikiPedia {

	public static void main(String[] args) throws IOException, CsvValidationException {

		System.out.println("> Hello :-)");

		Properties properties = new Properties();
		properties.setProperty("annotators", "tokenize,ssplit,pos,lemma,ner,depparse,natlog,openie");
		StanfordCoreNLP pipeline = new StanfordCoreNLP(properties);

		System.out.println("> Pipeline was build");

		// Open and Parse CSV
		CSVParser parser = new CSVParserBuilder().withSeparator('\t').build();
		CSVReader reader = new CSVReaderHeaderAwareBuilder(new FileReader("resources/pantheon/pantheon.tsv"))
				.withCSVParser(parser).build();
		String[] currentLine = reader.readNext();
		String name;
		Long pageId;
		String personDescription;
		CoreDocument document;

		// TODO this n was introduced to save time and just look at the first couple of
		// lines. Remove this if you want to go through all of the historical figures.
		int n = 5;
		
		while (currentLine != null && n > 0) {
			// 1.1. Read name from CSV current line
			name = currentLine[1];

			// 1.2. With name, get description from WikiPedia
			pageId = WikiPediaHelper.searchPageId(name);
			personDescription = WikiPediaHelper.getPage(pageId, 3);

			System.out.println("<entry>");
			System.out.println("<text>" + personDescription + "</text>");
			System.out.println("<extracted>");

			// 2. Do annotation
			document = new CoreDocument(personDescription);
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

			currentLine = reader.readNext();
			n--;
		}

	}

}
