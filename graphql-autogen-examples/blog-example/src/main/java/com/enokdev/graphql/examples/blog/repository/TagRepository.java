
package com.enokdev.graphql.examples.blog.repository;

import com.enokdev.graphql.examples.blog.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {
}
