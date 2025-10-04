package com.eptiq.vegobike.controllers;

import com.eptiq.vegobike.dtos.NotificationMsgDTO;
import com.eptiq.vegobike.services.NotificationMsgService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notification-msg")
public class NotificationMsgController {

    @Autowired
    private NotificationMsgService notificationMsgService;

    @PostMapping("/send")
    public void sendNotification(@RequestBody NotificationMsgDTO dto) {
        notificationMsgService.sendNotification(dto);
    }

    @GetMapping("/{recipient}")
    public List<NotificationMsgDTO> getNotifications(@PathVariable String recipient) {
        return notificationMsgService.getNotifications(recipient);
    }
}
