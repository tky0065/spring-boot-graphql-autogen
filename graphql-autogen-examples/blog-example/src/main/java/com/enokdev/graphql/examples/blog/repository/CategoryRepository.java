
package com.enokdev.graphql.examples.blog.repository;

import com.enokdev.graphql.examples.blog.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
}
