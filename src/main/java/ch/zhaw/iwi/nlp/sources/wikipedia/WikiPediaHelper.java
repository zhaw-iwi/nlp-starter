package ch.zhaw.iwi.nlp.sources.wikipedia;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;

public class WikiPediaHelper {

	public static String getPage(Long pageId, Integer sentences) {
		String result = null;
		String url = "https://en.wikipedia.org/w/api.php?action=query&format=json&prop=extracts&pageids="
				+ pageId.intValue() + "&exsentences=" + sentences.toString() + "&explaintext=1";

		InputStream is;
		try {
			is = new URL(url).openStream();
			Reader reader = new InputStreamReader(is, StandardCharsets.UTF_8);

			Gson gson = new Gson();
			Map<String, Object> deserialised = gson.fromJson(reader, Map.class);
			Map<String, Object> query = (Map<String, Object>) deserialised.get("query");
			Map<String, Object> pages = (Map<String, Object>) query.get("pages");
			Map<String, Object> page = (Map<String, Object>) pages.get(pageId.toString());
			result = (String) page.get("extract");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	public static Long searchPageId(String name) {
		Long pageId = null;
		String nameUrlEncoded = null;
		try {
			nameUrlEncoded = URLEncoder.encode(name, StandardCharsets.UTF_8.toString());
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
			return pageId;
		}
		String url = "https://en.wikipedia.org/w/api.php?action=query&prop=extracts&explaintext=1&list=search&srsearch="
				+ nameUrlEncoded + "&format=json";

		InputStream is;
		try {
			is = new URL(url).openStream();
			Reader reader = new InputStreamReader(is, StandardCharsets.UTF_8);

			Gson gson = new Gson();
			Map<String, Object> deserialised = gson.fromJson(reader, Map.class);
			Map<String, Object> query = (Map<String, Object>) deserialised.get("query");
			List<Object> search = (List<Object>) query.get("search");
			if (search.size() > 0) {
				Map<String, Object> firstHit = (Map<String, Object>) search.get(0);
				pageId = Double.valueOf(String.valueOf(firstHit.get("pageid"))).longValue();
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return pageId;
	}
}
