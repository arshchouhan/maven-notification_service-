package com.hayday.notifications.repository;

import com.hayday.notifications.model.NotificationDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationRepository extends MongoRepository<NotificationDocument, String> {

    @Query(value = "{ 'user_id': ?0 }", sort = "{ 'created_at': -1 }")
    Page<NotificationDocument> findByUser_idOrderByCreated_atDesc(String user_id, Pageable pageable);

    @Query(value = "{ 'user_id': ?0, 'status': ?1 }", sort = "{ 'created_at': -1 }")
    List<NotificationDocument> findByUser_idAndStatusOrderByCreated_atDesc(String user_id, String status);

    @Query("{ 'user_id': ?0, 'animal_id': ?1, 'category': ?2 }")
    List<NotificationDocument> findByUser_idAndAnimal_idAndCategory(String user_id, String animal_id, String category);

    @Query("{ 'dedup_key': ?0 }")
    Optional<NotificationDocument> findByDedup_key(String dedup_key);

    @Query(value = "{ 'user_id': ?0, 'status': ?1 }", count = true)
    long countByUser_idAndStatus(String user_id, String status);

    @Query(value = "{ 'user_id': ?0, 'category': ?1 }", count = true)
    long countByUser_idAndCategory(String user_id, String category);

    @Query("{ 'user_id': ?0, 'status': { $ne: 'resolved' } }")
    long countUnresolvedByUser_id(String user_id);
}
