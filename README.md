# http-request-retrofit-client

A [Retrofit](http://square.github.io/retrofit/) client powered by Kevin Sawicki's [http-request](https://github.com/kevinsawicki/http-request).

## Why?
You might wonder why you would want to use anything other than the stock Retrofit clients or the fantastic OkHttp.

This is a good question, and I would strongly recommend sticking with those as well. However, I found that I had a 
need for this library during some refactoring to partially migrate a few HTTP endpoints to Retrofit while leaving 
others using http-request and AsyncTask. I was using an Application wide CookieManager and this was not being used
by the stock Retrofit clients. This left me with a problem where some HTTP calls were sharing Cookies and others were
not. I realised that if I used http-request as the common HTTP transport, then my Cookies would be shared and I could
migrate the other other HTTP endpoints later.

And a library was born!

## Useful for
1. Refactoring or migrating from http-request to Retrofit. (Primary use-case)
2. Refactoring or migrating from Retrofit to http-request. (Does anybody ever do this?)

## Usage
Setup a custom ClientProvider when you create the Retrofit RestAdapter.

      RestAdapter restAdapter = new RestAdapter.Builder()
                                      .setEndpoint(BASE_URL)
                                      .setClient(new HttpRequestClientProvider())
                                      .build();

      API api = restAdapter.create(API.class);

## Tests
I've only written a few crude integration tests covering some basic HTTP interactions 
against [httpbin](http://httpbin.org).

It probably would be better to write some unit tests. Pull requests accepted!

It has also had some basic testing in the wild, but no guarantees.
If there are issues, create an issue and I'll try help fix it.
