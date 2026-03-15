package com.gener.chat.configuration;

import com.gener.chat.enums.ErrorCode;
import com.gener.chat.models.ResponseObject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

@Slf4j
@Component
public class JsonAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest req, HttpServletResponse res, AccessDeniedException ex) throws IOException {
        String user = SecurityContextHolder.getContext().getAuthentication() != null
                ? SecurityContextHolder.getContext().getAuthentication().getName()
                : "anonymous";
        ErrorCode errorCode = ErrorCode.ACCESS_DENIED;

        res.setStatus(errorCode.getHttpStatusCode().value());
        res.setContentType(MediaType.APPLICATION_JSON_VALUE);

        ResponseObject responseObject = ResponseObject.builder()
                .status(errorCode.getStatus())
                .message(errorCode.getMessage())
                .build();
        ObjectMapper objectMapper = new ObjectMapper();

        res.getWriter().write(objectMapper.writeValueAsString(responseObject));
        res.flushBuffer();
    }
}
