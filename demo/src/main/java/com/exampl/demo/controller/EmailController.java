package com.exampl.demo.controller;
import com.exampl.demo.dto.EmailDto;
import com.exampl.demo.dto.EmailRequest;
import com.exampl.demo.service.BloomService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.HashSet;
import java.util.List;
@RequiredArgsConstructor
@Controller
public class EmailController {

    private final BloomService bloomService;
    @GetMapping("/analyze")
    public String showForm(Model model) {
//        HashSet<String> hashSet=bloomService.getHashSet();
//        model.addAttribute("hashSet",hashSet);
        model.addAttribute("emailDto", new EmailDto());
        return "index";
    }
    @PostMapping("/analyze")
    public String analyze(EmailDto emailDto, Model model){
        List<EmailRequest> emailRequest= bloomService.analyze(emailDto);
//        HashSet<String> hashSet=bloomService.getHashSet();
//        model.addAttribute("hashSet",hashSet);
        model.addAttribute("emailDto", emailDto);
        model.addAttribute("emailRequest",emailRequest);
        return "index";
    }
    @PostMapping("/addDomain")
    public String addDomain(@RequestParam("domain") String domain, EmailDto emailDto, Model model){
        bloomService.addDomain(domain);
//        HashSet<String> hashSet=bloomService.getHashSet();
        List<EmailRequest> emailRequest=bloomService.getAllEmailRequests();
        model.addAttribute("emailDto", emailDto);
        model.addAttribute("emailRequest", emailRequest);
//        model.addAttribute("hashSet",hashSet);
        return "index";
    }
}
