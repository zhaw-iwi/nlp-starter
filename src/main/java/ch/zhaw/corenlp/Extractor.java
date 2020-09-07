package ch.zhaw.corenlp;

import java.util.Collection;
import java.util.Properties;

import edu.stanford.nlp.ie.util.RelationTriple;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreAnnotations.NamedEntityTagAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.naturalli.NaturalLogicAnnotations;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

public class Extractor {

	private final Manager mgmt;
	private final StanfordCoreNLP pipeline;

	public static void main(String[] args) {
		String text = "Apple CEO Tim Cook wants the tech industry to take action against \"fake news\" stories that are polluting the web.";

		Manager mgmt = new Manager();
		Extractor ext = new Extractor(mgmt);
		ext.getFacts(text);

		System.out.println(mgmt);
	}

	public Extractor(Manager mgmt) {
		this.mgmt = mgmt;

		Properties properties = new Properties();
		properties.setProperty("annotators", "tokenize,ssplit,pos,lemma,ner,depparse,natlog,openie");
		this.pipeline = new StanfordCoreNLP(properties);
	}

	public void getFacts(String text) {
		CoreDocument document = new CoreDocument(text);
		this.runPipeline(document);

		for (CoreMap sentence : document.annotation().get(CoreAnnotations.SentencesAnnotation.class)) {
			// Get the OpenIE triples for the sentence Collection<RelationTriple> triples
			Collection<RelationTriple> triples = sentence.get(NaturalLogicAnnotations.RelationTriplesAnnotation.class); //

			// Print the triple- and ner-facts
			for (RelationTriple triple : triples) {

				this.mgmt.store(triple.relationLemmaGloss(), triple.subjectLemmaGloss(), triple.objectLemmaGloss());

				for (CoreLabel current : triple.subject) {
					String ner = current.get(NamedEntityTagAnnotation.class);
					if (ner != null) {
						this.mgmt.store(ner, current.lemma());
					}
				}

				for (CoreLabel current : triple.object) {
					String ner = current.get(NamedEntityTagAnnotation.class);
					if (ner != null) {
						this.mgmt.store(ner, current.lemma());
					}
				}
			}
		}
	}

	private void runPipeline(CoreDocument document) {
		this.pipeline.annotate(document);
	}

	public Manager getMgmt() {
		return mgmt;
	}
}
