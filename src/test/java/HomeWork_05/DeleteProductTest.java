package HomeWork_05;

import HomeWork_05.api.ProductService;
import HomeWork_05.dto.Product;
import com.github.javafaker.Faker;
import okhttp3.ResponseBody;
import org.junit.jupiter.api.*;
import retrofit2.Response;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class DeleteProductTest {
    static ProductService productService;
    static Product product = null;
    static Faker faker = new Faker();

    private static int i = 1;

    @BeforeAll
    static void beforeAll() throws IOException {
        productService = RetrofitUtils.getRetrofit().create(ProductService.class);
        product = new Product();
        product.setTitle(faker.food().ingredient());
        product.setCategoryTitle("Food");
        product.setPrice((int) (Math.random() * 10000));

        Response<Product> response = productService.createProduct(product).execute();
        i = response.body().getId();
    }


    @Test
    @DisplayName("Delete product with ID = i - Positive Test")
    void deleteProductById() throws IOException {
        Response<ResponseBody> response = productService.deleteProduct(i).execute();

        assertThat(response.isSuccessful(), equalTo(true));
    }

    @Test
    @DisplayName("Delete non existing product with ID = i+1 - Negative Test")
    void deleteNonExistingProduct() throws IOException {
        Response<ResponseBody> response = productService.deleteProduct(i+1).execute();

        assertThat(response.isSuccessful(), equalTo(false));
        assertThat(response.code(), equalTo(500));

    }
}
