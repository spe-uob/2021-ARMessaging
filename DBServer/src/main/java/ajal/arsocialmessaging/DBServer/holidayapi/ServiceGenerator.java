package ajal.arsocialmessaging.DBServer.holidayapi;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;
import java.util.function.Supplier;

public class ServiceGenerator {

    private static ServiceGenerator instance;
    private HttpClient client;

    private ServiceGenerator() {
        this.client = HttpClient.newHttpClient();
    }

    public static ServiceGenerator getInstance() {
        if (instance == null) {
            instance = new ServiceGenerator();
        }
        return instance;
    }

    public HttpResponse<Supplier<Holiday[]>> getHttpResponse(Map<String,String> parameters) throws IOException, InterruptedException {
        // create a request
        String url = "https://holidays.abstractapi.com/v1/?api_key=3126d3f8235b49f7a11bd4ac1d9afc5b"
                + "&country=" + parameters.get("country")
                + "&year=" + parameters.get("year")
                + "&month=" + parameters.get("month")
                + "&day=" + parameters.get("day");
        HttpRequest request = HttpRequest.newBuilder(URI.create(url)).build();

        // use the client to send the request
        HttpResponse<Supplier<Holiday[]>> response = client.send(request, new JsonBodyHandler<>(Holiday[].class));
        return response;
    }
}
