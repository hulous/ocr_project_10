package com.ycyw.chatapi.controllers;

import java.util.List;

import com.ycyw.chatapi.dtos.MessageDto;
import com.ycyw.chatapi.responses.ApiMessageResponse;
import com.ycyw.chatapi.services.ChatService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/conversations")
@Tag(name = "Conversations", description = "Conversation history endpoints")
public class ConversationsController {

    private final ChatService chatService;

    public ConversationsController(ChatService chatService) {
      this.chatService = chatService;
    }

    @GetMapping("/{conversationId}/messages")
    @Operation(
      summary = "Get conversation message history",
      security = { @SecurityRequirement(name = "bearerAuth") }
    )
    @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Conversation messages returned", content = @Content(array = @ArraySchema(schema = @Schema(implementation = MessageDto.class)))),
      @ApiResponse(responseCode = "401", description = "Unauthorized request", content = @Content(schema = @Schema(implementation = ApiMessageResponse.class))),
      @ApiResponse(responseCode = "500", description = "Server error", content = @Content(schema = @Schema(implementation = ApiMessageResponse.class)))
    })
    public List<MessageDto> getHistory(
      @Parameter(description = "Conversation identifier", required = true)
      @PathVariable String conversationId
    ) {
      return chatService.getConversationHistory(conversationId);
    }
}
