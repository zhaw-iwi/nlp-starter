package ch.zhaw.corenlp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import edu.stanford.nlp.ie.util.RelationTriple;
import edu.stanford.nlp.naturalli.NaturalLogicAnnotations;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.CoreEntityMention;
import edu.stanford.nlp.pipeline.CoreSentence;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;

public class ExtractionScriptFromTXT {

	public static void main(String[] args) throws IOException {

		System.out.println("> Hello :-)");

		Properties properties = new Properties();
		properties.setProperty("annotators", "tokenize,ssplit,pos,lemma,ner,depparse,natlog,openie");
		StanfordCoreNLP pipeline = new StanfordCoreNLP(properties);

		System.out.println("> Pipeline was build");

		File file = new File("resources/biblical/bible-luke.txt");
		BufferedReader br = new BufferedReader(new FileReader(file));

		String line;
		CoreDocument document;
		while ((line = br.readLine()) != null) {

			System.out.println("<entry>");
			System.out.println("<text>" + line + "</text>");
			System.out.println("<extracted>");

			// 2. Do annotation
			document = new CoreDocument(line);
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
