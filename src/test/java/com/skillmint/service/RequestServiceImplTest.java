package com.skillmint.service;

import com.skillmint.domain.entity.Skill;
import com.skillmint.domain.entity.SkillRequest;
import com.skillmint.domain.entity.User;
import com.skillmint.domain.enums.RequestStatus;
import com.skillmint.domain.enums.RequestType;
import com.skillmint.domain.enums.SessionStatus;
import com.skillmint.dto.RequestDtos;
import com.skillmint.exception.ApiException;
import com.skillmint.repository.SkillRepository;
import com.skillmint.repository.SkillRequestRepository;
import com.skillmint.service.impl.RequestServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * Unit tests for RequestServiceImpl.
 * Covers the core business rules around creating and updating skill requests:
 * - a user cannot request from themselves
 * - COIN_SESSION requests require a positive coinsPerSession
 * - the skill being requested must belong to the receiver
 * - only the receiver can accept/reject a request
 * - only PENDING requests can transition, and never back to PENDING
 */
@ExtendWith(MockitoExtension.class)
class RequestServiceImplTest {

    @Mock
    private SkillRequestRepository requestRepository;
    @Mock
    private SkillRepository skillRepository;
    @Mock
    private CurrentUserService currentUserService;
    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private RequestServiceImpl requestService;

    private User sender;
    private User receiver;
    private Skill skill;

    @BeforeEach
    void setUp() {
        sender = User.builder().id(1L).fullName("Alice").username("alice").email("alice@test.com")
                .password("x").coins(10).build();
        receiver = User.builder().id(2L).fullName("Bob").username("bob").email("bob@test.com")
                .password("x").coins(10).build();
        skill = Skill.builder().id(100L).name("Guitar").level("Intermediate").user(receiver).build();
    }

    // ---------- createRequest ----------

    @Test
    void createRequest_throwsWhenRequestTypeMissing() {
        RequestDtos.CreateRequestRequest req = new RequestDtos.CreateRequestRequest(2L, 100L, null, null);

        assertThatThrownBy(() -> requestService.createRequest(1L, req))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining("requestType is required");

        verifyNoInteractions(requestRepository);
    }

    @Test
    void createRequest_throwsWhenCoinSessionHasNoPositiveCoins() {
        RequestDtos.CreateRequestRequest req =
                new RequestDtos.CreateRequestRequest(2L, 100L, RequestType.COIN_SESSION, 0);

        assertThatThrownBy(() -> requestService.createRequest(1L, req))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining("coinsPerSession must be greater than 0");
    }

    @Test
    void createRequest_throwsWhenSenderRequestsFromThemselves() {
        RequestDtos.CreateRequestRequest req =
                new RequestDtos.CreateRequestRequest(1L, 100L, RequestType.COIN_SESSION, 5);

        assertThatThrownBy(() -> requestService.createRequest(1L, req))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining("Cannot create request for yourself");
    }

    @Test
    void createRequest_throwsWhenSkillNotFound() {
        RequestDtos.CreateRequestRequest req =
                new RequestDtos.CreateRequestRequest(2L, 100L, RequestType.COIN_SESSION, 5);
        when(skillRepository.findById(100L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> requestService.createRequest(1L, req))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining("Skill not found");
    }

    @Test
    void createRequest_throwsWhenSkillDoesNotBelongToReceiver() {
        User someoneElse = User.builder().id(3L).fullName("Carl").build();
        Skill mismatchedSkill = Skill.builder().id(100L).name("Guitar").user(someoneElse).build();
        RequestDtos.CreateRequestRequest req =
                new RequestDtos.CreateRequestRequest(2L, 100L, RequestType.COIN_SESSION, 5);
        when(skillRepository.findById(100L)).thenReturn(Optional.of(mismatchedSkill));

        assertThatThrownBy(() -> requestService.createRequest(1L, req))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining("Skill does not belong to selected receiver");
    }

    @Test
    void createRequest_succeedsAndNotifiesReceiver() {
        RequestDtos.CreateRequestRequest req =
                new RequestDtos.CreateRequestRequest(2L, 100L, RequestType.COIN_SESSION, 5);
        when(skillRepository.findById(100L)).thenReturn(Optional.of(skill));
        when(currentUserService.requireUser(1L)).thenReturn(sender);
        when(currentUserService.requireUser(2L)).thenReturn(receiver);
        when(requestRepository.save(any(SkillRequest.class))).thenAnswer(invocation -> {
            SkillRequest r = invocation.getArgument(0);
            r.setId(500L);
            return r;
        });

        RequestDtos.SkillRequestResponse response = requestService.createRequest(1L, req);

        assertThat(response.id()).isEqualTo(500L);
        assertThat(response.status()).isEqualTo(RequestStatus.PENDING);
        assertThat(response.senderId()).isEqualTo(1L);
        assertThat(response.receiverId()).isEqualTo(2L);
        verify(notificationService).create(eq(2L), any(), anyString());
    }

    // ---------- updateStatus ----------

    @Test
    void updateStatus_throwsWhenRequestNotFound() {
        when(requestRepository.findById(500L)).thenReturn(Optional.empty());
        RequestDtos.UpdateRequestStatusRequest req = new RequestDtos.UpdateRequestStatusRequest(RequestStatus.ACCEPTED);

        assertThatThrownBy(() -> requestService.updateStatus(500L, 2L, req))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining("Request not found");
    }

    @Test
    void updateStatus_throwsWhenActingUserIsNotReceiver() {
        SkillRequest pending = SkillRequest.builder().id(500L).sender(sender).receiver(receiver)
                .skill(skill).status(RequestStatus.PENDING).build();
        when(requestRepository.findById(500L)).thenReturn(Optional.of(pending));
        RequestDtos.UpdateRequestStatusRequest req = new RequestDtos.UpdateRequestStatusRequest(RequestStatus.ACCEPTED);

        assertThatThrownBy(() -> requestService.updateStatus(500L, 1L, req))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining("Only receiver can update request");
    }

    @Test
    void updateStatus_throwsWhenRequestIsNotPending() {
        SkillRequest alreadyAccepted = SkillRequest.builder().id(500L).sender(sender).receiver(receiver)
                .skill(skill).status(RequestStatus.ACCEPTED).build();
        when(requestRepository.findById(500L)).thenReturn(Optional.of(alreadyAccepted));
        RequestDtos.UpdateRequestStatusRequest req = new RequestDtos.UpdateRequestStatusRequest(RequestStatus.REJECTED);

        assertThatThrownBy(() -> requestService.updateStatus(500L, 2L, req))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining("Only pending requests can be updated");
    }

    @Test
    void updateStatus_throwsWhenTransitioningBackToPending() {
        SkillRequest pending = SkillRequest.builder().id(500L).sender(sender).receiver(receiver)
                .skill(skill).status(RequestStatus.PENDING).build();
        when(requestRepository.findById(500L)).thenReturn(Optional.of(pending));
        RequestDtos.UpdateRequestStatusRequest req = new RequestDtos.UpdateRequestStatusRequest(RequestStatus.PENDING);

        assertThatThrownBy(() -> requestService.updateStatus(500L, 2L, req))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining("Request cannot be set back to pending");
    }

    @Test
    void updateStatus_acceptingSetsSessionActiveAndNotifiesSender() {
        SkillRequest pending = SkillRequest.builder().id(500L).sender(sender).receiver(receiver)
                .skill(skill).status(RequestStatus.PENDING).build();
        when(requestRepository.findById(500L)).thenReturn(Optional.of(pending));
        when(requestRepository.save(any(SkillRequest.class))).thenAnswer(invocation -> invocation.getArgument(0));
        RequestDtos.UpdateRequestStatusRequest req = new RequestDtos.UpdateRequestStatusRequest(RequestStatus.ACCEPTED);

        RequestDtos.SkillRequestResponse response = requestService.updateStatus(500L, 2L, req);

        assertThat(response.status()).isEqualTo(RequestStatus.ACCEPTED);
        assertThat(response.sessionStatus()).isEqualTo(SessionStatus.ACTIVE);
        assertThat(response.senderCompleted()).isFalse();
        assertThat(response.receiverCompleted()).isFalse();
        verify(notificationService).create(eq(1L), any(), anyString());
    }

    @Test
    void updateStatus_rejectingDoesNotTouchSessionStatusOrNotifySender() {
        SkillRequest pending = SkillRequest.builder().id(500L).sender(sender).receiver(receiver)
                .skill(skill).status(RequestStatus.PENDING).build();
        when(requestRepository.findById(500L)).thenReturn(Optional.of(pending));
        when(requestRepository.save(any(SkillRequest.class))).thenAnswer(invocation -> invocation.getArgument(0));
        RequestDtos.UpdateRequestStatusRequest req = new RequestDtos.UpdateRequestStatusRequest(RequestStatus.REJECTED);

        RequestDtos.SkillRequestResponse response = requestService.updateStatus(500L, 2L, req);

        assertThat(response.status()).isEqualTo(RequestStatus.REJECTED);
        assertThat(response.sessionStatus()).isNull();
        verify(notificationService, never()).create(anyLong(), any(), anyString());
    }
}