package com.gener.chat.controllers;

import com.gener.chat.dtos.request.CreateConversationReq;
import com.gener.chat.dtos.request.NameUpdateReq;
import com.gener.chat.exception.APIException;
import com.gener.chat.models.ResponseObject;
import com.gener.chat.services.ConversationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/conversations")
@RequiredArgsConstructor
public class ConversationController {

    private final ConversationService conversationService;

    @PostMapping
    public ResponseEntity<ResponseObject> create(
            @RequestBody CreateConversationReq request
    ) throws APIException {
        return conversationService.createConversation(request);
    }

    @PutMapping("/{conversationId}/nickname")
    public ResponseEntity<ResponseObject> updateNickname(
            @PathVariable Long conversationId,
            @RequestBody NameUpdateReq request

    ) throws APIException {
        return conversationService.updateNickname(conversationId,request);
    }

    @PutMapping("/{conversationId}/title")
    public ResponseEntity<ResponseObject> updateTitle(
            @PathVariable Long conversationId,
            @RequestBody NameUpdateReq request

    ) throws APIException {
        return conversationService.updateTitleGroup(conversationId,request);
    }



    @GetMapping("/{conversationId}")
    public ResponseEntity<ResponseObject> getConversationById(@PathVariable Long conversationId) throws APIException {
        return conversationService.getConversationById(conversationId);
    }

    @GetMapping("/detail/{conversationId}")
    public ResponseEntity<ResponseObject> getDetailConversationById(@PathVariable Long conversationId) throws APIException {
        return conversationService.getDetailConversationById(conversationId);
    }

    @GetMapping("/user")
    public ResponseEntity<ResponseObject> getListConversationsByUserId() throws APIException{
        return conversationService.getListConversationsByUserId();
    }

    @GetMapping("/user/search")
    public ResponseEntity<ResponseObject> searchConversationsByUserId(@RequestParam String keyword) throws APIException{
        return conversationService.searchConversationsByUserId(keyword);
    }
}
