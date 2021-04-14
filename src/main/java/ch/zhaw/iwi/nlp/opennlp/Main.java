package ch.zhaw.iwi.nlp.opennlp;

import java.io.IOException;
import java.io.InputStream;

import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.tokenize.SimpleTokenizer;
import opennlp.tools.util.Span;

public class Main {

	private static final String PARAGRAPH_STRING = "We look at how the police shooting of Jacob Blake in Kenosha, Wisconsin, has brought renewed scrutiny to another case from 2018: that of Black teenager Chrystul Kizer, who faces charges of killing her alleged sex trafficker, a 34-year-old white man, when she was just 17 years old. Court records show Randall Volar had a history of sexually abusing underage Black girls that was known to the Kenosha police, but he remained free for months. In June 2018, Kizer says she shot and killed Volar in self-defense after he drugged her and tried to rape her. Kizer was freed from jail on $400,000 bail in June but is still fighting her case. \"It really says a lot about the police force there, the prosecutors there,\" says Washington Post reporter Jessica Contrera. \"Chrystul is at the center of this case that says everything about the sexual trauma that so many young Black girls go through when they are trafficked.\"";

	public static void main(String[] args) throws IOException {
		System.out.println("> Hello Open NLP");

		String[] tokens = Main.tokenize(Main.PARAGRAPH_STRING);

		Span[] datePos = Main.detectNames("/en-ner-date.bin", tokens);
		Main.printFacts("date", tokens, datePos);

		Span[] locationPos = Main.detectNames("/en-ner-location.bin", tokens);
		Main.printFacts("location", tokens, locationPos);

		Span[] moneyPos = Main.detectNames("/en-ner-money.bin", tokens);
		Main.printFacts("money", tokens, moneyPos);

		Span[] organisationPos = Main.detectNames("/en-ner-organization.bin", tokens);
		Main.printFacts("organisation", tokens, organisationPos);

		Span[] personPos = Main.detectNames("/en-ner-person.bin", tokens);
		Main.printFacts("person", tokens, personPos);

		Span[] timePos = Main.detectNames("/en-ner-time.bin", tokens);
		Main.printFacts("time", tokens, timePos);
	}

	private static void printFacts(String factName, String[] tokens, Span[] spans) {
		for (Span current : spans) {
			System.out.print(factName + "(");
			for (int i = current.getStart(); i < current.getEnd(); i++) {
				System.out.print(tokens[i]);
				if (i < current.getEnd() - 1) {
					System.out.print(" ");
				}
			}
			System.out.println(")");
		}
	}

	private static String[] tokenize(String paragraph) {
		SimpleTokenizer tokenizer = SimpleTokenizer.INSTANCE;
		return tokenizer.tokenize(paragraph);
	}

	private static Span[] detectNames(String pathToModle, String[] tokens) throws IOException {
		InputStream modelStream = Main.class.getResourceAsStream(pathToModle);
		TokenNameFinderModel model = new TokenNameFinderModel(modelStream);
		modelStream.close();

		NameFinderME nameFinder = new NameFinderME(model);
		Span nameSpans[] = nameFinder.find(tokens);

		return nameSpans;
	}

}
