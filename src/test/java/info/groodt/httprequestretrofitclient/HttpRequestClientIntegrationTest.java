package info.groodt.httprequestretrofitclient;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import retrofit.client.Header;
import retrofit.client.Request;
import retrofit.client.Response;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

@RunWith(JUnit4.class)
public class HttpRequestClientIntegrationTest {

    private static final String HTTP_BIN_ROOT = "http://httpbin.org";

    @Test
    public void testGET200() throws Exception {
        // Given
        HttpRequestClient httpRequestClient = new HttpRequestClient();
        Request request = new Request("GET", HTTP_BIN_ROOT + "/status/200", null, null);

        // When
        Response response = httpRequestClient.execute(request);

        // Then
        assertNotNull(response);
        assertThat(response.getStatus(), is(200));
    }

    @Test
    public void testGETJSON() throws Exception {
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
}
