package info.groodt.httprequestretrofitclient;

import com.github.kevinsawicki.http.HttpRequest;
import retrofit.client.Client;
import retrofit.client.Header;
import retrofit.client.Request;
import retrofit.client.Response;
import retrofit.mime.TypedInput;
import retrofit.mime.TypedOutput;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.github.kevinsawicki.http.HttpRequest.CHARSET_UTF8;

import static com.github.kevinsawicki.http.HttpRequest.METHOD_POST;
import static com.github.kevinsawicki.http.HttpRequest.METHOD_PUT;

public class HttpRequestClient implements Client {

    @Override
    public Response execute(final Request request) throws IOException {

        HttpRequest httpRequest = HttpRequestClient.prepareHttpRequest(request);

        HttpRequestClient.sendBody(request, httpRequest);

        Response response = HttpRequestClient.getResponse(request, httpRequest);

        return response;
    }

    public static HttpRequest prepareHttpRequest(final Request request) throws IOException {

        // Extract details from incoming request from Retrofit
        final String requestUrl = request.getUrl();
        final String requestMethod = request.getMethod();
        final List<Header> requestHeaders = request.getHeaders();

        // URL and Method
        final HttpRequest httpRequest = new HttpRequest(requestUrl, requestMethod);

        // Headers
        for (Header header: requestHeaders) {
            httpRequest.header(header.getName(), header.getValue());
        }

        return httpRequest;
    }

    public static void sendBody(final Request request, final HttpRequest httpRequest) throws IOException {
        final String requestMethod = request.getMethod();
        final TypedOutput requestBody = request.getBody();

        // Only POST and PUT has body as far as I know
        if ((METHOD_POST.equals(requestMethod)) || METHOD_PUT.equals(requestMethod)) {

            if (requestBody != null) {

                // Content-Type
                httpRequest.contentType(requestBody.mimeType());

                // Payload
                ByteArrayOutputStream outputBytes = new ByteArrayOutputStream();
                requestBody.writeTo(outputBytes);
                byte[] dataBytes = outputBytes.toByteArray();

                try {
                    httpRequest.send(dataBytes);
                } catch (HttpRequest.HttpRequestException hre) {
                    // Throw original IOException since Retrofit handles this
                    throw hre.getCause();
                }
            }

        }
    }

    public static Response getResponse(final Request request, final HttpRequest httpRequest) throws IOException {
        // Execute request and get response
        final int responseCode = httpRequest.code();
        final String responseMessage = httpRequest.message();
        final String responseCharset = getValidCharSet(httpRequest);
        final byte[] bodyBytes = httpRequest.body().getBytes(responseCharset);

        // Prepare response headers
        Map<String, List<String>> httpRequestResponseHeaders = httpRequest.headers();
        List<Header> retrofitResponseHeaders = transformHttpResponseHeadersToRetrofitHeaders(httpRequestResponseHeaders);

        // Prepare response data
        TypedInput typedInput = new TypedInput() {
            @Override
            public String mimeType() {
                return httpRequest.contentType();
            }

            @Override
            public long length() {
                return bodyBytes.length;
            }

            @Override
            public InputStream in() throws IOException {
                return new ByteArrayInputStream(bodyBytes);
            }
        };

        // Response object for Retrofit
        return new Response(request.getUrl(), responseCode, responseMessage, retrofitResponseHeaders, typedInput);
    }

    private static String getValidCharSet(final HttpRequest httpRequest) {
        String charset = httpRequest.charset();
        return charset != null && charset.length() > 0 ? charset : CHARSET_UTF8;
    }

    private static List<Header> transformHttpResponseHeadersToRetrofitHeaders(final Map<String, List<String>> responseHeaders) {

        List<Header> returnHeaders = new ArrayList<Header>();

        for (Map.Entry<String, List<String>> entry : responseHeaders.entrySet()) {
            String headerName = entry.getKey();

            for (String stringHeader : entry.getValue()) {
                returnHeaders.add(new Header(headerName, stringHeader));
            }

        }

        return returnHeaders;
    }
}
