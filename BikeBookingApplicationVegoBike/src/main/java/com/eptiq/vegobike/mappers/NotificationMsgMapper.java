package com.eptiq.vegobike.mappers;

import com.eptiq.vegobike.dtos.NotificationMsgDTO;
import com.eptiq.vegobike.dtos.NotificationMsgDTO;
import com.eptiq.vegobike.model.NotificationMsg;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface NotificationMsgMapper {

    NotificationMsgMapper INSTANCE = Mappers.getMapper(NotificationMsgMapper.class);

    NotificationMsgDTO toDTO(NotificationMsg entity);

    NotificationMsg toEntity(NotificationMsgDTO dto);

    List<NotificationMsgDTO> toDTOList(List<NotificationMsg> entities);
}
