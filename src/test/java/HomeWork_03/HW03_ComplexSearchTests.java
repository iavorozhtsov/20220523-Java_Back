package HomeWork_03;

import io.restassured.path.json.JsonPath;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.*;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.lessThan;


public class HW03_ComplexSearchTests extends HW03_AbstractTest {

    private String path = getBaseUrl() + "recipes/complexSearch";

    @Test
    @DisplayName("01 Pasta should exist in any cuisine")
    void isPastaExist(){
        JsonPath resp = given()
                .queryParam("apiKey", getApiKey())
                .queryParam("query", "pasta")
                .queryParam("number", 5)
                .when()
                .get(path)
                .then()
                .statusCode(200)
                .extract()
                .jsonPath();
        System.out.println("totalResults = " + resp.get("totalResults"));
        assertThat(resp.get("totalResults"), greaterThan(200));
    }

    @Test
    @DisplayName("02 Number check")
    void numberCheck(){
        int number = 5;
        JsonPath resp = given()
                .queryParam("apiKey", getApiKey())
                .queryParam("query", "pasta")
                .queryParam("number", number)
                .when()
                .get(path)
                .then()
                .statusCode(200)
                .extract()
                .jsonPath();
        System.out.println("number = " + resp.get("number"));
        assertThat(resp.get("number"), equalTo(number));

        System.out.println("Array of results:\n" + resp.get("results").toString());
        //assertThat(resp.get("results"), arrayWithSize(equalTo(number)));
    }

    @Test
    @DisplayName("03 Most fastest pasta is \"Simple Parmesan Chili Pasta\"")
    void getMostFastestPasta(){
        JsonPath resp = given()
                .queryParam("apiKey", getApiKey())
                .queryParam("query", "pasta")
                .queryParam("maxReadyTime", 15)
                .when()
                .get(path)
                .then()
                .statusCode(200)
                .extract()
                .jsonPath();

        System.out.println("totalResults = " + resp.get("totalResults"));
        assertThat(resp.get("totalResults"), equalTo(1));

        System.out.println(resp.get("results[0].title").toString());
        assertThat(resp.get("results[0].title"), equalTo("Simple Parmesan Chili Pasta"));
    }

    @Test
    @DisplayName("04 Friday's pasta has ID 1697827")
    void isThereAlcoholPasta(){
        int minAlcohol = 38;
        int expectedID = 1697827;

        JsonPath resp = given()
                .queryParam("apiKey", getApiKey())
                .queryParam("query", "pasta")
                .queryParam("minAlcohol", minAlcohol)
                .when()
                .get(path)
                .then()
                .statusCode(200)
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
        given()
                .queryParam("apiKey", getApiKey())
                .queryParam("query", "pasta")
                .when()
                .get(path)
                .then()
                .statusCode(200)
                .contentType("application/json")
                .time(lessThan(250l))
                .header("Connection", Matchers.equalTo("keep-alive"));
    }
}
