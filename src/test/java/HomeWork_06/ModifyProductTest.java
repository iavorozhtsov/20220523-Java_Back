package HomeWork_06;

import HomeWork_05.RetrofitUtils;
import HomeWork_05.api.ProductService;
import HomeWork_05.dto.Product;
import com.github.javafaker.Faker;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import retrofit2.Response;

import java.io.IOException;
import java.io.InputStream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class ModifyProductTest {

    static ProductService productService;
    private Product product = null;
    private Faker faker = new Faker();

    static SqlSession session = null;
    static db.dao.ProductsMapper productMapper = null;
    static db.model.ProductsExample example = null;

    @BeforeAll
    static void beforeAll() throws IOException {
        productService = RetrofitUtils.getRetrofit().create(ProductService.class);

        String resource = "mybatis-config.xml";
        InputStream inputStream = Resources.getResourceAsStream(resource);
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);

        session = sqlSessionFactory.openSession();
        productMapper = session.getMapper(db.dao.ProductsMapper.class);
        example = new db.model.ProductsExample();
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
        //Сохраняем начальное значение названия в строке 2
        String s = productMapper.selectByPrimaryKey(2l).getTitle();

        Response<Product> response = productService.modifyProduct(product).execute();
        assertThat(response.isSuccessful(), equalTo(true));

        assert !(product.getTitle().matches(s));
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

