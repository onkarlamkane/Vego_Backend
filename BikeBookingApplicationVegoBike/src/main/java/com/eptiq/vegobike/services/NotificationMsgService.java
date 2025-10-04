package com.eptiq.vegobike.services;

import com.eptiq.vegobike.dtos.NotificationMsgDTO;

import java.util.List;

public interface NotificationMsgService {

    void sendNotification(NotificationMsgDTO dto);
    List<NotificationMsgDTO> getNotifications(String recipient);


}
