package com.unstoppabledomains.resolution.contracts;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class HTTPUtil {
    private HTTPUtil() {
    }

    public static JsonObject prepareBody(String method, JsonArray params) {
        JsonObject body = new JsonObject();
        body.addProperty("jsonrpc", "2.0");
        body.addProperty("id", 1);
        body.addProperty("method", method);
        body.add("params", params);
        return body;
    }
    
}
