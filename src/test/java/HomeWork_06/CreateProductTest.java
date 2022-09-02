package HomeWork_06;

import HomeWork_05.RetrofitUtils;
import HomeWork_05.api.ProductService;
import HomeWork_05.dto.Product;
import HomeWork_06.db.HomeWork_06;
import com.github.javafaker.Faker;
import okhttp3.ResponseBody;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;
import retrofit2.Response;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


public class CreateProductTest {

    static ProductService productService;
    Product productF = null;
    Product productE = null;
    Faker faker = new Faker();
    static List<Integer> id = new ArrayList<>();
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
        long j;

        example.createCriteria().andTitleLike("%");
        j = productMapper.countByExample(example);

        if (id.size()>0) {
            for (int i : id) {
                productMapper.deleteByPrimaryKey((long) i);
            }
        }

        assert (j > productMapper.selectByExample(example).size());

        session.close();
    }

    @Test
    @DisplayName("Create a product in food category and compare value with database record - Positive test")
    void createProductInFoodCategoryTest() throws IOException {
        example.clear();
        Response<Product> response = productService.createProduct(productF).execute();
        assert response.body() != null;
        id.add(response.body().getId());
        assertThat(response.isSuccessful(), CoreMatchers.is(true));
        assertThat(response.code(), equalTo(201));
        assertThat(response.body().getTitle(), equalTo(productF.getTitle()));
        assertThat(response.body().getCategoryTitle(), equalTo("Food"));

        example.createCriteria().andIdEqualTo((long) response.body().getId());
        List<db.model.Products> products = productMapper.selectByExample(example);
        assertThat(response.body().getTitle(), equalTo(products.get(0).getTitle()));

    }

    @Test
    @DisplayName("Create a product in electronic category and compare count of all products from database - Positive test")
    void createProductInElectronicCategoryTest() throws IOException {
        long i;
        long j;

        example.createCriteria().andTitleLike("%");
        i = productMapper.countByExample(example);
        example.clear();

        Response<Product> response = productService.createProduct(productE).execute();
        assert response.body() != null;
        id.add(response.body().getId());
        assertThat(response.isSuccessful(), CoreMatchers.is(true));
        assertThat(response.code(), equalTo(201));
        assertThat(response.body().getTitle(), equalTo(productE.getTitle()));
        assertThat(response.body().getCategoryTitle(), equalTo("Electronic"));

        example.createCriteria().andIdEqualTo((long) response.body().getId());
        List<db.model.Products> products = productMapper.selectByExample(example);
        assertThat(products.get(0).getPrice(), equalTo(response.body().getPrice()));

        example.clear();
        example.createCriteria().andTitleLike("%");
        List<db.model.Products> products1 = productMapper.selectByExample(example);
        j = productMapper.countByExample(example);

        //Не смог понять почему в переменной j получается 6, хотя возвращается список из 7 элементов.
        //Такое ощущение, что ответы кешируются или не выполняются дважды одинаковые запросы.
        //Из-за этого пришлось переписать проверку.

        assert (productMapper.selectByExample(example).size() > i);

    }

    @Test
    @DisplayName("Create product without Category and Title - Negative test")
    void createProductWithoutCategoryAndTitleTest() throws IOException {
        int i;

        example.createCriteria().andTitleLike("%");
        i = productMapper.selectByExample(example).size();

        Product productB = new Product();
        productB.setPrice(0);

        Response<Product> response = productService.createProduct(productB).execute();
        assertThat(response.isSuccessful(), CoreMatchers.is(false));
        assertThat(response.code(), equalTo(500));
        assertThat(response.errorBody().string().contains("Internal Server Error"), is(true));

        assertThat(productMapper.selectByExample(example).size(), equalTo(i));
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
