package com.skillmint.repository;

import com.skillmint.domain.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    @Query("""
            select m from Message m
            where (m.sender.id = :userId and m.receiver.id = :withUserId)
               or (m.sender.id = :withUserId and m.receiver.id = :userId)
            order by m.timestamp asc
            """)
    List<Message> findConversation(Long userId, Long withUserId);

    long countByReceiverId(Long receiverId);

    @Query("select distinct m.receiver.id from Message m where m.sender.id = :userId")
    List<Long> findDistinctReceiverIdsBySenderId(Long userId);

    @Query("select distinct m.sender.id from Message m where m.receiver.id = :userId")
    List<Long> findDistinctSenderIdsByReceiverId(Long userId);
}
