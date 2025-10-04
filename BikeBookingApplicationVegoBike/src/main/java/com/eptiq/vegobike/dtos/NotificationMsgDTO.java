package com.eptiq.vegobike.dtos;

import java.sql.Timestamp;

public class NotificationMsgDTO {

    private Long id;
    private String recipient;
    private String message;
    private String type;
    private int bookingId;
    private Timestamp createdAt;
    private boolean read;
}
