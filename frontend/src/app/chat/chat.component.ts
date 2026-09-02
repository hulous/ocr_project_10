import { Component, OnDestroy, OnInit } from "@angular/core";
import { ActivatedRoute } from "@angular/router";
import { Subject, takeUntil } from "rxjs";
import { ChatService, MessageDto } from "../core/services/chat.service";

interface ChatMessage {
  author: string;
  text: string;
  timestamp: string;
  datetime: string;
}

@Component({
  selector: "app-chat",
  templateUrl: "./chat.component.html",
})
export class ChatComponent implements OnInit, OnDestroy {
  private readonly destroy$ = new Subject<void>();
  private readonly defaultConversationId = "demo";
  conversationId = this.defaultConversationId;
  draft = "";
  messages: ChatMessage[] = [];

  constructor(
    private readonly route: ActivatedRoute,
    private readonly chatService: ChatService,
  ) {}

  ngOnInit(): void {
    this.conversationId =
      this.route.snapshot.paramMap.get("conversationId") ??
      this.defaultConversationId;
    this.chatService.messages$
      .pipe(takeUntil(this.destroy$))
      .subscribe((message) => {
        if (message.conversationId === this.conversationId) {
          this.messages.push(this.toChatMessage(message));
        }
      });
    this.chatService
      .loadHistory(this.conversationId)
      .pipe(takeUntil(this.destroy$))
      .subscribe((messages) => {
        this.messages = messages.map((message) => this.toChatMessage(message));
        this.chatService.connect(this.conversationId);
      });
  }

  sendMessage() {
    const content = this.draft.trim();
    if (!content) {
      return;
    }

    this.chatService.send(this.conversationId, content);
    this.draft = "";
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
    this.chatService.disconnect();
  }

  private toChatMessage(message: MessageDto): ChatMessage {
    return {
      author: message.senderEmail,
      text: message.content,
      timestamp: new Date(message.sentAt).toLocaleTimeString("fr-FR"),
      datetime: message.sentAt,
    };
  }
}
