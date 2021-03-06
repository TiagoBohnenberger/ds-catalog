package com.devsuperior.dscatalog.resources;

import com.devsuperior.dscatalog.dto.ProductDTO;
import com.devsuperior.dscatalog.utils.Factory;
import com.devsuperior.dscatalog.utils.TokenUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class ProductResourceIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TokenUtil tokenUtil;

    private long existingId;
    private long nonExistingId;
    private long totalProducts;
    private String username;
    private String password;

    @BeforeEach
    void setUp() {
        existingId = 1L;
        nonExistingId = 1000L;
        totalProducts = 25L;
        username = "maria@gmail.com";
        password = "123456";
    }

    @Test
    public void findAllShouldReturnSortedPageWhenSortedByName() throws Exception {

        ResultActions result = mockMvc
                .perform(get("/products?page=0&size=12&sort=name,asc")
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.totalElements").value(totalProducts));
        result.andExpect(jsonPath("$.content").exists());
        result.andExpect(jsonPath("$.content[0].name").value("Macbook Pro"));
        result.andExpect(jsonPath("$.content[1].name").value("PC Gamer"));
        result.andExpect(jsonPath("$.content[2].name").value("PC Gamer Alfa"));
    }

    @Test
    public void updateShouldReturnProductDTOWhenIdExist() throws Exception {
        // Arrange
        String accessToken = tokenUtil.obtainAccessToken(mockMvc, username, password);
        ProductDTO productDTO = Factory.createProductDTO();
        String productDTOAsJson = objectMapper.writeValueAsString(productDTO);

        // Act
        ResultActions result = mockMvc
                .perform(put("/products/{id}", existingId)
                        .header("Authorization", "Bearer " + accessToken)
                        .content(productDTOAsJson)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON));

        // Assert
        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.id").exists());
        result.andExpect(jsonPath("$.name").exists());
        result.andExpect(jsonPath("$.description").exists());
        result.andExpect(jsonPath("$.price").exists());
    }

    @Test
    public void updateShouldReturnNotFoundWhenIdDoesNotExist() throws Exception {
        // Arrange
        String accessToken = tokenUtil.obtainAccessToken(mockMvc, username, password);
        ProductDTO productDTO = Factory.createProductDTO();
        String productDTOAsJson = objectMapper.writeValueAsString(productDTO);

        // Act
        ResultActions result = mockMvc
                .perform(put("/products/{id}", nonExistingId)
                        .header("Authorization", "Bearer " + accessToken)
                        .content(productDTOAsJson)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON));

        // Assert
        result.andExpect(status().isNotFound());
    }
}
