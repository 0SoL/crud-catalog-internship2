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
import ru.rustam.catalog.repository.CatalogRepository;

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
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17")
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
    @Autowired private CatalogRepository catalogRepository;

    private Integer createProdut(String name, String description, BigDecimal price) throws Exception {
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


    // ЗДЕСЬ POST И СРАЗУ ПРОВЕРКА НА GET
    @Test
    void post_create_catalog() throws Exception {
        Integer id = createProdut("Coke", "Sparkling Drink", new BigDecimal("899.00"));

        mockMvc.perform(get("/product/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Coke"))
                .andExpect(jsonPath("$.description").value("Sparkling Drink"))
                .andReturn();
    }

    // ЭТО КАЖИСЬ НЕ НУЖНО
    @Test
    void get_catalog_id() throws Exception {
        Integer id = createProdut("Coke", "Sparkling Drink", new BigDecimal("899.00"));

        mockMvc.perform(get("/product/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Coke"))
                .andExpect(jsonPath("$.description").value("Sparkling Drink"))
                .andReturn();
    }


    // ТЕСТ для обновления
    @Test
    void put_update_catalog() throws Exception {
        Integer id = createProdut("Coke", "Sparkling Drink", new BigDecimal("899.00"));

        mockMvc.perform(put("/product/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(
                        UpdateCatalogDto.builder()
                            .name("Coke")
                            .description(null)
                            .price(new BigDecimal("999.99"))
                            .build()
                )))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Coke"))
                .andExpect(jsonPath("$.price").value(new BigDecimal("999.99")))
                .andReturn();
    }

    @Test
    void post_get_catalog() throws Exception {
//        catalogRepository.saveAll(List.of(
//                new ru.rustam.catalog.entity.CatalogEntity("Latte",  "Grande Coffe",  new BigDecimal("899.99")),
//                new ru.rustam.catalog.entity.CatalogEntity("Latte XL", "Venti Coffe", new BigDecimal("999.99")),
//                new ru.rustam.catalog.entity.CatalogEntity("Espresso", "Short", new BigDecimal("499.00"))
//        ));
        Integer product1 = createProdut("Latte", "Sparkling Drink", new BigDecimal("899.00"));
        Integer product2 = createProdut("Latte XL", "Venti Coffe", new BigDecimal("999.99"));
        Integer product3 = createProdut("Espresso", "Short", new BigDecimal("499.00"));

        var filter = new FilteredCatalogDto();
        filter.setName("Latte");

        mockMvc.perform(post("/product/newsearch")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(filter)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].name").value("Latte"))
                .andExpect(jsonPath("$.content[0].price").value(new BigDecimal("899.00")))
                .andExpect(jsonPath("$.content[1].name").value("Latte"))
                .andExpect(jsonPath("$.content[1].price").value(new BigDecimal("999.99")))
                .andDo(print());
    }
}
