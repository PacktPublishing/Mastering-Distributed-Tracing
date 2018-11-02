package lib;

import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class GiphyService {
    private final static String GIPHY_URL = "http://api.giphy.com/v1/gifs/search";
    private final static String GIPHY_API_TOKEN = "DhFQzq6E4uSzDgx6FFmTC0xqV0iFYDFK";
    private final static String NO_IDEA = "https://media0.giphy.com/media/3o6UBil4zn1Tt03PI4/giphy.gif";

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Autowired
    RestTemplate restTemplate;

    public String query(String query) {
        try {
            URI uri = UriComponentsBuilder //
                .fromHttpUrl(GIPHY_URL) //
                .queryParam("q", query) //
                .queryParam("api_key", GIPHY_API_TOKEN) //
                .queryParam("limit", "10") //
                .queryParam("rating", "pg") //
                .build(Collections.emptyMap());
            ResponseEntity<Response> response = restTemplate.getForEntity(uri, Response.class);
            List<String> urls = response.getBody().data.stream().filter(
                d -> d.images != null && d.images.original != null && d.images.original.url != null
            ).map(d -> d.images.original.url).collect(Collectors.toList());
            if (urls.isEmpty()) {
                System.out.println("Giphy returned no images for " + query);
                return NO_IDEA;
            }
            System.out.println("Giphy returned " + urls.size() + " images for " + query);
            int pick = (int)(Math.random() * urls.size());
            String url = urls.get(pick);
            return url;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return NO_IDEA;
    }

    public static class Response {
        public List<Data> data;
    }

    public static class Data {
        public Images images;
    }

    public static class Images {
        public Image original;
    }

    public static class Image {
        public String url;
    }
}