package com.skillmint.config;

import com.skillmint.domain.entity.Message;
import com.skillmint.domain.entity.Skill;
import com.skillmint.domain.entity.SkillRequest;
import com.skillmint.domain.entity.User;
import com.skillmint.domain.entity.WalletTransaction;
import com.skillmint.domain.enums.RequestStatus;
import com.skillmint.domain.enums.SkillType;
import com.skillmint.domain.enums.TransactionType;
import com.skillmint.repository.MessageRepository;
import com.skillmint.repository.SkillRepository;
import com.skillmint.repository.SkillRequestRepository;
import com.skillmint.repository.UserRepository;
import com.skillmint.repository.WalletTransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class DemoDataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final SkillRepository skillRepository;
    private final SkillRequestRepository skillRequestRepository;
    private final MessageRepository messageRepository;
    private final WalletTransactionRepository walletTransactionRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        if (userRepository.count() > 0) {
            log.info("Seed skipped: database already has users.");
            return;
        }

        User rani = User.builder()
                .username("rani")
                .email("rani@example.com")
                .password(passwordEncoder.encode("password123"))
                .fullName("Rani Jadhav")
                .coins(240)
                .location("Pune, India")
                .bio("Full-stack developer who loves skill exchanges.")
                .build();

        User anjali = User.builder()
                .username("anjali")
                .email("anjali@example.com")
                .password(passwordEncoder.encode("password123"))
                .fullName("Anjali Sharma")
                .coins(180)
                .location("Mumbai, India")
                .bio("UI/UX enthusiast learning backend development.")
                .build();

        User rahul = User.builder()
                .username("rahul")
                .email("rahul@example.com")
                .password(passwordEncoder.encode("password123"))
                .fullName("Rahul Khanna")
                .coins(150)
                .location("Bengaluru, India")
                .bio("Python and data science mentor.")
                .build();

        userRepository.saveAll(List.of(rani, anjali, rahul));

        Skill spring = Skill.builder().name("Java Spring Boot").level("Expert").type(SkillType.TEACH).user(rani).build();
        Skill react = Skill.builder().name("React.js").level("Intermediate").type(SkillType.TEACH).user(rani).build();
        Skill figma = Skill.builder().name("Figma").level("Intermediate").type(SkillType.TEACH).user(anjali).build();
        Skill python = Skill.builder().name("Python Basics").level("Expert").type(SkillType.TEACH).user(rahul).build();
        Skill ml = Skill.builder().name("Machine Learning").level("Beginner").type(SkillType.LEARN).user(rani).build();
        Skill uiux = Skill.builder().name("UI/UX Design").level("Beginner").type(SkillType.LEARN).user(rahul).build();
        skillRepository.saveAll(List.of(spring, react, figma, python, ml, uiux));

        SkillRequest req1 = SkillRequest.builder()
                .sender(anjali)
                .receiver(rani)
                .skill(spring)
                .coinsPerSession(10)
                .status(RequestStatus.PENDING)
                .build();
        SkillRequest req2 = SkillRequest.builder()
                .sender(rani)
                .receiver(anjali)
                .skill(figma)
                .coinsPerSession(10)
                .status(RequestStatus.ACCEPTED)
                .build();
        SkillRequest req3 = SkillRequest.builder()
                .sender(rani)
                .receiver(rahul)
                .skill(python)
                .coinsPerSession(15)
                .status(RequestStatus.REJECTED)
                .build();
        skillRequestRepository.saveAll(List.of(req1, req2, req3));

        Message m1 = Message.builder().sender(anjali).receiver(rani).content("Hi Rani! Can you help me with Spring Boot basics?").build();
        Message m2 = Message.builder().sender(rani).receiver(anjali).content("Sure, happy to help. We can start tomorrow.").build();
        Message m3 = Message.builder().sender(rahul).receiver(rani).content("Thanks for the API session notes.").build();
        messageRepository.saveAll(List.of(m1, m2, m3));

        WalletTransaction t1 = WalletTransaction.builder().user(rani).amount(100).type(TransactionType.BONUS).description("Signup bonus").build();
        WalletTransaction t2 = WalletTransaction.builder().user(anjali).amount(100).type(TransactionType.BONUS).description("Signup bonus").build();
        WalletTransaction t3 = WalletTransaction.builder().user(rahul).amount(100).type(TransactionType.BONUS).description("Signup bonus").build();
        walletTransactionRepository.saveAll(List.of(t1, t2, t3));

        log.info("Demo seed inserted: 3 users, 6 skills, 3 requests, 3 messages, 3 wallet transactions.");
    }
}
