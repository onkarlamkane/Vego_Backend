package com.eptiq.vegobike.repositories;


import com.eptiq.vegobike.model.NotificationMsg;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationMsgRepository extends JpaRepository<NotificationMsg, Long> {
    List<NotificationMsg> findByRecipient(String recipient);
}