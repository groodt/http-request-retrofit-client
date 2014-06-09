package info.groodt.httprequestretrofitclient;

import retrofit.client.Client;

public class HttpRequestClientProvider implements Client.Provider {

    private final Client client = new HttpRequestClient();

    @Override
    public Client get() {
        return client;
    }
}
