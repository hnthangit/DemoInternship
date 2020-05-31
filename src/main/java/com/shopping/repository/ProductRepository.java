package com.shopping.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.shopping.entity.Product;

@Repository("productRepository")
public interface ProductRepository extends JpaRepository<Product, Integer>, JpaSpecificationExecutor<Product> {
	
	 boolean existsByName(String name);
	 
	 boolean existsBySku(String sku);
	 
	 boolean existsByUrl(String url);
	 
	 Product findByUrl(String url);
	 
	 @Query(value = "SELECT * FROM product WHERE product.category_id IN ( SELECT category_id FROM category WHERE parent_id = ?1 OR category_id = ?1)", nativeQuery = true)
	 List<Product> findAllByParentCategory(int categoryId);	
	 
	 @Query(value = "SELECT * FROM product WHERE product.category_id = ?1", nativeQuery = true)
	 List<Product> findByCategoryId(int categoryId);
	 
	 @Query(value = "SELECT * FROM product WHERE product.manufacturer_id =?1", nativeQuery = true)
	 List<Product> findByManufacturerId(int manufacturerId);
	 
	 @Query(value = "SELECT * FROM product WHERE product_id IN (SELECT product_id FROM promotion_product WHERE promotion_id = ?1)", nativeQuery = true)
	 List<Product> findByPromotionId(int promotionId);
}
