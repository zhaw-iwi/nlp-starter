package ch.zhaw.wikipedia;

import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
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

	public static void main(String[] args) throws IOException, CsvValidationException {
		System.out.println("> Hello :-)");

		List<HistoricalFigure> historicalFigures = Main.readFromCSV("ressources/pantheon.tsv");
		Main.completeUsingWiki(historicalFigures);

		// TODO
		// process all figures in order to (insert into DB, create training data, ...)
	}

	/**
	 * Use of OpenCSV to read from tab-separated file
	 * 
	 * @param filePath
	 * @return
	 * @throws IOException
	 * @throws CsvValidationException
	 */
	private static List<HistoricalFigure> readFromCSV(String filePath) throws IOException, CsvValidationException {
		System.out.println("> Reading from CSV: START");

		List<HistoricalFigure> result = new ArrayList<HistoricalFigure>();

		FileReader csvFileReader = new FileReader("ressources/pantheon.tsv");
		CSVParser tsvParser = new CSVParserBuilder().withSeparator('\t').build();
		CSVReader csvReader = new CSVReaderBuilder(csvFileReader).withCSVParser(tsvParser).build();

		csvReader.readNext(); // skip header row

		// LOOP THROUGH EACH LINE
		String[] row = csvReader.readNext();
		String name;
		int birthyear;
		String birthyearRaw;
		String gender;
		String occupation;
		while (row != null) {

//			for (int i = 0; i < row.length; i++) {
//				System.out.print(row[i] + "\t");
//			}
//			System.out.println();

			name = row[1];
			birthyearRaw = row[11];
			try {
				birthyear = Integer.parseInt(birthyearRaw);
			} catch (NumberFormatException e) {
				System.out.println(name + ": " + e.getMessage());
				birthyear = -1;
			}
			gender = row[12];
			occupation = row[13];

			result.add(new HistoricalFigure(name, birthyear, gender, occupation));

			row = csvReader.readNext();
		}

		System.out.println("> Reading from CSV: DONE");
		System.out.println("> Read " + result.size() + " figures");

		return result;
	}

	/**
	 * Use of GSON to request data from WikiPedia and extract description from JSON
	 * response
	 * 
	 * @param figures
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	private static void completeUsingWiki(List<HistoricalFigure> figures) throws MalformedURLException, IOException {
		System.out.println("> Reading from WikiPedia: START");
		List<HistoricalFigure> sample = figures.subList(0, 10);

		// LOOP THROUGH EACH HISTORICAL FIGURE
		int n = 0;
		String description;
		String descriptionWithHTML;
		String nameURLEncoded;
		String url;
		Gson gson = new Gson();
		Map<String, Object> response;
		Map<String, Object> responseQuery;
		List<Object> responseQuerySearch;
		Map<String, Object> responseQuerySearchFirst;
		for (HistoricalFigure current : sample) {

			// 1. construct URL, e.g.
			// https://en.wikipedia.org/w/api.php?action=query&list=search&srsearch=Aristotle&format=json
			nameURLEncoded = URLEncoder.encode(current.getName(), StandardCharsets.UTF_8);
			url = "https://en.wikipedia.org/w/api.php?action=query&list=search&srsearch=" + nameURLEncoded
					+ "&format=json";
			System.out.println(url);

			// 2. construct reader
			InputStream jsonInputStream = new URL(url).openStream();
			Reader jsonReader = new InputStreamReader(jsonInputStream, StandardCharsets.UTF_8);

			// 3. execute request and navigate response
			response = gson.fromJson(jsonReader, Map.class);
			responseQuery = (Map<String, Object>) response.get("query");
			responseQuerySearch = (ArrayList<Object>) responseQuery.get("search");

			if (responseQuerySearch.size() > 0) {
				responseQuerySearchFirst = (Map<String, Object>) responseQuerySearch.get(0);
				descriptionWithHTML = (String) responseQuerySearchFirst.get("snippet");
				description = Main.removeHTML(descriptionWithHTML);
				n++;
			} else {
				description = null;
			}

			// 4. set description
			current.setDescription(description);
			System.out.println(description);
		}

		System.out.println("> Reading from WikiPedia: DONE");
		System.out.println("> Found " + n + " descriptions");
	}

	/**
	 * Use of JSoup to remove HTML elements
	 * 
	 * @param withHTML
	 * @return
	 */
	private static String removeHTML(String withHTML) {
		return Jsoup.parse(withHTML).text();
	}

}
