package com.network.network.user;

import com.fasterxml.jackson.annotation.JsonView;
import com.network.network.misc.View;
import com.network.network.user.resource.UserResourceAssembler;
import com.network.network.user.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@JsonView(View.AsProfessional.class)
@RequestMapping(value = "/users/{id}/connections", produces = "application/hal+json")
public class ConnectionsController {
    @Resource
    private UserService userService;

    @Resource
    private UserResourceAssembler userResourceAssembler;

    @GetMapping("")
    @JsonView(View.AsProfessional.class)
    public ResponseEntity<?> getConnections(@PathVariable int id) {
        User principal = userService.getPrincipal();
        User user = userService.getUserById(id);

        if (user.isConnected(principal)) {
            return ResponseEntity.ok(userResourceAssembler.toCollectionModel(user.getConnected()));
        }

        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).build();
    }
}
