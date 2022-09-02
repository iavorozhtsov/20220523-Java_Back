package HomeWork_06;

import HomeWork_05.RetrofitUtils;
import HomeWork_05.api.ProductService;
import okhttp3.ResponseBody;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import retrofit2.Response;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class GetProducts {

    static ProductService productService;

    @BeforeAll
    static void beforeAll() throws IOException {
        productService = RetrofitUtils.getRetrofit().create(ProductService.class);
    }

    @Test
    @DisplayName("Get list of all products - Positive Test")
    void getProducts() throws IOException {
        Response<ResponseBody> response = productService.getProducts().execute();

        assertThat(response.isSuccessful(), equalTo(true));
    }
}
