package com.lucianomda.rakuten.productapi.persistence.repository;

import com.lucianomda.rakuten.productapi.persistence.model.Product;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends CrudRepository<Product, Long> {

	long countByCategories_Id(long categoryId);
}
