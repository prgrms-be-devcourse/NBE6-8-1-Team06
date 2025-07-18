package com.coffeeproject.domain.product.product.api;

import com.coffeeproject.domain.product.product.entity.Product;
import com.coffeeproject.domain.product.product.service.ProductService;
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
        productService.write("커피", "쓴 커피","TmpImgUrl1",10000);
        productService.write("차", "달콤한 차","TmpImgUrl2", 8000);
        productService.write("케이크", "달콤한 케이크","TmpImgUrl3", 12000);
        productService.write("쿠키", "바삭한 쿠키","TmpImgUrl4", 5000);

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
                                  "imgUrl": "TestImgUrl",
                                  "price": 5000
                                }
                                """)
        ).andDo(print());

        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println(responseBody);

        resultActions
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andDo(print())
                .andExpect(jsonPath("$.data.name").value("커피"))
                .andExpect(jsonPath("$.data.description").value("맛있는 커피입니다."))
                .andExpect(jsonPath("$.data.price").value(5000))
                .andExpect(jsonPath("$.data.imgUrl").value("TestImgUrl"));

    }

    @Test
    @DisplayName("상품 등록- 상품 설명 필수")
    void t1_1() throws Exception {

        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "커피",
                                  "imgUrl": "TestImgUrl",
                                  "price": 5000
                                }
                                """)
        ).andDo(print());

        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println(responseBody);

        resultActions
                .andExpect(jsonPath("$.resultCode").value("400-1"))
                .andExpect(jsonPath("$.msg").value("description-NotBlank-must not be blank"));

    }
    @Test
    @DisplayName("상품 등록- 상품 이미지 주소 필수")
    void t1_2() throws Exception {

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

        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println(responseBody);

        resultActions
                .andExpect(jsonPath("$.resultCode").value("400-1"))
                .andExpect(jsonPath("$.msg").value("imgUrl-NotBlank-must not be blank"));

    }
    @Test
    @DisplayName("상품 등록- 상품 이름 필수")
    void t1_3() throws Exception {

        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "description": "맛있는 커피입니다.",
                                  "imgUrl": "TestImgUrl",
                                  "price": 5000
                                }
                                """)
        ).andDo(print());

        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println(responseBody);

        resultActions
                .andExpect(jsonPath("$.resultCode").value("400-1"))
                .andExpect(jsonPath("$.msg").value("name-NotBlank-must not be blank"));

    }

    @Test
    @DisplayName("상품 다건 조회")
    void t2() throws Exception {


        List<Product> products = productService.findAll();
        System.out.println("==============================================================");
        System.out.println(products.size());
        for (Product product : products) {
            System.out.println("상품 ID: " + product.getId());
            System.out.println("상품 이름: " + product.getName());
            System.out.println("상품 설명: " + product.getDescription());
            System.out.println("상품 가격: " + product.getPrice());
            System.out.println("상품 이미지 주소: " + product.getImgUrl());
            System.out.println("==============================================================");
        }
        System.out.println("==============================================================");


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
                .andExpect(jsonPath("$.data.length()").value(products.size()));


        for (int i = 0; i < products.size(); i++) {
            Product product = products.get(i);
            resultActions
                    .andExpect(jsonPath("$.data.[%d].id".formatted(i)).value(product.getId()))
                    .andExpect(jsonPath("$.data.[%d].name".formatted(i)).value(product.getName()))
                    .andExpect(jsonPath("$.data.[%d].description".formatted(i)).value(product.getDescription()))
                    .andExpect(jsonPath("$.data.[%d].price".formatted(i)).value(product.getPrice()))
                    .andExpect(jsonPath("$.data.[%d].imgUrl".formatted(i)).value(product.getImgUrl()));
        }


    }

    @Test
    @DisplayName("상품 다건 조회 - 상품이 없는 경우")
void t2_1() throws Exception
    {
        // 모든 상품을 삭제하여 빈 리스트를 만듭니다.
        productService.deleteAll();

        ResultActions resultActions = mockMvc
                .perform(
                        get("/api/v1/products")
                )
                .andDo(print());

        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println(responseBody);

        resultActions
                .andExpect(handler().handlerType(ApiV1ProductController.class))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.msg").value("상품이 없습니다."));
    }

    @Test
    @DisplayName("상품 단건 조회")
    void t3() throws Exception {


        Product product = productService.findLatest().get();
        ResultActions resultActions = mockMvc
                .perform(
                        get("/api/v1/products/" + product.getId())
                )
                .andDo(print());
        resultActions
                .andExpect(handler().handlerType(ApiV1ProductController.class))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(product.getId()))
                .andExpect(jsonPath("$.data.name").value(product.getName()))
                .andExpect(jsonPath("$.data.description").value(product.getDescription()))
                .andExpect(jsonPath("$.data.price").value(product.getPrice()))
                .andExpect(jsonPath("$.data.imgUrl").value(product.getImgUrl()));

    }
    @Test
    @DisplayName("상품 단건 조회- 상품이 없는 경우")
    void t3_1() throws Exception {

        // 존재하지 않는 상품 ID로 조회
        int nonExistentProductId = 9999;

        ResultActions resultActions = mockMvc
                .perform(
                        get("/api/v1/products/" + nonExistentProductId)
                )
                .andDo(print());

        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println(responseBody);

        resultActions
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.msg").value("상품을 찾을수 없습니다."));

    }

    @Test
    @DisplayName("상품 삭제")
    void t4() {

        Product product = productService.findLatest().get();

        try {
            mockMvc.perform(
                            MockMvcRequestBuilders.delete("/api/v1/products/" + product.getId())
                    ).andDo(print())
                    .andExpect(status().isNoContent());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Test
    @DisplayName("상품삭제 - 상품이 없는 경우")
    void t4_1() throws Exception {

        // 존재하지 않는 상품 ID로 삭제 요청
        int nonExistentProductId = 9999;

        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.delete("/api/v1/products/" + nonExistentProductId)
        ).andDo(print());

        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println(responseBody);

        resultActions
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.msg").value("상품을 찾을수 없습니다."));

    }

    @Test
    @DisplayName("상품 수정")
    void t5() throws Exception{
        Product product = productService.findLatest().get();

        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.put("/api/v1/products/" + product.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "수정된 커피",
                                  "description": "수정된 맛있는 커피입니다.",
                                  "imgUrl": "imaegeUrl",
                                  "price": 6000
                                }
                                """)
        ).andDo(print());


        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println(responseBody);

        resultActions
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.data.name").value("수정된 커피"))
                .andExpect(jsonPath("$.data.description").value("수정된 맛있는 커피입니다."))
                .andExpect(jsonPath("$.data.price").value(6000))
                .andExpect(jsonPath("$.data.imgUrl").value(product.getImgUrl()));
    }

}