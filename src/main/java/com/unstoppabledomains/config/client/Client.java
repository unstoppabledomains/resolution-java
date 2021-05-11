package com.unstoppabledomains.config.client;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;
import java.io.InputStreamReader;

public abstract class Client {

    private static final String CLIENT_FILE = "client.json";

    private static final String VERSION = initClientVersion();

    public static String getVersion() {
        return VERSION;
    }

    private static String initClientVersion() {
        final JsonReader jsonReader =
                new JsonReader(new InputStreamReader(Client.class.getResourceAsStream(CLIENT_FILE)));
        final JsonObject jsonObj = new Gson().fromJson(jsonReader, JsonObject.class);

        return jsonObj.get("version").getAsString();
    }

}
