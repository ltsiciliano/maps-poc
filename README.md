# maps-poc

PoC (proof of concept) de uma API Spring Boot para integração com o Google Maps (Geocoding, Reverse Geocoding e Places Autocomplete).

Tudo já está configurado para você apenas informar a sua API Key do Google Maps. Incluímos também:
- Swagger/OpenAPI (springdoc) com UI em `/swagger-ui.html`
- Actuator (health/info)
- Devtools (hot reload em desenvolvimento)
- Camadas Controller + Service
- GlobalExceptionHandler com `@RestControllerAdvice` para padronizar respostas de erro (400/500)
- Lombok para reduzir boilerplate (construtores, getters/setters)
- Testes (unitários e web) com Spring Boot Test e MockMvc
- JaCoCo para relatório de cobertura de testes
- Especificações OpenAPI (YAML) não oficiais para os endpoints do Google usados (para futura geração de cliente, se desejar)

## Requisitos
- Java 17+
- Maven 3.9+
- Uma API Key válida do Google Maps Platform com os serviços habilitados (Geocoding API e Places API)

## Configuração da API Key
Defina a variável de ambiente `GOOGLE_MAPS_API_KEY` antes de iniciar a aplicação:

```bash
export GOOGLE_MAPS_API_KEY="SUA_API_KEY_AQUI"
```

Alternativamente, você pode criar um `application-local.yml` (não versionado) e configurar:

```yaml
google:
  maps:
    api-key: SUA_API_KEY_AQUI
```

> Observação: por padrão, a aplicação lê `google.maps.api-key` e/ou a variável de ambiente `GOOGLE_MAPS_API_KEY`.

## Como executar

```bash
# compila e executa
mvn spring-boot:run

# ou empacota e executa
mvn clean package
java -jar target/maps-poc-0.0.1-SNAPSHOT.jar
```

Aplicação iniciará em `http://localhost:8080`.

- Swagger UI: http://localhost:8080/swagger-ui.html
- OpenAPI JSON: http://localhost:8080/v3/api-docs
- Actuator: http://localhost:8080/actuator (health, info)

## Endpoints disponíveis

Base path: `/api/maps`

- `GET /api/maps/geocode?address=...&language=pt-BR` — Geocodifica um endereço (chama Google Geocoding API)
- `GET /api/maps/reverse?lat=-23.5&lng=-46.6&language=pt-BR` — Reverse geocoding por coordenadas (chama Google Geocoding API)
- `GET /api/maps/places/autocomplete?input=Avenida%20Paulista&language=pt-BR` — Autocomplete de lugares (chama Google Places Autocomplete)

As respostas retornam o JSON bruto recebido do Google (mesma estrutura do Google), para simplicidade neste PoC.

### Exemplos cURL

```bash
curl "http://localhost:8080/api/maps/geocode?address=Avenida%20Paulista%2C%20S%C3%A3o%20Paulo&language=pt-BR"

curl "http://localhost:8080/api/maps/reverse?lat=-23.561414&lng=-46.656532&language=pt-BR"

curl "http://localhost:8080/api/maps/places/autocomplete?input=Avenida%20Paulista&language=pt-BR"
```

## Estrutura principal

```
src/main/java/com/example/mapspoc
├── MapsPocApplication.java
├── config
│   ├── GoogleMapsProperties.java  # lê google.maps.api-key
│   └── HttpClientConfig.java      # RestTemplate
├── controller
│   └── GoogleMapsController.java  # camada de exposição (Swagger anotado)
├── service
│   └── GoogleMapsService.java     # integra com Google Maps via REST
└── exception
    └── GlobalExceptionHandler.java

src/main/resources
├── application.yml
└── openapi/google
    ├── geocoding.yaml             # Spec não-oficial do endpoint de geocoding
    └── places-autocomplete.yaml   # Spec não-oficial do endpoint de places autocomplete
```

## OpenAPI (YAML) do Google

O diretório `src/main/resources/openapi/google` contém especificações OpenAPI (não oficiais) mínimas dos endpoints do Google utilizados por esta POC. Você pode usá-las para gerar um cliente HTTP tipado caso queira.

Já deixamos um plugin do `openapi-generator-maven-plugin` configurado no `pom.xml` (apontando para `geocoding.yaml`). Ele está marcado para rodar na fase `generate-sources`. Se preferir desabilitar, use o profile `no-openapi-gen`.

### Gerar cliente a partir do YAML (opcional)

```bash
# gera códigos do cliente para o geocoding (saída em target/generated-sources/openapi-geocoding)
mvn generate-sources
```

Depois você poderia apontar seu serviço para esse cliente gerado ao invés do `RestTemplate` manual (este PoC mantém simples com RestTemplate).

## Testes

Execute a suíte de testes:

```bash
mvn test
```

### Cobertura de Código (JaCoCo)

O plugin JaCoCo já está configurado no `pom.xml`. Para gerar o relatório de cobertura:

```bash
mvn clean verify
```

Depois abra o relatório em:

- `target/site/jacoco/index.html`

## Observações
- Não há segurança configurada neste momento (conforme solicitado).
- Certifique-se de habilitar os serviços necessários (Geocoding API, Places API) na sua conta do Google Cloud e de restringir apropriadamente sua API Key.
- Erros comuns retornarão com um payload JSON padrão via nosso `GlobalExceptionHandler`.
- Caso use uma IDE, ative o processamento de anotações (annotation processing) para o Lombok.

## Versionamento

O projeto foi preparado para o repositório GitHub sugerido: `https://github.com/ltsiciliano/maps-poc.git`. Você pode realizar o push a partir do seu ambiente.
