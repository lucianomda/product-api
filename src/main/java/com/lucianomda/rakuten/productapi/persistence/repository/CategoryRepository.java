package com.lucianomda.rakuten.productapi.persistence.repository;

import com.lucianomda.rakuten.productapi.persistence.model.Category;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends CrudRepository<Category, Long> {

	long countByParentId(long parentId);
}
