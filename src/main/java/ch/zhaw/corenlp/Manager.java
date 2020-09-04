package ch.zhaw.corenlp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.stanford.nlp.util.Pair;

public class Manager {

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

	public void store(String fact, String arg) {

		String factKey = fact.toLowerCase().trim().replace(" ", "");
		String argClean = arg.toLowerCase().trim().replace(" ", "");

		if (!this.singleValuedFacts.containsKey(factKey)) {
			this.singleValuedFacts.put(factKey, new ArrayList<String>());
		}

		List<String> values = this.singleValuedFacts.get(factKey);
		if (!this.contains(values, argClean)) {
			values.add(argClean);
		}
	}

	public void store(String fact, String arg1, String arg2) {
		String factKey = fact.toLowerCase().trim().replace(" ", "");
		String arg1Clearn = arg1.toLowerCase().trim().replace(" ", "");
		String arg2Clearn = arg2.toLowerCase().trim().replace(" ", "");
		if (!this.doubleValuedFacts.containsKey(factKey)) {
			this.doubleValuedFacts.put(factKey, new ArrayList<Pair<String, String>>());
		}
		this.doubleValuedFacts.get(factKey).add(new Pair<String, String>(arg1Clearn, arg2Clearn));
	}

	public void out() {
		for (String key : this.singleValuedFacts.keySet()) {
			List<String> values = this.singleValuedFacts.get(key);
			for (String value : values) {
				System.out.println(key + "(" + value + ")");
			}
		}
		for (String key : this.doubleValuedFacts.keySet()) {
			List<Pair<String, String>> values = this.doubleValuedFacts.get(key);
			for (Pair<String, String> value : values) {
				System.out.println(key + "(" + value.first + ", " + value.second + ")");
			}
		}
	}
}
