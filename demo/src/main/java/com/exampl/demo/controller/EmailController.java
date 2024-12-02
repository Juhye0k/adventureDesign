package com.exampl.demo.controller;

import com.exampl.demo.dto.EmailDto;
import com.exampl.demo.dto.EmailRequest;
import com.exampl.demo.service.BloomService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

import java.util.List;

@RequiredArgsConstructor
@Controller
@SessionAttributes({"emailDto", "emailRequest"})
public class EmailController {

    private final BloomService bloomService;

    @GetMapping("/analyze")
    public String showForm(@RequestParam(defaultValue = "0") int page, Model model, HttpSession session) {
        if (!model.containsAttribute("emailDto")) {
            model.addAttribute("emailDto", new EmailDto());
        }

        if (session.getAttribute("emailRequest") != null) {
            model.addAttribute("emailRequest", session.getAttribute("emailRequest"));
        }

        List<String> hashSetPage = bloomService.getPagedHashSet(page, 10);
        int totalPages = (int) Math.ceil((double) bloomService.getHashSet().size() / 10);

        model.addAttribute("hashSet", hashSetPage);
        model.addAttribute("page", page);
        model.addAttribute("totalPages", totalPages);

        return "index";
    }

    @PostMapping("/analyze")
    public String analyze(EmailDto emailDto, @RequestParam(defaultValue = "0") int page, Model model, HttpSession session) {
        List<EmailRequest> emailRequest = bloomService.analyze(emailDto);
        model.addAttribute("emailDto", emailDto);
        model.addAttribute("emailRequest", emailRequest);

        // 세션에 분석 결과 저장
        session.setAttribute("emailRequest", emailRequest);

        List<String> hashSetPage = bloomService.getPagedHashSet(page, 10);
        int totalPages = (int) Math.ceil((double) bloomService.getHashSet().size() / 10);

        model.addAttribute("hashSet", hashSetPage);
        model.addAttribute("page", page);
        model.addAttribute("totalPages", totalPages);

        return "index";
    }

    @PostMapping("/addDomain")
    public String addDomain(@RequestParam("domain") String domain, @RequestParam(defaultValue = "0") int page, Model model, HttpSession session) {
        bloomService.addDomain(domain);

        if (!model.containsAttribute("emailDto")) {
            model.addAttribute("emailDto", new EmailDto());
        }

        if (session.getAttribute("emailRequest") != null) {
            model.addAttribute("emailRequest", session.getAttribute("emailRequest"));
        }

        List<String> hashSetPage = bloomService.getPagedHashSet(page, 10);
        int totalPages = (int) Math.ceil((double) bloomService.getHashSet().size() / 10);

        model.addAttribute("hashSet", hashSetPage);
        model.addAttribute("page", page);
        model.addAttribute("totalPages", totalPages);

        return "index";
    }

    @PostMapping("/removeDomain")
    public String subDomain(@RequestParam("domain") String domain, @RequestParam(defaultValue = "0") int page, Model model, HttpSession session) {
        bloomService.removeDomain(domain);

        if (!model.containsAttribute("emailDto")) {
            model.addAttribute("emailDto", new EmailDto());
        }

        if (session.getAttribute("emailRequest") != null) {
            model.addAttribute("emailRequest", session.getAttribute("emailRequest"));
        }

        List<String> hashSetPage = bloomService.getPagedHashSet(page, 10);
        int totalPages = (int) Math.ceil((double) bloomService.getHashSet().size() / 10);

        model.addAttribute("hashSet", hashSetPage);
        model.addAttribute("page", page);
        model.addAttribute("totalPages", totalPages);

        return "index";
    }

    @GetMapping("/searchSpam")
    public String searchSpam(@RequestParam("keyword") String keyword, @RequestParam(defaultValue = "0") int page, Model model, HttpSession session) {
        List<String> searchResults = bloomService.searchSpamDomains(keyword);

        if (!model.containsAttribute("emailDto")) {
            model.addAttribute("emailDto", new EmailDto());
        }

        if (session.getAttribute("emailRequest") != null) {
            model.addAttribute("emailRequest", session.getAttribute("emailRequest"));
        }

        model.addAttribute("hashSet", searchResults);
        model.addAttribute("page", page);
        model.addAttribute("totalPages", (searchResults.size() + 9) / 10);

        return "index";
    }
    @PostMapping("/clearAnalysis")
    public String clearAnalysis(HttpSession session) {
        // 세션에서 분석 결과 삭제
        session.removeAttribute("emailRequest");

        // BloomService에 있는 분석 결과 리스트 비우기
        bloomService.clearAllEmailRequests();

        return "redirect:/analyze";
    }

}
