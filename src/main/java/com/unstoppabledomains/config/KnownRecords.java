package com.unstoppabledomains.config;

import java.io.InputStreamReader;
import java.util.Set;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;

public class KnownRecords {
  private static final String KNOWN_RECORDS_FILE = "knownRecords.json";
  private static final JsonObject RECORDS = initKnownRecords();

  public static JsonObject getRecordsObj() {
    return RECORDS;
  }

  public static String getVersion() {
    return RECORDS.get("version").getAsString();
  }

  public static Set<String> getAllRecordKeys() {
    JsonObject keysStructures = RECORDS.get("keys").getAsJsonObject();
    return keysStructures.keySet();
  }

  private static JsonObject initKnownRecords() {
    JsonObject knowRecordsObj;
    try {
      final JsonReader jsonReader =
                  new JsonReader(new InputStreamReader(KnownRecords.class.getResourceAsStream(KNOWN_RECORDS_FILE)));
      knowRecordsObj = new Gson().fromJson(jsonReader, JsonObject.class);
    } catch (Exception e) {
      throw new RuntimeException("Couldn't load known records file", e);
  }
    return knowRecordsObj;
  }
}
