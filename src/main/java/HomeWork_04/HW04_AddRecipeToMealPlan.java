package HomeWork_04;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder( {
        "date",
        "slot",
        "position",
        "type",
        "value"
})

@Data
public class HW04_AddRecipeToMealPlan {
    @JsonProperty("date")
    private Integer date;
    @JsonProperty("slot")
    private Integer slot;
    @JsonProperty("position")
    private Integer position;
    @JsonProperty("type")
    private String type = "RECIPE";
    @JsonProperty("value")
    private Value value;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonPropertyOrder( {
            "id",
            "servings",
            "title",
            "imageType"
    })

    @Data
    private static class Value {
        @JsonProperty("id")
        private Integer id;
        @JsonProperty("servings")
        private Integer servings = 2;
        @JsonProperty("title")
        private String title;
        @JsonProperty("imageType")
        private String imageType = "jpg";
    }
}
