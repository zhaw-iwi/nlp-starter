package ch.zhaw.iwi.nlp.sources.pdf.coreapi;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import kong.unirest.UnirestException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CoreApiConnector {

  private static final String API_KEY = "KrYbp1CchyNJU6tvdoE7FG4TieOwnDZP";
  private static final String BASE_URL = "https://core.ac.uk/api-v2";
  private static final String STATUS_OK = "OK";
  private static final int MAX_PAGE_SIZE = 100;

  private final File dummyFile = new File("");
  private final String absolutePath = this.dummyFile.getAbsolutePath() + "/pdfOut/coreApiParser";
  private int totalHits;
  private int offset;
  private String requestStatus;
  private Map<String, CoreApiArticle> coreApiArticles = new HashMap<>();

  public Map<String, CoreApiArticle> getDocuments(final String query, final int limit) {
    return getDocuments(query, 1, limit);
  }

  public Map<String, CoreApiArticle> getDocuments(
      final String query, final int page, final int limit) {

    final String directoryPath = this.absolutePath + "/" + query;
    final File dummyDirectoryFile = new File(directoryPath);
    if (!dummyDirectoryFile.exists()) {
      dummyDirectoryFile.mkdirs();
      System.out.println("Creating directory: " + directoryPath);
    }

    try (final Stream<File> fileStream =
        Files.walk(Paths.get(directoryPath))
            .filter(path -> path.toString().contains(".pdf"))
            .map(Path::toFile)) {
      if (fileStream.count() < limit) {

        try {

          final HttpResponse<JsonNode> jsonResponse =
              Unirest.get(
                      BASE_URL
                          + "/articles/search/%22"
                          + query
                          + "%22?pageSize="
                          + MAX_PAGE_SIZE
                          + "&page="
                          + page)
                  .header("accept", "application/json")
                  .header("apiKey", API_KEY)
                  .asJson();

          this.requestStatus = (String) jsonResponse.getBody().getObject().get("status");
          if (!STATUS_OK.equals(this.requestStatus)) {
            throw new RuntimeException("Request failed, check API-KEY and error response");
          }
          if (this.offset == 0) {
            this.totalHits = (int) jsonResponse.getBody().getObject().get("totalHits");
          }

          final ObjectMapper objectMapper = new ObjectMapper();
          objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

          final List<CoreApiArticle> articles =
              objectMapper.readValue(
                  jsonResponse.getBody().getObject().get("data").toString(),
                  new TypeReference<>() {});

          System.out.println("Received " + articles.size() + " articles");

          final List<CoreApiArticle> filteredArticles =
              articles.stream()
                  .filter(
                      article ->
                          "en".equals(article.getLanguageCode())
                              && article.getDownloadUrl().contains("core.ac.uk")
                              && article.getDescription() != null)
                  .collect(Collectors.toList());

          System.out.println("Remaining articles after filtering: " + filteredArticles.size());

          for (final CoreApiArticle filteredArticle : filteredArticles) {
            final String filePath =
                this.absolutePath + "/" + query + "/" + filteredArticle.getId() + ".pdf";
            final File file = new File(filePath);

            if (!file.exists()) {
              Unirest.get(BASE_URL + "/articles/get/" + filteredArticle.getId() + "/download/pdf")
                  .accept("application/pdf")
                  .header("apiKey", API_KEY)
                  .asFile(filePath)
                  .getBody();

              System.out.println("Saving file to " + filePath);
            }
            filteredArticle.setFile(file);
            this.coreApiArticles.put(String.valueOf(filteredArticle.getId()), filteredArticle);
          }

          this.offset++;
          System.out.println(
              "Processed " + this.offset * MAX_PAGE_SIZE + " of " + this.totalHits + " articles");
          if (this.offset * MAX_PAGE_SIZE < this.totalHits && this.coreApiArticles.size() < limit) {
            getDocuments(query, this.offset, limit);
          }

        } catch (final UnirestException e) {
          e.printStackTrace();
        } catch (final JsonMappingException e) {
          e.printStackTrace();
        } catch (final JsonProcessingException e) {
          e.printStackTrace();
        }
      } else {
        System.out.println("already enough files for " + query);
        extractDocumentInformation(directoryPath);
      }

    } catch (final IOException e) {
      e.printStackTrace();
    }

    return this.coreApiArticles;
  }

  private void extractDocumentInformation(final String directoryPath) {
    System.out.println("Requesting data for existing pdf's");

    try (final Stream<File> fileStream =
        Files.walk(Paths.get(directoryPath))
            .filter(path -> path.toString().contains(".pdf"))
            .map(Path::toFile)) {
      fileStream.forEach(
          file -> {
            CoreApiArticle article = new CoreApiArticle();
            article.setId(Long.parseLong(file.getName().replace(".pdf", "")));

            final HttpResponse<JsonNode> jsonResponse =
                Unirest.get(BASE_URL + "/articles/get/" + article.getId())
                    .header("accept", "application/json")
                    .header("apiKey", API_KEY)
                    .asJson();

            this.requestStatus = (String) jsonResponse.getBody().getObject().get("status");
            if (!STATUS_OK.equals(this.requestStatus)) {
              throw new RuntimeException("Request failed, check API-KEY and error response");
            }

            final ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            try {
              article =
                  objectMapper.readValue(
                      jsonResponse.getBody().getObject().get("data").toString(),
                      CoreApiArticle.class);
              article.setFile(file);
            } catch (final JsonProcessingException e) {
              e.printStackTrace();
            }

            this.coreApiArticles.put(String.valueOf(article.getId()), article);
            System.out.println("added information for " + article.getId());
          });

    } catch (final IOException e) {
      e.printStackTrace();
    }
  }
}
