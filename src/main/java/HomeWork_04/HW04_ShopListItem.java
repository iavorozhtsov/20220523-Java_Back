package HomeWork_04;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;


@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder( {
        "item",
        "parse"
})

@Data
public class HW04_ShopListItem {

    @JsonProperty("item")
    private String item;
    @JsonProperty("parse")
    private String parse;

}
