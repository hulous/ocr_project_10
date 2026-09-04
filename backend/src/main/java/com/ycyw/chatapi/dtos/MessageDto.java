package com.ycyw.chatapi.dtos;

import java.time.Instant;
import java.util.UUID;

public record MessageDto(
  UUID id,
  String conversationId,
  String senderEmail,
  String senderName,
  String content,
  Instant sentAt
) {}
