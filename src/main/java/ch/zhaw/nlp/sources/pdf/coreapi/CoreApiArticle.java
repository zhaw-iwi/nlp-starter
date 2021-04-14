package ch.zhaw.nlp.sources.pdf.coreapi;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.File;
import java.util.Map;

public class CoreApiArticle {

  private long id;
  private String description;
  private String title;
  private String fullText;
  private String downloadUrl;
  private String languageCode;
  @JsonIgnore private File file;

  @JsonProperty("language")
  private void unpackLanguageCodeFromLanguage(final Map<String, String> language) {
    this.languageCode = language.get("code");
  }

  public File getFile() {
    return this.file;
  }

  public void setFile(final File file) {
    this.file = file;
  }

  public long getId() {
    return this.id;
  }

  public void setId(final long id) {
    this.id = id;
  }

  public String getLanguageCode() {
    return this.languageCode;
  }

  public String getDownloadUrl() {
    return this.downloadUrl;
  }

  public void setDownloadUrl(final String downloadUrl) {
    this.downloadUrl = downloadUrl;
  }

  public void setLanguageCode(final String languageCode) {
    this.languageCode = languageCode;
  }

  public String getDescription() {
    return this.description;
  }

  public void setDescription(final String description) {
    this.description = description;
  }

  public String getTitle() {
    return this.title;
  }

  public void setTitle(final String title) {
    this.title = title;
  }

  public String getFullText() {
    return this.fullText;
  }

  public void setFullText(final String fullText) {
    this.fullText = fullText;
  }
}
