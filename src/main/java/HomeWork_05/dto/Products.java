package HomeWork_05.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class Products {
    @JsonProperty("products")
    private ArrayList<Product> products = new ArrayList<>();

    public Product getFirstProduct(){
        return products.get(0);
    }
}
