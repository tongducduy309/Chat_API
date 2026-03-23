package com.gener.chat.controllers;

import com.gener.chat.dtos.request.SendMessageReq;
import com.gener.chat.exception.APIException;
import com.gener.chat.models.ResponseObject;
import com.gener.chat.services.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/conversations")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @PostMapping("/messages")
    public ResponseEntity<ResponseObject> send(
                                               @RequestBody SendMessageReq req) throws APIException {
        return messageService.send(req);
    }

    @GetMapping("/{conversationId}/messages")
    public ResponseEntity<ResponseObject> list(@PathVariable Long conversationId) {
        return messageService.getMessage(conversationId);
    }

    @DeleteMapping("/message/{messageId}/delete-for-me")
    public ResponseEntity<ResponseObject> deleteForMe(@PathVariable Long messageId) throws APIException {
        return messageService.deleteForMe(messageId);
    }

    @PostMapping("/{conversationId}/message/read")
    public ResponseEntity<ResponseObject> read(@PathVariable Long conversationId) throws APIException {
        return messageService.readMessage(conversationId);
    }

    @GetMapping("/{conversationId}/messages/search")
    public ResponseEntity<ResponseObject> search(@PathVariable Long conversationId, @RequestParam String keyword) throws APIException {
        return messageService.searchMessages(conversationId,keyword);
    }
}