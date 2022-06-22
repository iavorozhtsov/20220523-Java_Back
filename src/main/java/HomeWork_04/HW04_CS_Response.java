package HomeWork_04;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "results",
        "offset",
        "number",
        "totalResults"
})
@Data
public class HW04_CS_Response {

    @JsonProperty("results")
    private Results[] results = null;
    @JsonProperty("offset")
    private Integer offset;
    @JsonProperty("number")
    private Integer number;
    @JsonProperty("totalResults")
    private Integer totalResults;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonPropertyOrder({
            "id",
            "title",
            "image",
            "imageType",
            "nutrition"
    })
    @Data
    static class Results {
        @JsonProperty("id")
        Integer id;
        @JsonProperty("title")
        String title;
        @JsonProperty("image")
        String image;
        @JsonProperty("imageType")
        String imageType;

        //Вот это почему-то не сработало
        @JsonIgnore
        @JsonProperty("nutrition")
        Map<String, Object> additionalProperties = new HashMap<String, Object>();

        @Override
        public String toString() {
            return "\n{" +
                    "\nid=\t\t" + id +
                    "\ntitle=\t" + title +
                    "\nimage=\t" + image +
                    "\nimageType=\t" + imageType +
                    "\n}";
        }
    }

}
