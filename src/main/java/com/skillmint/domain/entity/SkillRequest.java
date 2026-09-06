package com.skillmint.domain.entity;

import com.skillmint.domain.enums.RequestStatus;
import com.skillmint.domain.enums.RequestType;
import com.skillmint.domain.enums.SessionStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "skill_requests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SkillRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", nullable = false)
    private User receiver;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "skill_id", nullable = false)
    private Skill skill;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RequestStatus status;

    @Enumerated(EnumType.STRING)
    @Column
    private RequestType requestType;

    @Enumerated(EnumType.STRING)
    @Column
    private SessionStatus sessionStatus;

    // For COIN_SESSION: amount transferred on completion (dual confirmation)
    private Integer coinsPerSession;

    @Column
    private Boolean senderCompleted;

    @Column
    private Boolean receiverCompleted;

    private Instant completedAt;

    private Instant terminatedAt;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    void prePersist() {
        if (status == null) status = RequestStatus.PENDING;
        if (requestType == null) requestType = RequestType.COIN_SESSION;
        if (senderCompleted == null) senderCompleted = false;
        if (receiverCompleted == null) receiverCompleted = false;
        createdAt = Instant.now();
    }
}
