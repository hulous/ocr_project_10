package com.ycyw.chatapi.mappers;

import com.ycyw.chatapi.dtos.MessageDto;
import com.ycyw.chatapi.entities.Message;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MessageMapper {

  @Mapping(source = "sender.email", target = "senderEmail")
  @Mapping(source = "sender.name", target = "senderName")
  MessageDto toDto(Message message);
}
