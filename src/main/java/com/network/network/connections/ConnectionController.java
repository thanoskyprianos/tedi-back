package com.network.network.connections;

import com.fasterxml.jackson.annotation.JsonView;
import com.network.network.misc.View;
import com.network.network.connections.resource.ConnectionNotificationResourceAssembler;
import com.network.network.connections.service.ConnectionService;
import com.network.network.user.User;
import com.network.network.user.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/users/{id}/connections", produces = "application/hal+json")
public class ConnectionController {
    @Resource
    private UserService userService;

    @Resource
    private ConnectionService connectionService;

    @Resource
    private ConnectionNotificationResourceAssembler connectionNotificationResourceAssembler;

    @JsonView(View.AsProfessional.class)
    @PostMapping("/request/{friendId}")
    @PreAuthorize("#id == principal.getId()")
    public ResponseEntity<?> addFriend(@PathVariable int id, @PathVariable int friendId) {
        User sender = userService.getUserById(id);
        User receiver = userService.getUserById(friendId);

        if (sender == receiver || sender.isConnected(receiver)) {
            return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).build();
        }

        EntityModel<ConnectionNotification> connectionNotificationEntityModel =
                connectionNotificationResourceAssembler.toModel(
                        connectionService.createNotification(sender, receiver)
                );

        return ResponseEntity
                .created(connectionNotificationEntityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(connectionNotificationEntityModel);
    }

    @JsonView(View.AsProfessional.class)
    @DeleteMapping("/remove/{friendId}")
    @PreAuthorize("#id == principal.getId()")
    public ResponseEntity<?> removeFriend(@PathVariable int id, @PathVariable int friendId) {
        User sender = userService.getUserById(id);
        User receiver = userService.getUserById(friendId);

        if (!sender.isConnected(receiver)) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        connectionService.remove(sender, receiver);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/requests/sent")
    @JsonView(View.AsProfessional.class)
    @PreAuthorize("#id == principal.getId()")
    public ResponseEntity<?> getSent(@PathVariable int id) {
        return ResponseEntity.ok(
                connectionNotificationResourceAssembler.toCollectionModel(
                        connectionService.getSent(id)));
    }

    @GetMapping("/requests/sent/{notificationId}")
    @JsonView(View.AsProfessional.class)
    @PreAuthorize("#id == principal.getId()")
    public ResponseEntity<?> getSent(@PathVariable int id, @PathVariable int notificationId) {
        return ResponseEntity.ok(
                connectionNotificationResourceAssembler.toModel(
                        connectionService.getSent(id, notificationId)));
    }

    @DeleteMapping("/requests/sent/{notificationId}/cancel")
    @JsonView(View.AsProfessional.class)
    @PreAuthorize("#id == principal.getId()")
    public ResponseEntity<?> cancelRequest(@PathVariable int id, @PathVariable int notificationId) {
        ConnectionNotification connectionNotification =
                connectionService.getSent(id, notificationId);

        connectionService.cancel(connectionNotification);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/requests/received")
    @JsonView(View.AsProfessional.class)
    @PreAuthorize("#id == principal.getId()")
    public ResponseEntity<?> getReceived(@PathVariable int id) {
        return ResponseEntity.ok(
                connectionNotificationResourceAssembler.toCollectionModel(
                        connectionService.getReceived(id)));
    }

    @GetMapping("/requests/received/{notificationId}")
    @JsonView(View.AsProfessional.class)
    @PreAuthorize("#id == principal.getId()")
    public ResponseEntity<?> getReceived(@PathVariable int id, @PathVariable int notificationId) {
        return ResponseEntity.ok(
                connectionNotificationResourceAssembler.toModel(
                        connectionService.getReceived(id, notificationId)));
    }

    @PutMapping("/requests/received/{notificationId}/accept")
    @JsonView(View.AsProfessional.class)
    @PreAuthorize("#id == principal.getId()")

    public ResponseEntity<?> acceptRequest(@PathVariable int id, @PathVariable int notificationId) {
        ConnectionNotification connectionNotification
                = connectionService.getReceived(id, notificationId);

        connectionService.accept(connectionNotification);
        return ResponseEntity.noContent().build();
    }


    @DeleteMapping("/requests/received/{notificationId}/reject")
    @JsonView(View.AsProfessional.class)
    @PreAuthorize("#id == principal.getId()")
    public ResponseEntity<?> rejectRequest(@PathVariable int id, @PathVariable int notificationId) {
        ConnectionNotification connectionNotification
                = connectionService.getReceived(id, notificationId);

        connectionService.reject(connectionNotification);
        return ResponseEntity.noContent().build();
    }
}
