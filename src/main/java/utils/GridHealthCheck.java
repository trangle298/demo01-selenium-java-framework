package utils;

import static io.restassured.RestAssured.given;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

public class GridHealthCheck {
    public static boolean isGridReady(String endPoint) {
        try {
            Response response = given().relaxedHTTPSValidation().when().get(endPoint);

            if(response.statusCode() != 200){
                return false;
            }

            JsonPath jsonPath = response.jsonPath();
            return jsonPath.getBoolean("value.ready");

        } catch (Exception e) {
            return false;
        }
    }
}
