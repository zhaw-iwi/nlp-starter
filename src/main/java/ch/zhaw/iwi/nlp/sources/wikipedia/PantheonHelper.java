package ch.zhaw.iwi.nlp.sources.wikipedia;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvValidationException;

public class PantheonHelper {

	/**
	 * Use of OpenCSV to read from tab-separated file
	 * 
	 * @param filePath
	 * @return
	 * @throws IOException
	 * @throws CsvValidationException
	 */
	public static List<HistoricalFigure> readFromCSV(String filePath, HistoricalFigureFactory factory)
			throws IOException, CsvValidationException {
		System.out.println("> Reading from CSV: START");

		List<HistoricalFigure> result = new ArrayList<HistoricalFigure>();

		FileReader csvFileReader = new FileReader(filePath);
		CSVParser tsvParser = new CSVParserBuilder().withSeparator('\t').build();
		CSVReader csvReader = new CSVReaderBuilder(csvFileReader).withCSVParser(tsvParser).build();

		csvReader.readNext(); // skip header row

		// LOOP THROUGH EACH LINE
		String[] row = csvReader.readNext();
		String name;
		String country;
		int birthYear;
		String birthCity;
		String birthYearRaw;
		String gender;
		String occupation;
		while (row != null) {

//			for (int i = 0; i < row.length; i++) {
//				System.out.print(row[i] + "\t");
//			}
//			System.out.println();

			name = row[1];
			country = row[5];
			birthYearRaw = row[11];
			try {
				birthYear = Integer.parseInt(birthYearRaw);
			} catch (NumberFormatException e) {
				System.out.println(name + ": " + e.getMessage());
				birthYear = -1;
			}
			birthCity = row[3];
			gender = row[12];
			occupation = row[13];

			result.add(factory.create(name, country, birthYear, birthCity, gender, occupation));

			row = csvReader.readNext();
		}

		System.out.println("> Reading from CSV: DONE");
		System.out.println("> Read " + result.size() + " figures");

		return result;
	}
}
