package com.unstoppabledomains.config;

import java.io.InputStreamReader;
import java.util.Set;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;

public class KnownRecords {
  private static final String KNOWN_RECORDS_FILE = "knownRecords.json";

  public static String getVersion() {
    JsonObject fileContent = getFileContent();
    return fileContent.get("version").getAsString();
  }

  public static Set<String> getAllRecordKeys() {
    JsonObject fileContent = getFileContent();
    JsonObject keysStructures = fileContent.get("keys").getAsJsonObject();
    return keysStructures.keySet();
  }

  private static JsonObject getFileContent() {
    final JsonReader jsonReader =
                new JsonReader(new InputStreamReader(KnownRecords.class.getResourceAsStream(KNOWN_RECORDS_FILE)));
    final JsonObject jsonObj = new Gson().fromJson(jsonReader, JsonObject.class);
    return jsonObj;
  }
}
