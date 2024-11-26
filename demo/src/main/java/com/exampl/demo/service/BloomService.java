package com.exampl.demo.service;
import com.exampl.demo.dto.EmailDto;
import com.exampl.demo.dto.EmailRequest;
import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
@Slf4j
public class BloomService {
    private int bitArraySize = 1000;
    private BloomFilter<String> bloomFilter;
    private HashSet<String> hashSet = new HashSet<>();
    private static List<EmailRequest> emailRequests = new ArrayList<>();

    public BloomService() {
        // Bloom Filter 초기화
        bloomFilter = BloomFilter.create(Funnels.unencodedCharsFunnel(), 10000, 0.01);
    }

    @PostConstruct
    public void initializeSpamPatterns() {
        // 스팸 이메일 패턴 10000개 추가
        for (int i = 0; i < 9999999; i++) {
            String spamDomain = "spamdomain" + i + ".com";
            addDomain(spamDomain);
        }
    }

    public List<EmailRequest> analyze(EmailDto emailDto) {
        EmailRequest emailRequest = new EmailRequest();
        emailRequest.setEmail(emailDto.getSender());
        emailRequest.setTitle(emailDto.getTitle());
        String email = emailDto.getSender();
        long startTime = System.nanoTime();
        boolean isSpam = emailDto.getMethod().equals("HashTable만 사용") ? bloomFilter.mightContain(email) : HashSet(email);
        long endTime = System.nanoTime();
        long durationMicro = (endTime - startTime) / 1000; // 나노초를 마이크로초로 변환
        emailRequest.setTime(durationMicro + " µs"); // 마이크로초로 시간 설정
        emailRequest.setSpam(isSpam);
        if (isSpam) {
            emailRequest.setReason("도메인 불일치");
            emailRequest.setMethod(emailDto.getMethod());
        }
        emailRequests.add(emailRequest);
        return emailRequests;
    }

    public void addDomain(String domain) {
        bloomFilter.put(domain);
        hashSet.add(domain);
    }

    public boolean HashSet(String email) {
        String[] emailParts = email.split("@");
        if (emailParts.length != 2) {
            return true;
        }

        return hashSet.contains(emailParts[1]);
    }

    public void reportEmail(String email) {
        String domain = email.split("@")[1];
        hashSet.remove(domain);
    }

    public HashSet<String> getTrustedDomains() {
        return new HashSet<>(hashSet);
    }

    public List<EmailRequest> getAllEmailRequests() {
        return emailRequests; // 기존의 이메일 요청 리스트를 반환합니다.
    }

    public HashSet<String> getHashSet() {
        return hashSet;
    }

}
