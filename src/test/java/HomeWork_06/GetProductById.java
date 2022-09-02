package HomeWork_06;

import HomeWork_05.RetrofitUtils;
import HomeWork_05.api.ProductService;
import HomeWork_05.dto.Product;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import retrofit2.Response;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;


public class GetProductById {
    static ProductService productService;

    @BeforeAll
    static void beforeAll() throws IOException {
        productService = RetrofitUtils.getRetrofit().create(ProductService.class);
    }

    @Test
    @DisplayName("Get any product by ID positive test")
    void getProductByIdPositive() throws IOException {
        Response<Product> response = productService.getProductById(5).execute();
        assertThat(response.isSuccessful(), equalTo(true));
        assertThat(response.body().getPrice(), greaterThanOrEqualTo(0));
    }

    @Test
    @DisplayName("Get any product by ID negative test")
    void getProductByIdNegative() throws IOException {
        int i = 1100;

        Response<Product> response = productService.getProductById(i).execute();
        assertThat(response.isSuccessful(), equalTo(false));
        assertThat(response.code(), equalTo(404));
        assertThat(response.errorBody().string().contains("Unable to find product with id: " + i), equalTo(true));
    }
}
