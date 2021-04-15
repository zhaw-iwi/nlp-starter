package ch.zhaw.iwi.nlp.corenlp.newsextraction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.stanford.nlp.util.Pair;

public class Manager {

	private static final String EOL = "\n";

	private final Map<String, List<String>> unaryFacts;
	private final Map<String, List<Pair<String, String>>> binaryFacts;

	public Manager() {
		this.unaryFacts = new HashMap<String, List<String>>();
		this.binaryFacts = new HashMap<String, List<Pair<String, String>>>();
	}

	public void store(String fact, String arg) {

		String factKey = cleanUp(fact);
		String argClean = cleanUp(arg);

		// forward storage
		if (!this.unaryFacts.containsKey(factKey)) {
			this.unaryFacts.put(factKey, new ArrayList<String>());
		}

		List<String> values = this.unaryFacts.get(factKey);
		if (!this.contains(values, argClean)) {
			values.add(argClean);
		}
	}

	public void store(String fact, String arg1, String arg2) {

		String factKey = toCamelCase(cleanUp(fact));

		String arg1Clean = cleanUp(arg1);
		String arg1List = toList(arg1Clean);

		String arg2Clean = cleanUp(arg2);
		String arg2List = toList(arg2Clean);

		// forward storage
		if (!this.binaryFacts.containsKey(factKey)) {
			this.binaryFacts.put(factKey, new ArrayList<Pair<String, String>>());
		}
		List<Pair<String, String>> values = this.binaryFacts.get(factKey);
		values.add(new Pair<String, String>(arg1List, arg2List));
	}

	public String toString() {
		StringBuffer result = new StringBuffer();
		for (String key : this.unaryFacts.keySet()) {
			List<String> values = this.unaryFacts.get(key);
			for (String value : values) {
				result.append(key + "(" + value + ")." + Manager.EOL);
			}
		}
		for (String key : this.binaryFacts.keySet()) {
			List<Pair<String, String>> values = this.binaryFacts.get(key);
			for (Pair<String, String> value : values) {
				result.append(key + "(" + value.first + ", " + value.second + ")." + Manager.EOL);
			}
		}
		return result.toString();
	}

	private static boolean contains(List<String> hayStack, String needle) {
		for (String current : hayStack) {
			if (current.equals(needle)) {
				return true;
			}
		}
		return false;
	}

	private static String cleanUp(String string) {
		String noHTML = string.replaceAll("\\<.*?\\>", "");
		String noSpecialChars = noHTML.replaceAll("[^a-zA-Z0-9]", " ");
		String trimed = noSpecialChars.trim();
		String lower = trimed.toLowerCase();
		return lower;
	}

	private static String toCamelCase(String string) {
		String[] tokens = string.split(" ");
		if (tokens.length < 1) {
			return "";
		}

		StringBuffer result = new StringBuffer(tokens[0]);
		for (int i = 1; i < tokens.length; i++) {
			String tokenCapitalised = "";
			if (tokens[i].length() > 0) {
				tokenCapitalised += tokens[i].substring(0, 1).toUpperCase();
			}
			if (tokens[i].length() > 1) {
				tokenCapitalised += tokens[i].substring(1);
			}
			result.append(tokenCapitalised);
		}

		return result.toString();
	}

	private static String toList(String string) {
		StringBuffer result = new StringBuffer("[");

		String[] tokens = string.split(" ");
		for (int i = 0; i < tokens.length; i++) {
			if (!tokens[i].isBlank()) {
				result.append(tokens[i]);
				if (i < tokens.length - 1) {
					result.append(", ");
				}
			}
		}
		result.append("]");

		return result.toString();
	}
}
