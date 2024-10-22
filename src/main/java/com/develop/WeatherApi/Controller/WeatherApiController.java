package com.develop.WeatherApi.Controller;

import com.develop.WeatherApi.Configuration.SecurityConfig;
import com.develop.WeatherApi.Model.GeoLocationResponse;
import com.develop.WeatherApi.Model.WeatherModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@RestController
public class WeatherApiController {
    private final ChatClient chatClient;

    @Autowired
    public WeatherApiController(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }
    private Double latestTemperatureCelsius;
    @Autowired
    private WebClient.Builder webClientBuilder;

    @Value("${openweathermap.api.key}")
    private String API_KEY;


    @GetMapping("/convert")
    public Mono<GeoLocationResponse[]> convertCityToLatLon(
            @RequestParam String city,
            @RequestParam(required = false) String state,
            @RequestParam String country,
            @RequestParam(defaultValue = "1") int limit
    ) {
        // Constructing the URL for the API
        String uri = String.format(
                "http://api.openweathermap.org/geo/1.0/direct?q=%s,%s,%s&limit=%d&appid=%s",
                city, state != null ? state : "", country, limit, API_KEY
        );

        // Make the API call using WebClient
        return webClientBuilder.build()
                .get()
                .uri(uri)
                .retrieve()
                .bodyToMono(GeoLocationResponse[].class);  // Map the response to an array of GeoLocationResponse
    }

    @GetMapping(value = "/fetchWeather", produces = "application/json")
    public Mono<ResponseEntity<WeatherModel>> fetchWeather(@RequestParam String city,
                                                           @RequestParam(required = false) String state,
                                                           @RequestParam String country) {
        // Fetch the location data asynchronously
        return convertCityToLatLon(city, state, country, 1)
                .flatMap(geoDataArray -> {
                    // Check if the data is available
                    if (geoDataArray != null && geoDataArray.length > 0) {
                        GeoLocationResponse geoData = geoDataArray[0];  // Get the first result

                        // Construct the weather API URI using the lat and lon
                        String uri = String.format(
                                "https://api.openweathermap.org/data/2.5/weather?lat=%s&lon=%s&appid=%s",
                                geoData.getLat(), geoData.getLon(), API_KEY
                        );

                        // Make the second API call to get weather data
                        return webClientBuilder.build()
                                .get()
                                .uri(uri)
                                .retrieve()
                                .bodyToMono(WeatherModel.class)
                                .map(weatherResponse -> {
                                    // Store the temperature in Celsius for later use
                                    double temperatureKelvin = weatherResponse.getMain().getTemp();
                                    latestTemperatureCelsius = temperatureKelvin - 273.15;  // Convert to Celsius

                                    return ResponseEntity.ok(weatherResponse);  // Return the weather data
                                });
                    } else {
                        return Mono.just(ResponseEntity.notFound().build());
                    }
                });
    }

    @GetMapping("/suggestOutfit")
    public String suggestOutfit() {

        String prompt = String.format("Suggest an outfit for the current temperature of %.2f°C.", latestTemperatureCelsius);
        String msg = """
                hello""";
        PromptTemplate temp = new PromptTemplate(msg);
        Prompt prompt1 = temp.create();
        ChatResponse response = chatClient.prompt(prompt1)
                .call()
                .chatResponse();
        return response.getResult().getOutput().getContent();
            }

            @GetMapping("/checking")
            public String Checking(Authentication authentication){
            return authentication.getPrincipal().toString();
            }
    private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);

    @GetMapping("/")
    public String testAuthorities(Authentication authentication) {
        logger.info("Authorities for the current user: {}", authentication.getAuthorities());
        return "Hello: " + authentication.getName();
    }

}


