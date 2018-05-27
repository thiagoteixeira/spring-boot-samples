package com.thiagojavabr.repositoryrestresource.resource;



import com.thiagojavabr.repositoryrestresource.domain.Product;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.ApplicationContext;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ProductResourceTest {
    @Value("${local.server.port}")
    private int localServerPort;
    private String baseUri;
    private TestRestTemplate restTemplate = new TestRestTemplate();


    // Assert attributes
    private static final int PRODUCT_LIST_SIZE = 3;
    private static final Long PRODUCT_ID_TO_FIND_ONE = 3L;
    private static final Long PRODUCT_ID_TO_DELETE = 2L;
    private static final Long PRODUCT_ID_TO_UPDATE = 1L;

    @Before
    public void setup(){
        baseUri = String.format("http://localhost:%s/products",localServerPort);
    }

    @Test
    public void testGet(){
        ResponseEntity<List> response = this.restTemplate.getForEntity(baseUri, List.class);
        List products = response.getBody();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(products.size()).isEqualTo(PRODUCT_LIST_SIZE);
    }

    @Test
    public void testGetById(){
        ResponseEntity<Product> response = this.restTemplate.getForEntity(baseUri + "/{id}", Product.class, PRODUCT_ID_TO_FIND_ONE);
        Product product = response.getBody();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(product).isNotNull();
    }

    @Test
    public void testCreate(){
        Product p = new Product();
        p.setName("test");
        p.setPrice(BigDecimal.TEN);
        ResponseEntity<Product> response = this.restTemplate.postForEntity(baseUri, p,
                Product.class);
        Product product = response.getBody();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(product).isNotNull();
    }

    @Test
    public void testUpdate(){
        String newName = "TestProduct";

        String requestBody = "{\"name\":\"" + newName + "\"}";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<String>(requestBody, headers);
        ResponseEntity<Product> response = this.restTemplate.exchange(baseUri + "/{id}", HttpMethod.PUT, entity, Product.class, PRODUCT_ID_TO_UPDATE);
        Product product = response.getBody();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(product.getId()).isEqualTo(PRODUCT_ID_TO_UPDATE);
        assertThat(product.getName()).isEqualTo(newName);
    }

    @Test
    public void testDelete(){
        this.restTemplate.delete(baseUri + "/{id}", PRODUCT_ID_TO_DELETE);
        ResponseEntity<Product> response = this.restTemplate.getForEntity(baseUri + "/{id}", Product.class, PRODUCT_ID_TO_DELETE);
        Product p = response.getBody();
        assertThat(p).isNull();
    }
}
