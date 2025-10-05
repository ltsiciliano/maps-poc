# maps-poc

Proof of Concept (PoC) Spring Boot API for integrating with Google Maps (Geocoding, Reverse Geocoding, and Places Autocomplete).

Everything is already configured so you only need to provide your Google Maps API Key. We also include:
- Swagger/OpenAPI (springdoc) with UI at `/swagger-ui.html`
- Actuator (health/info)
- Devtools (hot reload for development)
- Controller + Service layers
- GlobalExceptionHandler with `@RestControllerAdvice` to standardize error responses (400/500)
- Lombok to reduce boilerplate (constructors, getters/setters)
- Tests (unit and web) with Spring Boot Test and MockMvc
- JaCoCo for test coverage reports
- Unofficial OpenAPI (YAML) specs for the Google endpoints used (for potential client generation)

## Requirements
- Java 17+
- Maven 3.9+
- A valid Google Maps Platform API Key with the necessary services enabled (Geocoding API and Places API)

## API Key configuration
Set the environment variable `GOOGLE_MAPS_API_KEY` before starting the application:

```bash
export GOOGLE_MAPS_API_KEY="YOUR_API_KEY_HERE"
```

Alternatively, you can create a non-versioned `application-local.yml` and configure:

```yaml
google:
  maps:
    api-key: YOUR_API_KEY_HERE
```

> Note: by default, the application reads `google.maps.api-key` and/or the `GOOGLE_MAPS_API_KEY` environment variable.

## How to run

```bash
# build and run
mvn spring-boot:run

# or package and run
mvn clean package
java -jar target/maps-poc-0.0.1-SNAPSHOT.jar
```

The application will start at `http://localhost:8080`.

- Swagger UI: http://localhost:8080/swagger-ui.html
- OpenAPI JSON: http://localhost:8080/v3/api-docs
- Actuator: http://localhost:8080/actuator (health, info)

## Available endpoints

Base path: `/api/maps`

- `GET /api/maps/geocode?address=...&language=pt-BR` — Geocode an address (calls Google Geocoding API)
- `GET /api/maps/reverse?lat=-23.5&lng=-46.6&language=pt-BR` — Reverse geocoding by coordinates (calls Google Geocoding API)
- `GET /api/maps/places/autocomplete?input=Avenida%20Paulista&language=pt-BR` — Places Autocomplete (calls Google Places Autocomplete)

Responses return the raw JSON received from Google (same structure as Google) for simplicity in this PoC.

### cURL examples

```bash
curl "http://localhost:8080/api/maps/geocode?address=Avenida%20Paulista%2C%20S%C3%A3o%20Paulo&language=pt-BR"

curl "http://localhost:8080/api/maps/reverse?lat=-23.561414&lng=-46.656532&language=pt-BR"

curl "http://localhost:8080/api/maps/places/autocomplete?input=Avenida%20Paulista&language=pt-BR"
```

## Main structure

```
src/main/java/com/example/mapspoc
├── MapsPocApplication.java
├── config
│   ├── GoogleMapsProperties.java  # reads google.maps.api-key
│   └── HttpClientConfig.java      # RestTemplate
├── controller
│   └── GoogleMapsController.java  # exposure layer (Swagger annotated)
├── service
│   └── GoogleMapsService.java     # integrates with Google Maps via REST
└── exception
    └── GlobalExceptionHandler.java

src/main/resources
├── application.yml
└── openapi/google
    ├── geocoding.yaml             # Unofficial spec for geocoding endpoint
    └── places-autocomplete.yaml   # Unofficial spec for places autocomplete endpoint
```

## Google OpenAPI (YAML)

The directory `src/main/resources/openapi/google` contains minimal (unofficial) OpenAPI specifications for the Google endpoints used by this PoC. You can use them to generate a typed HTTP client if you wish.

We have already set up an `openapi-generator-maven-plugin` entry in `pom.xml` (pointing to `geocoding.yaml`). It is configured to run in the `generate-sources` phase. If you prefer to disable it, use the `no-openapi-gen` profile.

### Generate client from YAML (optional)

```bash
# generates client code for geocoding (output in target/generated-sources/openapi-geocoding)
mvn generate-sources
```

You could then point your service to that generated client instead of the manual `RestTemplate` (this PoC keeps it simple with RestTemplate).

## Tests

Run the test suite:

```bash
mvn test
```

### Code Coverage (JaCoCo)

The JaCoCo plugin is already configured in `pom.xml`. To generate the coverage report:

```bash
mvn clean verify
```

Then open the report at:

- `target/site/jacoco/index.html`

## Notes
- There is no security configured at this time (as requested).
- Make sure to enable the required services (Geocoding API, Places API) in your Google Cloud account and to properly restrict your API Key.
- Common errors will return a standardized JSON payload via our `GlobalExceptionHandler`.
- If you use an IDE, enable annotation processing for Lombok.

## Versioning

The project has been prepared for the suggested GitHub repository: `https://github.com/ltsiciliano/maps-poc.git`. You can push from your environment.
