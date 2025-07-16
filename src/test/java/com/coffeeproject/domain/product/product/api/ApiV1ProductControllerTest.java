package com.coffeeproject.domain.product.product.api;

import com.coffeeproject.domain.product.product.entity.Product;
import com.coffeeproject.domain.product.product.service.ProductService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@SpringBootTest(classes = com.coffeeproject.CoffeeProjectApplication.class)
@AutoConfigureMockMvc
@Transactional
public class ApiV1ProductControllerTest {
    @Autowired
    private ProductService productService;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        // 테스트를 위한 초기 상품 데이터 삭제
        productService.write("커피", "쓴 커피", 10000);
        productService.write("차", "달콤한 차", 8000);
        productService.write("케이크", "달콤한 케이크", 12000);
        productService.write("쿠키", "바삭한 쿠키", 5000);
    }

    @Test
    @DisplayName("상품 등록")
    void t1() throws Exception {

        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "커피",
                                  "description": "맛있는 커피입니다.",
                                  "price": 5000
                                }
                                """)
        ).andDo(print());
        Product product = productService.findLatest().get();

        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println(responseBody);

        resultActions
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andDo(print())
                .andExpect(jsonPath("$.data.name").value("커피"))
                .andExpect(jsonPath("$.data.description").value("맛있는 커피입니다."))
                .andExpect(jsonPath("$.data.price").value(5000));

    }


    @Test
    @DisplayName("상품 다건 조회")
    void t2() throws Exception {



        List<Product> products = productService.findAll();

        ResultActions resultActions = mockMvc
                .perform(
                        get("/api/v1/products")
                )
                .andDo(print());

        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println(responseBody);

        resultActions
                .andExpect(handler().handlerType(ApiV1ProductController.class))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(products.size()));


        for (int i = 0; i < products.size(); i++) {
            Product product = products.get(i);
            resultActions
                    .andExpect(jsonPath("$[%d].id".formatted(i)).value(product.getId()))
                    .andExpect(jsonPath("$[%d].name".formatted(i)).value(product.getName()))
                    .andExpect(jsonPath("$[%d].description".formatted(i)).value(product.getDescription()))
                    .andExpect(jsonPath("$[%d].price".formatted(i)).value(product.getPrice()));
        }
        Product product = products.get(0);

    }

    @Test
    @DisplayName("상품 단건 조회")
    void t3 ()throws Exception {


        Product product = productService.findLatest().get();
        ResultActions resultActions = mockMvc
                .perform(
                        get("/api/v1/products/" + product.getId())
                )
                .andDo(print());
        resultActions
                .andExpect(handler().handlerType(ApiV1ProductController.class))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(product.getId()))
                .andExpect(jsonPath("$.name").value(product.getName()))
                .andExpect(jsonPath("$.description").value(product.getDescription()))
                .andExpect(jsonPath("$.price").value(product.getPrice()));

    }

}