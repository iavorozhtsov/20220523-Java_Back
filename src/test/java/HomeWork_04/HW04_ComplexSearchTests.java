package HomeWork_04;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.path.json.JsonPath;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;


public class HW04_ComplexSearchTests extends HW04_AbstractTest {

    private String path = getBaseUrl() + "recipes/complexSearch";

    RequestSpecification reqSpec = new RequestSpecBuilder()
            .addQueryParam("apiKey", getApiKey())
            .addQueryParam("query", "pasta")
            .build();

    ResponseSpecification respSpec = new ResponseSpecBuilder()
            .expectStatusCode(200)
            .expectResponseTime(Matchers.lessThan(1000l))
            .build();

    @Test
    @DisplayName("01 Pasta should exist in any cuisine")
    void isPastaExist(){
        HW04_CS_Response resp = given().spec(reqSpec)
                .queryParam("number", 5)
                .when()
                .get(path)
                .then()
                .spec(respSpec)
                .extract()
                .response()
                .body()
                .as(HW04_CS_Response.class);
        System.out.println("totalResults = " + resp.getTotalResults());
        assertThat(resp.getTotalResults(), greaterThan(200));
    }

    @Test
    @DisplayName("02 Number check")
    void numberCheck(){
        int number = 5;
        HW04_CS_Response resp = given().spec(reqSpec)
                .queryParam("number", number)
                .when()
                .get(path)
                .then()
                .spec(respSpec)
                .extract()
                .response()
                .body()
                .as(HW04_CS_Response.class);
        System.out.println("number = " + resp.getNumber());
        assertThat(resp.getNumber(), equalTo(number));

        System.out.println("Array of results:\n" + Arrays.toString(resp.getResults()));
        //assertThat(resp.get("results"), arrayWithSize(equalTo(number)));
    }

    @Test
    @DisplayName("03 Most fastest pasta is \"Simple Parmesan Chili Pasta\"")
    void getMostFastestPasta(){
        HW04_CS_Response resp = given().spec(reqSpec)
                .queryParam("maxReadyTime", 15)
                .when()
                .get(path)
                .then()
                .spec(respSpec)
                .extract()
                .response()
                .body()
                .as(HW04_CS_Response.class);

        System.out.println("totalResults = " + resp.getTotalResults());
        assertThat(resp.getTotalResults(), equalTo(1));

        System.out.println(Arrays.toString(resp.getResults()));
        assertThat(Arrays.toString(resp.getResults()), containsString("Simple Parmesan Chili Pasta"));
    }

    @Test
    @DisplayName("04 Friday's pasta has ID 1697827")
    /*В этом тесте не получилось реализовать проверку через класс,
    так как в ответе появлялся параметр nutrition, который мне был не нужен,
    но @JsonIgnore почему-то не отработал с сообщением Unknown parameter 'nutrition' not marked as ignored
     */
    void isThereAlcoholPasta(){
        int minAlcohol = 38;
        int expectedID = 1697827;

        JsonPath resp = given().spec(reqSpec)
                .queryParam("minAlcohol", minAlcohol)
                .when()
                .get(path)
                .then()
                .spec(respSpec)
                .extract()
                .jsonPath();

        System.out.println("totalResults = " + resp.get("totalResults"));
        assertThat(resp.get("totalResults"), equalTo(1));

        System.out.println("ID = " + resp.get("results[0].id"));
        assertThat(resp.get("results[0].id"), equalTo(expectedID));
    }

    @Test
    @DisplayName("05 Content-Type and SLA check")
    void contentAndSLAcheck(){
        given().spec(reqSpec)
                .when()
                .get(path)
                .then()
                .spec(respSpec)
                .contentType("application/json")
                .time(lessThan(700l))
                .header("Connection", Matchers.equalTo("keep-alive"));
    }
}
