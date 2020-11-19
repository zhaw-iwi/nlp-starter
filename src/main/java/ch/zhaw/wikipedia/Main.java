package ch.zhaw.wikipedia;

import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;

import com.google.gson.Gson;
import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvValidationException;

public class Main {

	public static void main(String[] args) throws CsvValidationException, IOException {
		System.out.println("> Hello :-)");

		// FIRST read names of historical figures in particular language
		System.out.println("> Getting names ...");

		List<String> names = new ArrayList<String>();

		CSVParser parser = new CSVParserBuilder().withSeparator(',').build();
		CSVReader csvReader = new CSVReaderBuilder(new FileReader("pantheon/wikilangs-ende.csv")).withCSVParser(parser)
				.build();

		int i = 0;
		String[] values = csvReader.readNext();
		while (values != null) {
			if (values[1].equals("en")) {
				names.add(values[2]);
			}
			values = csvReader.readNext();
		}

		System.out.println("> Getting names DONE. " + names.size() + " names read.");

		// SECOND request Wiki page for each name
		System.out.println("> Fetching from WikiPedia ...");

		Map<String, String> result = new HashMap<String, String>();

		String url;
		String nameUrlEncoded;
		for (String current : names) {
			nameUrlEncoded = URLEncoder.encode(current, StandardCharsets.UTF_8.toString());
			url = "https://en.wikipedia.org/w/api.php?action=query&list=search&srsearch=" + nameUrlEncoded
					+ "&format=json";

			InputStream is = new URL(url).openStream();
			Reader reader = new InputStreamReader(is, StandardCharsets.UTF_8);

			Gson gson = new Gson();
			Map<String, Object> deserialised = gson.fromJson(reader, Map.class);
			Map<String, Object> query = (Map<String, Object>) deserialised.get("query");
			List<Object> search = (List<Object>) query.get("search");
			if (search.size() > 0) {
				Map<String, Object> firstHit = (Map<String, Object>) search.get(0);
				String text = Jsoup.parse((String) firstHit.get("snippet")).text();

				result.put(current, text);
				
				System.out.println("> DONE with " + current + ": " + text);
			}
		}

		System.out.println("> Fetching from WikiPedia DONE. " + result.size() + " descriptions fetched.");

		// THIRD to file?
		for (String current : result.keySet()) {
			System.out.println(current + "\t" + result.get(current));
		}
	}

}
