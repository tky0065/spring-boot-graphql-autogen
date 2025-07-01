
package com.enokdev.graphql.examples.blog.repository;

import com.enokdev.graphql.examples.blog.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
}
