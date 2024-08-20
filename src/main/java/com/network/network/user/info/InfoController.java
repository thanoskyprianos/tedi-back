package com.network.network.user.info;

import com.fasterxml.jackson.annotation.JsonView;
import com.network.network.misc.View;
import com.network.network.user.User;
import com.network.network.user.info.service.InfoService;
import com.network.network.user.service.UserService;
import jakarta.annotation.Resource;
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

        return ResponseEntity.ok(new Info(info));
    }

    @PutMapping("") // user always has info from construction
    @PreAuthorize("#id == principal.getId()")
    @JsonView(View.AsProfessional.class)
    public ResponseEntity<?> updateInfo(@PathVariable int id, @RequestBody Info info) {
        User user = userService.getUserById(id);

        Info userInfo = user.getInfo();

        infoService.updateInfo(userInfo, info);

        return ResponseEntity.ok(userInfo);
    }

    @PutMapping("/privacy")
    @PreAuthorize("#id == principal.getId()")
    @JsonView(View.AsProfessional.class)
    public ResponseEntity<?> updateInfoPrivacy(@PathVariable int id, @RequestBody Info info) {
        User user = userService.getUserById(id);

        Info userInfo = user.getInfo();

        infoService.updateInfoPrivacy(userInfo, info);

        return ResponseEntity.noContent().build();
    }
}
