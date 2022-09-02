package HomeWork_06;

import HomeWork_05.RetrofitUtils;
import HomeWork_05.api.ProductService;
import HomeWork_05.dto.Product;
import com.github.javafaker.Faker;
import okhttp3.ResponseBody;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import retrofit2.Response;

import java.io.IOException;
import java.io.InputStream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class DeleteProductTest {
    static ProductService productService;
    static Product product = null;
    static Faker faker = new Faker();

    static SqlSession session = null;
    static db.dao.ProductsMapper productMapper = null;
    static db.model.ProductsExample example = null;

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

        String resource = "mybatis-config.xml";
        InputStream inputStream = Resources.getResourceAsStream(resource);
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);

        session = sqlSessionFactory.openSession();
        productMapper = session.getMapper(db.dao.ProductsMapper.class);
        example = new db.model.ProductsExample();
    }

    @AfterAll
    static void afterAll(){
        session.close();
    }


    @Test
    @DisplayName("Delete product with ID = i and sure that count of products was decreased- Positive Test")
    void deleteProductById() throws IOException {
        long j;

        example.createCriteria().andTitleLike("%");
        j = productMapper.selectByExample(example).size();
        example.clear();

        Response<ResponseBody> response = productService.deleteProduct(i).execute();
        assertThat(response.isSuccessful(), equalTo(true));

        example.createCriteria().andTitleLike("%");
        assert (j > productMapper.countByExample(example));
    }

    @Test
    @DisplayName("Delete non existing product with ID = i+1 - Negative Test")
    void deleteNonExistingProduct() throws IOException {
        Response<ResponseBody> response = productService.deleteProduct(i+1).execute();

        assertThat(response.isSuccessful(), equalTo(false));
        assertThat(response.code(), equalTo(500));

    }
}
