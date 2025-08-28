package ru.rustam.catalog.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.rustam.catalog.dto.CreateCatalogDto;
import ru.rustam.catalog.dto.FilteredCatalogDto;
import ru.rustam.catalog.dto.UpdateCatalogDto;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class CatalogControllerTests {
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17-alpine")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.flyway.enabled", () -> true);
    }

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    private Integer createProduct(String name, String description, BigDecimal price) throws Exception {
        CreateCatalogDto dto = CreateCatalogDto.builder()
                .name(name)
                .description(description)
                .price(price).build();

        MvcResult mvcResult = mockMvc.perform(post("/product")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andReturn();
        return objectMapper.readTree(mvcResult.getResponse().getContentAsString()).get("id").asInt();
    }

    @Test
    void post_create_catalog() throws Exception {
        CreateCatalogDto dto = CreateCatalogDto.builder()
                .name("Coke")
                .description("Sparkling Drink")
                .price(new BigDecimal("899.00"))
                .build();

        mockMvc.perform(post("/product")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());
    }

    @Test
    void get_catalog_id() throws Exception {
        Integer id = createProduct("Coke", "Sparkling Drink", new BigDecimal("899.00"));
        mockMvc.perform(get("/product/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id));
    }

    @Test
    void put_update_catalog() throws Exception {
        Integer id = createProduct("Coke", "Sparkling Drink", new BigDecimal("899.00"));
        mockMvc.perform(put("/product/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(
                        UpdateCatalogDto.builder()
                            .name("Sprite")
                            .description(null)
                            .price(new BigDecimal("999.99"))
                            .build()
                )))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Sprite"))
                .andExpect(jsonPath("$.price").value(new BigDecimal("999.99")))
                .andReturn();
    }

    @Test
    void post_get_catalog() throws Exception {
        Integer product1 = createProduct("Latte", "Sparkling Drink", new BigDecimal("899.00"));
        Integer product2 = createProduct("Latte XL", "Venti Coffe", new BigDecimal("999.99"));
        Integer product3 = createProduct("Espresso", "Short", new BigDecimal("499.00"));

        FilteredCatalogDto filter = new FilteredCatalogDto();
        filter.setName("Latte");

        mockMvc.perform(post("/product/newsearch")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(filter)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].name").value("Latte"))
                .andExpect(jsonPath("$.content[0].price").value(new BigDecimal("899.0")))
                .andExpect(jsonPath("$.content[1].name").value("Latte XL"))
                .andExpect(jsonPath("$.content[1].price").value(new BigDecimal("999.99")))
                .andReturn();
    }

    @Test
    void deleteProduct() throws Exception {
        Integer product = createProduct("Latte", "Sparkling Drink", new BigDecimal("899.00"));
        mockMvc.perform(delete("/product/{id}", product))
                .andExpect(status().isNoContent())
                .andReturn();
    }
}
