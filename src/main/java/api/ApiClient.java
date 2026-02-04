package api;

import io.restassured.RestAssured;
import io.restassured.common.mapper.TypeRef;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class ApiClient {
    private String baseUri;
    private RequestSpecification requestSpec;

    public ApiClient(String baseUri) {
        this.baseUri = baseUri;
        initializeRequestSpec();
    }

    private void initializeRequestSpec() {
        // Initialize the request specification with common settings
        this.requestSpec = RestAssured.given()
//                .config(RestAssured.config()
//                        .encoderConfig(encoderConfig()
//                                .appendDefaultContentCharsetToContentTypeIfUndefined(false)))
                .baseUri(baseUri)
                .contentType(ContentType.JSON)
                .header("Content-Type", "application/json")
//                .log()
//                .all()
                ;
    }

    public ApiClient withQueryParam(String name, Object value) {
        requestSpec.queryParam(name, value);
        return this;
    }

    public ApiClient withBody(Object body) {
        requestSpec.body(body);
        return this;
    }

    public ApiClient withAuthToken(String token) {
        requestSpec.header("Authorization", "Bearer " + token);
        return this;
    }

    public Response get(String endpoint) {
        Response response = requestSpec.get(endpoint);
        initializeRequestSpec(); // Reset for next call
        return response;
    }

    public Response post(String endpoint) {
        Response response = requestSpec.post(endpoint);
        initializeRequestSpec(); // Reset for next call
        return response;
    }

    public Response delete(String endpoint) {
        Response response = requestSpec.delete(endpoint);
        initializeRequestSpec(); // Reset for next call
        return response;
    }

    public <T> T getAndDeserialize(String endpoint, Class<T> responseType) {
        return get(endpoint).then().extract().as(responseType);
    }

    public <T> T getAndDeserialize(String endpoint, TypeRef<T> typeRef) {
        return get(endpoint).then().extract().as(typeRef);
    }

}
