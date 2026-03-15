package com.gener.chat.controllers;

import com.gener.chat.dtos.request.CallActionReq;
import com.gener.chat.dtos.request.CreateCallReq;
import com.gener.chat.dtos.response.CallRes;
import com.gener.chat.exception.APIException;
import com.gener.chat.services.CallService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/calls")
@RequiredArgsConstructor
public class CallController {
    private final CallService callService;

    @PostMapping
    public CallRes create(@RequestBody CreateCallReq req) throws APIException {
        return callService.create(req);
    }

    @PostMapping("/{callId}/accept")
    public void accept(@PathVariable Long callId, @RequestBody CallActionReq req) {
        callService.accept(callId, req.userId());
    }

    @PostMapping("/{callId}/reject")
    public void reject(@PathVariable Long callId, @RequestBody CallActionReq req) {
        callService.reject(callId, req.userId());
    }

    @PostMapping("/{callId}/end")
    public void end(@PathVariable Long callId) throws APIException {
        callService.end(callId);
    }
}
