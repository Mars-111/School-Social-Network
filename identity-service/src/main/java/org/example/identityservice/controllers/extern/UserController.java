package org.example.identityservice.controllers.extern;

import lombok.RequiredArgsConstructor;
import org.example.identityservice.models.UserInfo;
import org.example.identityservice.models.entity.User;
import org.example.identityservice.services.UserService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/{id}")
    public UserInfo getUserById(@PathVariable("id") Long id) {
        return userService.getUserInfoById(id);
    }

    @GetMapping("/version-verify/{userId}/{version}")
    public boolean isVersionSync(@PathVariable("userId") Long userId, @PathVariable("version") Integer version) {
        return userService.existsByIdAndVersion(userId, version);
    }

    @PostMapping("/me/avatar")
    public void updateAvatar(@RequestParam("fileId") Long fileId) {
        Long userId = userService.getUserInfoById(fileId).getId();
        User user = userService.findById(userId);
        user.setAvatarFileId(fileId);
        //Хорошо было бы узнать картинка ли это и публичный ли файл
        userService.save(user);
    }
}
