package ru.kors.socketbrokerservice.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/test")
@Slf4j
public class TestSecurityController {
    @GetMapping
    public String securityTest() {
        log.info("TestSecurityController");
        return "Secrity Test Is OK";
    }
}
