package com.ordershare.backend.repository;

import com.ordershare.backend.entity.PostInterest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface PostInterestRepository extends JpaRepository<PostInterest, Long> {

    boolean existsByPost_IdAndUser_Id(String postId, String userId);

    @Query("select i.post.id from PostInterest i where i.user.id = :userId")
    Set<String> findPostIdsByUserId(@Param("userId") String userId);

    @Query("""
            select i
            from PostInterest i
            join fetch i.user
            where i.post.id in :postIds
            order by i.createdAt desc
            """)
    List<PostInterest> findAllByPostIdsWithUsers(@Param("postIds") Collection<String> postIds);
}
