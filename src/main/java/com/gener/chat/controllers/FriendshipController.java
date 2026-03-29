package com.gener.chat.controllers;

import com.gener.chat.exception.APIException;
import com.gener.chat.models.ResponseObject;
import com.gener.chat.services.FriendshipService;
import com.gener.chat.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api/v1/friends")
@RequiredArgsConstructor
public class FriendshipController {
    private final UserService userService;
    private final FriendshipService friendshipService;

    @PostMapping("/request/{peerId}")
    public ResponseEntity<ResponseObject> sendFriendRequest(@PathVariable Long peerId) throws APIException {
        return friendshipService.sendFriendRequest(peerId);
    }

    @PostMapping("/cancel/{peerId}")
    public ResponseEntity<ResponseObject> cancelFriendRequest(@PathVariable Long peerId) throws APIException {
        return friendshipService.cancelFriendRequest(peerId);
    }

    @PostMapping("/accept/{peerId}")
    public ResponseEntity<ResponseObject> acceptFriendRequest(@PathVariable Long peerId) throws APIException {
        return friendshipService.acceptFriendRequest(peerId);
    }

    @PostMapping("/reject/{peerId}")
    public ResponseEntity<ResponseObject> rejectFriendRequest(@PathVariable Long peerId) throws APIException {
        return friendshipService.rejectFriendRequest(peerId);
    }

    @PostMapping("/unfriend/{peerId}")
    public ResponseEntity<ResponseObject> unfriend(@PathVariable Long peerId) throws APIException {
        return friendshipService.unfriend(peerId);
    }

    @PostMapping("/block/{peerId}")
    public ResponseEntity<ResponseObject> blockUser(@PathVariable Long peerId) throws APIException {
        return friendshipService.blockUser(peerId);
    }

    @PostMapping("/unblock/{peerId}")
    public ResponseEntity<ResponseObject> unblockUser(@PathVariable Long peerId) throws APIException {
        return friendshipService.unblockUser(peerId);
    }

    @GetMapping("/search/{value}/phone-or-userCode")
    ResponseEntity<ResponseObject> getUserByPhoneOrUserCode(@PathVariable String value) throws APIException {
        return userService.getUserByPhoneOrUserCode(value);
    }

    @GetMapping("/contacts")
    public ResponseEntity<ResponseObject> getContacts() throws APIException {
        return friendshipService.getMyFriends();

    }

}
