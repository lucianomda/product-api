package com.lucianomda.rakuten.productapi.persistence.repository;

import com.lucianomda.rakuten.productapi.persistence.model.Product;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends CrudRepository<Product, Long> {

	/**
	 * Count number of product with an specified category asociated.
	 * IMPORTANT: The name of the method does not comply with java naming conventions but it was done on purpose to use query builder mechanism built into Spring Data repository to lookup into many to many relationship.
	 *
	 * @param categoryId The id from a category ({@link com.lucianomda.rakuten.productapi.persistence.model.Category#id}).
	 * @return Quantity of products assigned to an specific category.
	 */
	long countByCategories_Id(long categoryId);
}
