package com.ycyw.chatapi.repositories;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ycyw.chatapi.entities.Message;

@Repository
public interface MessageRepository extends JpaRepository<Message, UUID> {
  List<Message> findByConversationIdOrderBySentAtAsc(String conversationId);
  List<Message> findBySenderIdOrderBySentAtAsc(Integer senderId);
  List<Message> findByConversationIdAndSenderIdOrderBySentAtAsc(String conversationId, Integer senderId);
}
