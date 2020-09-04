package ch.zhaw.corenlp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.stanford.nlp.util.Pair;

public class Manager {

	private static final String EOL = "\n";

	private final Map<String, List<String>> singleValuedFacts;
	private final Map<String, List<Pair<String, String>>> doubleValuedFacts;

	public Manager() {
		this.singleValuedFacts = new HashMap<String, List<String>>();
		this.doubleValuedFacts = new HashMap<String, List<Pair<String, String>>>();
	}

	private boolean contains(List<String> hayStack, String needle) {
		for (String current : hayStack) {
			if (current.equals(needle)) {
				return true;
			}
		}
		return false;
	}

	private String cleanUp(String string) {
		String trimed = string.trim();
		if (trimed.endsWith(".")) {
			trimed = trimed.substring(0, trimed.length() - 1);
		}

		String noSpecialChars = trimed.replace("'", "").replace(".", " ");
		String lower = noSpecialChars.toLowerCase();
		String[] tokens = lower.split(" ");
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

	public void store(String fact, String arg) {

		String factKey = cleanUp(fact);
		String argClean = cleanUp(arg);

		if (!this.singleValuedFacts.containsKey(factKey)) {
			this.singleValuedFacts.put(factKey, new ArrayList<String>());
		}

		List<String> values = this.singleValuedFacts.get(factKey);
		if (!this.contains(values, argClean)) {
			values.add(argClean);
		}
	}

	public void store(String fact, String arg1, String arg2) {
		String factKey = cleanUp(fact);
		String arg1Clearn = cleanUp(arg1);
		String arg2Clearn = cleanUp(arg2);
		if (!this.doubleValuedFacts.containsKey(factKey)) {
			this.doubleValuedFacts.put(factKey, new ArrayList<Pair<String, String>>());
		}
		this.doubleValuedFacts.get(factKey).add(new Pair<String, String>(arg1Clearn, arg2Clearn));
	}

	public String toString() {
		StringBuffer result = new StringBuffer();
		for (String key : this.singleValuedFacts.keySet()) {
			List<String> values = this.singleValuedFacts.get(key);
			for (String value : values) {
				result.append(key + "(" + value + ")." + Manager.EOL);
			}
		}
		for (String key : this.doubleValuedFacts.keySet()) {
			List<Pair<String, String>> values = this.doubleValuedFacts.get(key);
			for (Pair<String, String> value : values) {
				result.append(key + "(" + value.first + ", " + value.second + ")." + Manager.EOL);
			}
		}
		return result.toString();
	}
}
