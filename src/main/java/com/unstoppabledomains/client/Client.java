package com.unstoppabledomains.client;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

public abstract class Client {
    
    private static final String CLIENT_FILE = "client.json";

    private static final String VERSION = initClientVersion();

    public static String getVersion() {
        return VERSION;
    }

    private static String initClientVersion() {
        final InputStreamReader reader = new InputStreamReader(Client.class.getResourceAsStream(CLIENT_FILE));

        String jsonString = new BufferedReader(reader).lines().collect(Collectors.joining("\n"));
        JsonObject jsonObj = new Gson().fromJson(jsonString, JsonObject.class);

        return jsonObj.get("version").getAsString();
    }
}
