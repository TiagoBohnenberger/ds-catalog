package com.devsuperior.dscatalog.repositories;

import com.devsuperior.dscatalog.entities.Category;
import com.devsuperior.dscatalog.entities.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("SELECT DISTINCT prod FROM Product prod INNER JOIN prod.categories cats " +
            "WHERE (COALESCE(:categories) IS NULL OR cats IN :categories) " +
            "AND (LOWER(prod.name) LIKE LOWER(CONCAT('%', :name, '%'))) ")
    Page<Product> findProducts(List<Category> categories, String name, Pageable pageable);

    @Query("SELECT prod FROM Product prod JOIN FETCH prod.categories " +
            "WHERE prod IN :products")
    List<Product> findProductsWithCategories(List<Product> products);
}
