package HomeWork_04;

//TODO Дописать класс со стандартным ответом, скопипастить Response класс.
//TODO Найти как передать в форму класс с рецептом.
//TODO Протестировать всю цепочку и сдать домашнее задание.

import io.restassured.path.json.JsonPath;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static io.restassured.RestAssured.given;
import static java.lang.System.currentTimeMillis;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.lessThan;

public class HW04_ChainTest extends HW04_AbstractTest {

    @Test
    @DisplayName("01 Chain test")
    void chainTest(){
        String userName = "20220619-api-via2";
        String userHash = "d8c9d49f92f76d270c7cc28713e647ce579712a7";
        String recipePath = getBaseUrl() + "recipes/complexSearch";
        String userPath = getBaseUrl() + "users/connect";
        String mealPlanPath = getBaseUrl() + "mealplanner/{username}/items";
        String shoplistPath = getBaseUrl() + "mealplanner/{username}/shopping-list";
        String generateShopListPath = getBaseUrl() + "mealplanner/{username}/shopping-list/{start-date}/{end-date}";
        String addToShopListPath = getBaseUrl() + "mealplanner/{username}/shopping-list/items";
        String delFromShopListPath = getBaseUrl() + "mealplanner/{username}/shopping-list/items/{id}";

        long myRecipeID;
        String myRecipeTitle;
        Double costBeforeDeleting;
        Double costAfterDeleting;
        long itemID;
        String itemName;


        //Get Recipe ID for MealPlan
        JsonPath resp = given()
                .queryParam("apiKey", getApiKey())
                .queryParam("query", "pasta margherita")
                .queryParam("number", 5)
                .when()
                .get(recipePath)
                .then()
                .statusCode(200)
                .extract()
                .jsonPath();
        assertThat(resp.get("totalResults"), greaterThan(0));

        myRecipeID = resp.getLong("results[0].id");
        myRecipeTitle = resp.get("results[0].title");
        assertThat(resp.get("results[0].title"), equalTo("Pasta Margherita"));

        //User creation
        /* Закомментировано на период написания теста, чтобы не плодить сущности пользователей. Тест работает и без этого функционала

        resp = given()
                .queryParam("apiKey", getApiKey())
                .body("{" +
                        "    \"username\": \"" + userName + "\"," +
                        "    \"firstName\": \"Igor\"," +
                        "    \"lastName\": \"Vorozhtsov\"," +
                        "    \"email\": \"iavorozhtsov@gmail.com\"" +
                        "}")
                .when()
                .post(userPath)
                .then()
                .statusCode(200)
                .extract()
                .jsonPath();

        userName = resp.get("username");
        System.out.println("User name is: " + resp.get("username").toString());
        //assertThat(resp.get("username"), equalTo(userName));

        userHash = resp.get("hash").toString();
        System.out.println("User's hash is: " + userHash);
         */

        //Get current shopping list
        /* Проверка что у пользователя ещё нет ничего в списке покупок
        resp = given()
                .pathParam("username", userName)
                .queryParam("apiKey", getApiKey())
                .queryParam("hash", userHash)
                .when()
                .get(shoplistPath)
                .then()
                .statusCode(200)
                .extract()
                .jsonPath();

        assertThat(resp.getDouble("cost"), equalTo(0.0));
         */

        //Add an item to Shopping List
        resp = given()
                .pathParam("username", userName)
                .queryParam("apiKey", getApiKey())
                .queryParam("hash", userHash)
                .body("{" +
                        "\"item\": \"1 package baking powder\","+
                        "\"item\": \"1 lemon\","+
                        "\"item\": \"5 eggs\","+
                        "\"parse\": \"true\"" +
                "}")
                .when()
                .post(addToShopListPath)
                .then()
                //.statusCode(200)
                .extract()
                .jsonPath();

        //Add Recipe to the Meal Plan
        Long dateTime = currentTimeMillis() / 1000;
        resp = given()
                .pathParam("username", userName)
                .queryParam("hash", userHash)
                .queryParam("apiKey", getApiKey())
                .body("{" +
                        "    \"date\": " + dateTime + "," +
                        "    \"slot\": 2," +
                        "    \"position\": 0," +
                        "    \"type\": \"RECIPE\"," +
                        "    \"value\": {" +
                        "        \"id\": " + myRecipeID + "," +
                        "        \"servings\": 2," +
                        "        \"title\": \"" + myRecipeTitle + "\"," +
                        "        \"imageType\": \"jpg\"," +
                        "    }" +
                        "}")
                .when()
                .post(mealPlanPath)
                .then()
                .statusCode(200)
                .extract()
                .jsonPath();

        //Generate Shopping list from Meal Plan
        String todayDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        resp = given()
                .pathParam("username", userName)
                .pathParam("start-date", todayDate)
                .pathParam("end-date", todayDate)
                .queryParam("hash", userHash)
                .queryParam("apiKey", getApiKey())
                .when()
                .post(generateShopListPath)
                .then()
                .statusCode(200)
                .extract()
                .jsonPath();

        //Get current shopping list
        resp = given()
                .pathParam("username", userName)
                .queryParam("apiKey", getApiKey())
                .queryParam("hash", userHash)
                .when()
                .get(shoplistPath)
                .then()
                .statusCode(200)
                .extract()
                .jsonPath();

        costBeforeDeleting = resp.getDouble("cost");
        System.out.println("Shopping list cost before: " + costBeforeDeleting);
        itemID = resp.getLong("aisles[0].items[0].id");
        System.out.println("Item ID that will be deleted: " + itemID);
        itemName = resp.get("aisles[0].items[0].name");
        System.out.println("Item Name: " + itemName);
        System.out.println("Item Cost: " + resp.get("aisles[0].items[0].name"));

        //Delete first Item from shopping list
        resp = given()
                .pathParam("username", userName)
                .pathParam("id", itemID)
                .queryParam("hash", userHash)
                .queryParam("apiKey", getApiKey())
                .when()
                .delete(delFromShopListPath)
                .then()
                .statusCode(200)
                .extract()
                .jsonPath();
        assertThat(resp.get("status"), equalTo("success"));

        //Get current shopping list to compare costs value
        resp = given()
                .pathParam("username", userName)
                .queryParam("apiKey", getApiKey())
                .queryParam("hash", userHash)
                .when()
                .get(shoplistPath)
                .then()
                .statusCode(200)
                .extract()
                .jsonPath();
        assertThat(resp.getDouble("cost"), lessThan(costBeforeDeleting));
        System.out.println("Shopping list cost after: " + resp.getDouble("cost"));
    }
}
