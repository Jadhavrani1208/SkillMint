package com.skillmint.repository;

import com.skillmint.domain.entity.SkillRequest;
import com.skillmint.domain.enums.RequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SkillRequestRepository extends JpaRepository<SkillRequest, Long> {
    // NOTE: use explicit queries to avoid any ambiguity in derived query parsing
    // for association traversal (receiver.id / sender.id).
    @Query("""
            select r from SkillRequest r
            where r.receiver.id = :receiverId
            order by r.createdAt desc
            """)
    List<SkillRequest> findReceived(@Param("receiverId") Long receiverId);

    @Query("""
            select r from SkillRequest r
            where r.sender.id = :senderId
            order by r.createdAt desc
            """)
    List<SkillRequest> findSent(@Param("senderId") Long senderId);

    // Backward-compatible derived queries used in other services (e.g. messaging contacts).
    // Keeping these avoids wider refactors while still using explicit queries for the API list views.
    List<SkillRequest> findByReceiverId(Long receiverId);
    List<SkillRequest> findBySenderId(Long senderId);

    long countByReceiverIdAndStatus(Long receiverId, RequestStatus status);
}
