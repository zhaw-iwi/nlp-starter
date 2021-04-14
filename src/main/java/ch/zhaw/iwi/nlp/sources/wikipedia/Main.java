package ch.zhaw.iwi.nlp.sources.wikipedia;

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
import com.opencsv.exceptions.CsvValidationException;

public class Main {

	public static void main(String[] args) throws IOException, CsvValidationException {
		System.out.println("> Hello :-)");

		List<HistoricalFigure> historicalFigures = PantheonHelper.readFromCSV("resources/pantheon/pantheon.tsv",
				new HistoricalFigureFactory());
		// Main.completeUsingWikiQueries(historicalFigures);
		Main.completeUsingWikiPages(historicalFigures, 3);

		// TODO
		// process all figures in order to insert into DB, create training data, ...
	}

	/**
	 * Use of GSON to request data from WikiPedia and extract description from JSON
	 * response. We extract the first nOfSentences of sentences from each page.
	 * 
	 * @param figures
	 * @param nOfSentences How many of the first sentences per page?
	 */
	public static void completeUsingWikiPages(List<HistoricalFigure> figures, Integer nOfSentences) {
		System.out.println("> Reading from WikiPedia (First " + nOfSentences + " Sentences from Page): START");

		// TODO we take the first ten figures only for demo purposes (speed :-))
		List<HistoricalFigure> sample = figures.subList(0, 10);

		int n = 0;
		Long pageId;
		String extract;
		for (HistoricalFigure current : sample) {
			pageId = WikiPediaHelper.searchPageId(current.getName());
			if (pageId != null) {
				extract = WikiPediaHelper.getPage(pageId, nOfSentences);
				System.out.println(extract);
				current.setDescription(extract);
				n++;
			}
		}

		System.out.println("> Reading from WikiPedia (First " + nOfSentences + " Sentences from Page): DONE");
		System.out.println("> Found " + n + " descriptions");
	}

	/**
	 * Use of GSON to request data from WikiPedia and extract description from JSON
	 * response. We use WikiPedia queries, and get search result excerpts. Note that
	 * these excerpts just contain the first couple of sentences of a page. This is
	 * not what we want if we want "real" data.
	 * 
	 * @param figures
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	public static void completeUsingWikiQueries(List<HistoricalFigure> figures)
			throws MalformedURLException, IOException {
		System.out.println("> Reading from WikiPedia (Query Result Excerpts): START");
		// TODO we take the first ten figures only for demo purposes (speed :-))
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
				description = Jsoup.parse(descriptionWithHTML).text();
				n++;
			} else {
				description = null;
			}

			// 4. set description
			current.setDescription(description);
			System.out.println(description);
		}

		System.out.println("> Reading from WikiPedia (Query Result Excerpts): DONE");
		System.out.println("> Found " + n + " descriptions");
	}
}
