package com.gener.chat.models;

import com.gener.chat.enums.CallStatus;
import com.gener.chat.enums.EventType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@DiscriminatorValue("CALL")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CallMessage extends Message {

    @Enumerated(EnumType.STRING)
    @Column(name = "call_status", length = 20)
    private CallStatus callStatus;

    @Column(name = "call_duration_seconds")
    private Long callDurationSeconds;

    @Column(name = "call_id")
    private Long callId;
}

