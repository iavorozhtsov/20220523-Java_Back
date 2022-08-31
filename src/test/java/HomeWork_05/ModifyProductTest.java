package HomeWork_05;

import HomeWork_05.api.ProductService;
import HomeWork_05.dto.Product;
import com.github.javafaker.Faker;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import retrofit2.Response;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class ModifyProductTest {

    static ProductService productService;
    private Product product = null;
    private Faker faker = new Faker();

    @BeforeAll
    static void beforeAll() throws IOException {
        productService = RetrofitUtils.getRetrofit().create(ProductService.class);
    }

    @BeforeEach
    void preconditions(){
        product = new Product();
        product.setId(2);
        product.setTitle(faker.food().ingredient());
        product.setPrice((int) (Math.random() * 1000));
        product.setCategoryTitle("Food");

    }

    @Test
    @DisplayName("Modify existing product - Positive Test")
    void modifyExistingProduct() throws IOException {
        Response<Product> response = productService.modifyProduct(product).execute();

        assertThat(response.isSuccessful(), equalTo(true));
    }

    @Test
    @DisplayName("Modify not existing product - Negative Test")
    void modifyNotExistingProduct() throws IOException {
        int i = 110;

        Product product = new Product();
        product.setId(i);
        product.setTitle(faker.food().ingredient());
        product.setPrice((int) (Math.random() * 1000));
        product.setCategoryTitle("Food");

        Response<Product> response = productService.modifyProduct(product).execute();

        assertThat(response.isSuccessful(), equalTo(false));
        assertThat(response.code(), equalTo(400));
        assertThat(response.errorBody().string().contains("Product with id: " + i + " doesn't exist"), equalTo(true));
    }
}

