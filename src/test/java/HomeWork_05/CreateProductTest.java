package HomeWork_05;

import HomeWork_05.api.ProductService;
import HomeWork_05.dto.Product;
import com.github.javafaker.Faker;
import okhttp3.ResponseBody;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.*;
import retrofit2.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.equalTo;


public class CreateProductTest {

    static ProductService productService;
    Product productF = null;
    Product productE = null;
    Faker faker = new Faker();
    static List<Integer> id = new ArrayList<>();

    @BeforeAll
    static void beforeAll() throws IOException {
        productService = RetrofitUtils.getRetrofit().create(ProductService.class);
    }

    @BeforeEach
    void preconditions(){
        productF = new Product();
        productF.setTitle(faker.food().ingredient());
        productF.setCategoryTitle("Food");
        productF.setPrice((int) (Math.random() * 10000));

        productE = new Product();
        productE.setTitle(faker.commerce().productName());
        productE.setCategoryTitle("Electronic");
        productE.setPrice((int) (Math.random() * 10000));

    }
    
    @AfterAll
    static void afterAll() throws IOException {
        if (id.size()>0) {
            for (int i : id) {
                Response<ResponseBody> response = productService.deleteProduct(i).execute();
                assertThat(response.isSuccessful(), is(true));
            }
        }
    }

    @Test
    @DisplayName("Create a product in food category - Positive test")
    void createProductInFoodCategoryTest() throws IOException {
        Response<Product> response = productService.createProduct(productF).execute();
        assert response.body() != null;
        id.add(response.body().getId());
        assertThat(response.isSuccessful(), CoreMatchers.is(true));
        assertThat(response.code(), equalTo(201));
        assertThat(response.body().getTitle(), equalTo(productF.getTitle()));
        assertThat(response.body().getCategoryTitle(), equalTo("Food"));
    }

    @Test
    @DisplayName("Create a product in electronic category - Positive test")
    void createProductInElectronicCategoryTest() throws IOException {
        Response<Product> response = productService.createProduct(productE).execute();
        assert response.body() != null;
        id.add(response.body().getId());
        assertThat(response.isSuccessful(), CoreMatchers.is(true));
        assertThat(response.code(), equalTo(201));
        assertThat(response.body().getTitle(), equalTo(productE.getTitle()));
        assertThat(response.body().getCategoryTitle(), equalTo("Electronic"));
    }

    @Test
    @DisplayName("Create product without Category and Title - Negative test")
    void createProductWithoutCategoryAndTitleTest() throws IOException {
        Product productB = new Product();
        productB.setPrice(0);

        Response<Product> response = productService.createProduct(productB).execute();
        assertThat(response.isSuccessful(), CoreMatchers.is(false));
        assertThat(response.code(), equalTo(500));
        assertThat(response.errorBody().string().contains("Internal Server Error"), is(true));
    }

    @Test
    @DisplayName("Create product with ID - Negative test")
    void createProductWithIdTest() throws IOException {
        productF.setId(1);

        Response<Product> response = productService.createProduct(productF).execute();
        assertThat(response.isSuccessful(), CoreMatchers.is(false));
        assertThat(response.code(), equalTo(400));
        assertThat(response.errorBody().string().contains("Id must be null for new entity"), is(true));
    }
}
