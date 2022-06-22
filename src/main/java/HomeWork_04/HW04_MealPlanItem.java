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
public class HW04_MealPlanItem {

    @JsonProperty("date")
    private Long date;
    @JsonProperty("slot")
    private Integer slot;
    @JsonProperty("position")
    private Integer position;
    @JsonProperty("type")
    private String type;
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
    static class Value{
        @JsonProperty("id")
        private Integer id;
        @JsonProperty("servings")
        private Integer servings;
        @JsonProperty("title")
        private String title;
        @JsonProperty("imageType")
        private String imageType;

        public Value(long id, int servings, String title, String imageType) {
        }
    }
}
