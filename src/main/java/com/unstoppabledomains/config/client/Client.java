package com.unstoppabledomains.config.client;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public abstract class Client {

    private static final String CLIENT_FILE = "client.json";

    private static final String VERSION = initClientVersion();

    public static String getVersion() {
        return VERSION;
    }

    private static String initClientVersion() {
        final InputStreamReader reader = new InputStreamReader(Client.class.getResourceAsStream(CLIENT_FILE));
        final BufferedReader buffer = new BufferedReader(reader);
        try {
            String jsonString = "";
            String line = buffer.readLine();
            while ( line != null) {
                jsonString = jsonString.concat(line);
                line = buffer.readLine();
            }
            final JsonObject jsonObj = new Gson().fromJson(jsonString, JsonObject.class);

            return jsonObj.get("version").getAsString();
        } catch(IOException err) {
            err.printStackTrace();
        }
        return "-2";
    }
}
