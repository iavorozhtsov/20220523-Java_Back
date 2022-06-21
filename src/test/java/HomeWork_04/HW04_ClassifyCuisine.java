package HomeWork_04;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.path.json.JsonPath;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class HW04_ClassifyCuisine extends HW04_AbstractTest {

    private String path = getBaseUrl() + "recipes/cuisine";

    RequestSpecification reqSpec = new RequestSpecBuilder()
            .addQueryParam("apiKey", getApiKey())
            .setContentType("application/x-www-form-urlencoded")
            .build();

    ResponseSpecification respSpec = new ResponseSpecBuilder()
            .expectStatusCode(200)
            .expectResponseTime(Matchers.lessThan(1000l))
            .build();

    @Test
    @DisplayName("01 Pho Bo is in Vietnamese cuisine")
    void whereIsPhoBo(){
        JsonPath resp = given().spec(reqSpec)
                .formParam("title","Pho Noodle")
                .when()
                .post(path)
                .then()
                .spec(respSpec)
                .extract()
                .jsonPath();

        System.out.println("Cuisine is: " + resp.get("cuisine").toString());
        assertThat(resp.get("cuisine"), equalTo("Vietnamese"));
    }

    @Test
    @DisplayName("02 Caesar salad is American cuisine")
    void whereIsCaesar(){
        JsonPath resp = given().spec(reqSpec)
                .formParam("title","Caesar salad")
                .when()
                .post(path)
                .then()
                .spec(respSpec)
                .extract()
                .jsonPath();

        System.out.println("Cuisine is: " + resp.get("cuisine").toString());
        assertThat(resp.get("cuisine"), equalTo("American"));
    }

    @Test
    @DisplayName("03 Unauthorized request attemption")
    void unAuthorizedCheck() {
        JsonPath resp = given()
                .contentType("application/x-www-form-urlencoded")
                .formParam("title", "Noodle")
                .when()
                .post(path)
                .then()
                .statusCode(401)
                .extract()
                .jsonPath();

        System.out.println("Status is: " + resp.get("status").toString());
        assertThat(resp.get("status").toString(), equalTo("failure"));

        System.out.println("Code is: " + resp.get("code").toString());
        assertThat(resp.get("code").toString(), equalTo("401"));

        System.out.println("Message is: " + resp.get("message").toString());
        assertThat(resp.get("message").toString(), equalTo("You are not authorized. Please read https://spoonacular.com/food-api/docs#Authentication"));
    }

    @Test
    @DisplayName("04 Pizza has 0.95 confidence")
    void pizzasConfidence() {
        JsonPath resp = given().spec(reqSpec)
                .formParam("title", "pizza")
                .when()
                .post(path)
                .then()
                .spec(respSpec)
                .extract()
                .jsonPath();

        System.out.println("Cuisine is: " + resp.get("cuisine").toString());
        assertThat(resp.get("cuisine"), equalTo("Mediterranean"));

        System.out.println("Confidence is: " + resp.get("confidence").toString());
        assertThat(resp.get("confidence"), equalTo(0.95f));
    }

    @Test
    @DisplayName("05 Default answer check")
    void defaultAnswerCheck() {
        given().spec(reqSpec)
                .when()
                .post(path)
                .then()
                .assertThat()
                .spec(respSpec)
                .body(equalTo("{\"cuisine\":\"Mediterranean\",\"cuisines\":[\"Mediterranean\",\"European\",\"Italian\"],\"confidence\":0.0}"));
    }
}
