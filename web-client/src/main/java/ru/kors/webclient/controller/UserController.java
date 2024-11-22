package ru.kors.webclient.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.kors.webclient.client.UserRestClient;
import ru.kors.webclient.client.payload.CreateUserPayload;

@Controller
@RequiredArgsConstructor
@RequestMapping("user")
public class UserController {
    UserRestClient userRestClient;

    @GetMapping
    public String index() {
        return "index";
    }

    @PostMapping("/registration")
    public String registration(CreateUserPayload userPayload, Model model) {
        var response = userRestClient.createUser(userPayload);
        if (!(response.getStatusCode() == HttpStatus.CREATED)) {
            model.addAttribute("errorMessage", response.getBody());
        }
        return "login";
    }
}
