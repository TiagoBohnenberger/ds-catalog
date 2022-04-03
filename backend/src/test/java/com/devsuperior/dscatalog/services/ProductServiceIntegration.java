package com.devsuperior.dscatalog.services;

import com.devsuperior.dscatalog.dto.ProductDTO;
import com.devsuperior.dscatalog.repositories.ProductRepository;
import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class ProductServiceIntegration {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    private long existingId;
    private long nonExistingId;
    private long totalProducts;

    @BeforeEach
    void setUp() {

        existingId = 1L;
        nonExistingId = 1000L;
        totalProducts = 25L;
    }

    @Test
    public void deleteShouldDeleteProductWhenIdExists() {

        productService.delete(existingId);

        assertEquals(totalProducts - 1, productRepository.count());
    }

    @Test
    public void deleteShouldThrowResourceNotFoundExceptionWhenIdDoesNotExists() {

        assertThrows(ResourceNotFoundException.class, () -> {
            productService.delete(nonExistingId);
        });
    }

    @Test
    public void findAllPagedShouldReturnPageWhenPage0Size10() {

        PageRequest pageRequest = PageRequest.of(0, 10);

        Page<ProductDTO> resultPage = productService.findAllPaged(0L, "", pageRequest);

        assertFalse(resultPage.isEmpty());
        assertEquals(0, resultPage.getNumber());
        assertEquals(10, resultPage.getSize());
        assertEquals(totalProducts, resultPage.getTotalElements());
    }

    @Test
    public void findAllPagedShouldReturnEmptyPageWhenPageDoesNotExist() {

        PageRequest pageRequest = PageRequest.of(50, 10);

        Page<ProductDTO> resultPage = productService.findAllPaged(0L, "", pageRequest);

        assertTrue(resultPage.isEmpty());
    }

    @Test
    public void findAllPagedShouldReturnSortedPageWhenSortedByName() {

        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by("name"));

        Page<ProductDTO> resultPage = productService.findAllPaged(0L, "", pageRequest);

        assertFalse(resultPage.isEmpty());
        assertEquals(0, resultPage.getNumber());
        assertEquals(10, resultPage.getSize());
        assertEquals("Macbook Pro", resultPage.getContent().get(0).getName());
        assertEquals("PC Gamer", resultPage.getContent().get(1).getName());
        assertEquals("PC Gamer Alfa", resultPage.getContent().get(2).getName());
        assertEquals(totalProducts, resultPage.getTotalElements());
    }
}
