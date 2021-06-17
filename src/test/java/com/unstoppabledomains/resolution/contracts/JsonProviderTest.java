package com.unstoppabledomains.resolution.contracts;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterAll;

import org.mockserver.integration.ClientAndServer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockserver.integration.ClientAndServer.startClientAndServer;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

import java.io.IOException;

import com.google.gson.JsonSyntaxException;
import com.unstoppabledomains.resolution.TokenUriMetadata;

public class JsonProviderTest {
  private static ClientAndServer mockServer;

  @BeforeAll
  public static void startMockServer() {
      mockServer = startClientAndServer(1080);
  }

  @AfterAll
  public static void stopMockServer() {
      mockServer.stop();
  }

  @BeforeEach
  public void resetMockServer() {
    mockServer.reset();
  }

  @Test
  public void TestJsonRequest() throws IOException {
    mockServer.when(
        request()
            .withMethod("GET")
            .withPath("/get-metadata-test")
    )
    .respond(
        response()
            .withStatusCode(200)
            .withBody("{\"name\": \"test.crypto\", \"description\": \"test token description\"}")
    );

    JsonProvider provider = new JsonProvider();
    provider.setMethod("GET");
    TokenUriMetadata data = provider.request("http://localhost:1080/get-metadata-test", TokenUriMetadata.class);

    assertEquals("test.crypto", data.getName());
    assertEquals("test token description", data.getDescription());
  }

  @Test
  public void TestInvalidJsonRequest() {
    mockServer.when(
        request()
            .withMethod("GET")
            .withPath("/get-metadata-test")
    )
    .respond(
        response()
            .withStatusCode(200)
            .withBody("<invalid json>")
    );

    JsonProvider provider = new JsonProvider();
    provider.setMethod("GET");
    assertThrows(JsonSyntaxException.class, () -> provider.request("http://localhost:1080/get-metadata-test", TokenUriMetadata.class));
  }
}
