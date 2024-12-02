package com.exampl.demo.service;

import com.exampl.demo.dto.EmailDto;
import com.exampl.demo.dto.EmailRequest;
import com.exampl.demo.HashFunction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.util.*;

@Service
@Slf4j
public class BloomService {
    private int bitArraySize = 100000000;
    private int[] countingBloomFilter;
    private HashSet<String> hashSet = new HashSet<>();
    private static List<EmailRequest> emailRequests = new ArrayList<>();
    private List<HashFunction> hashFunctions;

    public BloomService() {
        // Counting Bloom Filter 초기화
        countingBloomFilter = new int[bitArraySize];
        hashFunctions = Arrays.asList(
                new HashFunction(31, bitArraySize),
                new HashFunction(53, bitArraySize),
                new HashFunction(97, bitArraySize)
        );
    }

    @PostConstruct
    public void initializeSpamPatterns() {
        // 스팸 이메일 패턴 10000개 추가
        for (int i = 0; i < 1000000; i++) {
            String spamDomain = "spamdomain" + i + ".com";
            addDomain(spamDomain);
        }
    }


    /*
    스팸여부 판단 메소드
     */

    public List<EmailRequest> analyze(EmailDto emailDto) {
        EmailRequest emailRequest = new EmailRequest();
        emailRequest.setEmail(emailDto.getSender());
        emailRequest.setTitle(emailDto.getTitle());
        String email = emailDto.getSender();
        long startTime = System.nanoTime();
        boolean isSpam = emailDto.getMethod().equals("HashTable만 사용") ? checkInHashSet(email) : mightContain(email);
        long endTime = System.nanoTime();
        long durationMicro = (endTime - startTime) / 1000; // 나노초를 마이크로초로 변환
        emailRequest.setTime(durationMicro + " µs"); // 마이크로초로 시간 설정
        emailRequest.setMethod(emailDto.getMethod());
        emailRequest.setSpam(isSpam);
        if (isSpam) {
            emailRequest.setReason("도메인 불일치");
        }
        emailRequests.add(emailRequest);
        return emailRequests;
    }

    public void addDomain(String domain) {
        for (HashFunction hashFunction : hashFunctions) {
            int index = hashFunction.hash(domain);
            countingBloomFilter[index]++;
        }
        hashSet.add(domain);
        log.info("add domain={}",domain);
    }

    public void removeDomain(String domain) {
        for (HashFunction hashFunction : hashFunctions) {
            int index = hashFunction.hash(domain);
            if (countingBloomFilter[index] > 0) {
                countingBloomFilter[index]--;
            }
        }
        hashSet.remove(domain);
        log.info("remove domain={}",domain);
    }

    public boolean mightContain(String email) {
        String[] emailParts = email.split("@");
        if (emailParts.length != 2) {
            return true;
        }
        String domain = emailParts[1];
        for (HashFunction hashFunction : hashFunctions) {
            int index = hashFunction.hash(domain);
            if (countingBloomFilter[index] == 0) {
                return false;
            }
        }
        return hashSet.contains(domain);
    }

    public boolean checkInHashSet(String email) {
        String[] emailParts = email.split("@");
        if (emailParts.length != 2) {
            return true;
        }
        return hashSet.contains(emailParts[1]);
    }


    public List<EmailRequest> getAllEmailRequests() {
        return emailRequests;
    }

    public HashSet<String> getHashSet() {
        return hashSet;
    }
    // BloomService.java 내 추가
    public List<String> getPagedHashSet(int page, int pageSize) {
        List<String> hashSetList = new ArrayList<>(hashSet);
        int start = page * pageSize;
        int end = Math.min(start + pageSize, hashSetList.size());
        if (start > hashSetList.size()) {
            return new ArrayList<>();
        }
        return hashSetList.subList(start, end);
    }

    public List<String> searchSpamDomains(String keyword) {
        return hashSet.stream()
                .filter(domain -> domain.contains(keyword))
                .toList();  // Java 16 이상에서 사용 가능
    }

    public void clearAllEmailRequests() {
        emailRequests.clear();
    }



}
