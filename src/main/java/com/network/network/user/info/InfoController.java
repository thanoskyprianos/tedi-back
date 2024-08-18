package com.network.network.user.info;

import com.fasterxml.jackson.annotation.JsonView;
import com.network.network.misc.View;
import com.network.network.user.User;
import com.network.network.user.info.service.InfoService;
import com.network.network.user.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/users/{id}/info", produces = "application/hal+json")
public class InfoController {
    @Resource
    private UserService userService;

    @Resource
    private InfoService infoService;

    @GetMapping("")
    @JsonView(View.AsProfessional.class)
    public ResponseEntity<?> getInfo(@PathVariable int id) {
        User principal = userService.getPrincipal();
        User user = userService.getUserById(id);

        if (principal == user) {
            return ResponseEntity.ok(user.getInfo());
        }

        Info info = user.getInfo();
        if (info == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(new Info(info, principal));
    }

    @JsonView(View.AsProfessional.class)
    @PostMapping("") @PreAuthorize("#id == principal.getId()")
    public ResponseEntity<?> addInfo(@PathVariable int id, @RequestBody Info info) {
        User user = userService.getUserById(id);

        info.setUser(user);
        infoService.saveInfo(info);

        user.setInfo(info);
        userService.updateUser(user);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("")
    @JsonView(View.AsProfessional.class)
    public ResponseEntity<?> updateInfo(@PathVariable int id, @RequestBody Info info) {
        User user = userService.getUserById(id);

        Info userInfo = user.getInfo();

        infoService.updateInfo(userInfo, info);

        return ResponseEntity.ok(userInfo);
    }

    @PutMapping("/experience/{privacy}") @PreAuthorize("#id == principal.getId()")
    @JsonView(View.AsProfessional.class)
    public ResponseEntity<?> updateExperience(@PathVariable int id, @PathVariable String privacy) {
        Info info = userService.getUserInfoOrThrow(id);

        infoService.updateExperiencePrivacy(info, Privacy.valueOf(privacy.toUpperCase()));

        return ResponseEntity.noContent().build();
    }

    @PutMapping("/education/{privacy}") @PreAuthorize("#id == principal.getId()")
    @JsonView(View.AsProfessional.class)
    public ResponseEntity<?> updateEducation(@PathVariable int id, @PathVariable String privacy) {
        Info info = userService.getUserInfoOrThrow(id);

        infoService.updateEducationPrivacy(info, Privacy.valueOf(privacy.toUpperCase()));

        return ResponseEntity.noContent().build();
    }

    @PutMapping("/skills/{privacy}") @PreAuthorize("#id == principal.getId()")
    @JsonView(View.AsProfessional.class)
    public ResponseEntity<?> updateSkills(@PathVariable int id, @PathVariable String privacy) {
        Info info = userService.getUserInfoOrThrow(id);

        infoService.updateSkillsPrivacy(info, Privacy.valueOf(privacy.toUpperCase()));

        return ResponseEntity.noContent().build();
    }
}
