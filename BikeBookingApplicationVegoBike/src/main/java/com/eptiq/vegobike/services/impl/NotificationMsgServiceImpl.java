package com.eptiq.vegobike.services.impl;

import com.eptiq.vegobike.dtos.NotificationMsgDTO;
import com.eptiq.vegobike.mappers.NotificationMsgMapper;
import com.eptiq.vegobike.model.NotificationMsg;
import com.eptiq.vegobike.repositories.NotificationMsgRepository;
import com.eptiq.vegobike.services.NotificationMsgService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;

@Service
public class NotificationMsgServiceImpl implements NotificationMsgService {

    @Autowired
    private NotificationMsgRepository repository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private NotificationMsgMapper mapper;  // ✅ MapStruct Mapper injected

    @Override
    public void sendNotification(NotificationMsgDTO dto) {
        NotificationMsg notification = mapper.toEntity(dto);
        notification.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        notification.setRead(false);

        NotificationMsg saved = repository.save(notification);

        // push via websocket
        messagingTemplate.convertAndSend(
                "/topic/notification-msg/" + saved.getRecipient(), // ✅ match WebSocketConfig
                mapper.toDTO(saved)
        );
    }

    @Override
    public List<NotificationMsgDTO> getNotifications(String recipient) {
        return mapper.toDTOList(repository.findByRecipient(recipient));
    }
}
