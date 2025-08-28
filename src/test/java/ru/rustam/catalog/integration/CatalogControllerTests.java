package ru.rustam.catalog.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.rustam.catalog.repository.CatalogRepository;
import ru.rustam.catalog.repository.CategoryRepository;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private CatalogRepository catalogRepository;
    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    void createCatalog() throws Exception {
        var dto =  new CatalogDto("Latte", "Grande Coffe", 899.99);

        // Создаем объект
        var mvcResult = mockMvc.perform(post("/product")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Latte"))
                .andExpect(jsonPath("$.description").value("Grande Coffe"))
                .andReturn();

        // Берем айдишник объекта
        Integer id = objectMapper.readTree(mvcResult.getResponse().getContentAsString()).get("id").asInt();


        // Ищем созданный объект по айдишнику
        var fromDb = catalogRepository.findById(id).orElseThrow();

        // Сравниваем значения
        assertEquals("Latte", fromDb.getName());
        assertEquals(new BigDecimal("899.99"), fromDb.getPrice());

        //
        mockMvc.perform(get("/product/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value("Grande Coffe"))
                .andExpect(jsonPath("$.name").value("Latte"));


        var newMvcResult = mockMvc.perform(put("/product/{id}", id)
        .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new CatalogDto("Latte", "Venti Coffe", 999.99))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value("Venti Coffe"))
                .andExpect(jsonPath("$.name").value("Latte"))
                .andExpect(jsonPath("$.price").value(new BigDecimal("999.99")))
                .andReturn();

        id = objectMapper.readTree(newMvcResult.getResponse().getContentAsString()).get("id").asInt();
        var fromNewDb = catalogRepository.findById(id).orElseThrow();
        assertEquals("Latte", fromNewDb.getName());
        assertEquals(new BigDecimal("999.99"), fromNewDb.getPrice());
        assertEquals("Venti Coffe", fromNewDb.getDescription());
    }



    @Test
    void createCategory() throws Exception {
        var dto = new CategoryDto("Овощи");

        var mvcResult = mockMvc.perform(post("/category")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Овощи"))
                .andReturn();

        Integer id =  objectMapper.readTree(mvcResult.getResponse().getContentAsString()).get("id").asInt();
        var fromDb = categoryRepository.findById(id).orElseThrow();

        assertEquals("Овощи", fromDb.getName());

    }

    public record CategoryDto(String name) {
    }
    public record CatalogDto(String name, String description, Double price) {}
}
