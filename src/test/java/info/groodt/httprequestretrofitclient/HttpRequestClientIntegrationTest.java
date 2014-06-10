package info.groodt.httprequestretrofitclient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.google.common.io.CharSource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import retrofit.client.Header;
import retrofit.client.Request;
import retrofit.client.Response;
import retrofit.mime.TypedOutput;
import retrofit.mime.TypedString;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

@RunWith(JUnit4.class)
public class HttpRequestClientIntegrationTest {

    private static final String HTTP_BIN_ROOT = "http://httpbin.org";
    private static final String STATUS_PATH = "/status/";

    @Test
    public void testGET200() throws Exception {
        // Given
        HttpRequestClient httpRequestClient = new HttpRequestClient();
        Request request = new Request("GET", HTTP_BIN_ROOT + STATUS_PATH + "200", null, null);

        // When
        Response response = httpRequestClient.execute(request);

        // Then
        assertNotNull(response);
        assertThat(response.getStatus(), is(200));
        assertThat(response.getReason(), is("OK"));
    }

    @Test
    public void testGET404() throws Exception {
        // Given
        HttpRequestClient httpRequestClient = new HttpRequestClient();
        Request request = new Request("GET", HTTP_BIN_ROOT + STATUS_PATH + "404", null, null);

        // When
        Response response = httpRequestClient.execute(request);

        // Then
        assertNotNull(response);
        assertThat(response.getStatus(), is(404));
        assertThat(response.getReason(), is("NOT FOUND"));
    }

    @Test
    public void testGETJSONContentTypeHeader() throws Exception {
        // Given
        HttpRequestClient httpRequestClient = new HttpRequestClient();
        Request request = new Request("GET", HTTP_BIN_ROOT + "/get", null, null);

        // When
        Response response = httpRequestClient.execute(request);

        // Then
        assertNotNull(response);
        assertThat(response.getStatus(), is(200));
        assertThat(response.getHeaders(), hasItem(new Header("Content-Type", "application/json")));
    }

    @Test
    public void testPOST200() throws Exception {
        // Given
        HttpRequestClient httpRequestClient = new HttpRequestClient();
        Request request = new Request("POST", HTTP_BIN_ROOT + "/post", null, null);

        // When
        Response response = httpRequestClient.execute(request);

        // Then
        assertNotNull(response);
        assertThat(response.getStatus(), is(200));
    }

    @Test
    public void testPOSTDataEcho() throws Exception {
        // Given
        final String postBodyString = "hello";
        HttpRequestClient httpRequestClient = new HttpRequestClient();
        TypedOutput postBody = new TypedString(postBodyString);
        Request request = new Request("POST", HTTP_BIN_ROOT + "/post", null, postBody);

        // When
        final Response response = httpRequestClient.execute(request);
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> jsonObj = objectMapper.readValue(response.getBody().in(), Map.class);

        // Then
        assertNotNull(response);
        assertThat(response.getStatus(), is(200));
        assertThat(jsonObj.get("data").toString(), is(postBodyString));
    }

    @Test
    public void testSendCustomHeader() throws Exception {
        // Given
        final String X_CUSTOM_AUTH = "X-Custom-Auth";
        final String SOME_AUTH_TOKEN = "SOMEAUTHTOKEN";
        HttpRequestClient httpRequestClient = new HttpRequestClient();
        List<Header> customHeaders = ImmutableList.of(new Header(X_CUSTOM_AUTH, SOME_AUTH_TOKEN));
        Request request = new Request("GET", HTTP_BIN_ROOT + "/get", customHeaders, null);

        // When
        final Response response = httpRequestClient.execute(request);
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> jsonObj = objectMapper.readValue(response.getBody().in(), Map.class);
        Map<String, Object> sentHeaders = (Map<String, Object>) jsonObj.get("headers");

        // Then
        assertNotNull(response);
        assertThat(response.getStatus(), is(200));
        assertThat(sentHeaders.get(X_CUSTOM_AUTH).toString(), is(SOME_AUTH_TOKEN));
    }
}
