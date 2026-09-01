import { Component, OnDestroy, OnInit } from "@angular/core";
import { ActivatedRoute } from "@angular/router";
import { Subject, takeUntil } from "rxjs";
import { ChatService, MessageDto } from "./chat.service";

interface ChatMessage {
  author: string;
  text: string;
  timestamp: string;
  datetime: string;
}

@Component({
  selector: "app-chat",
  templateUrl: "./chat.component.html",
  styles: [
    ".chat-window { border: 1px solid #cbd5e1; border-radius: 0.75rem; padding: 1rem; min-height: 18rem; background: #f8fafc; }",
    ".chat-message { margin-bottom: 1rem; padding-bottom: 0.75rem; border-bottom: 1px solid #e2e8f0; }",
    ".chat-message:last-child { border-bottom: none; margin-bottom: 0; padding-bottom: 0; }",
    ".chat-message strong { display: block; margin-bottom: 0.25rem; font-weight: 600; }",
    ".chat-message time { display: block; margin-bottom: 0.5rem; color: #64748b; font-size: 0.875rem; }",
    ".chat-form { display: grid; gap: 0.75rem; margin-top: 1rem; }",
    ".chat-form label { font-weight: 600; }",
    ".chat-form textarea { width: 100%; min-height: 6rem; padding: 0.75rem; border: 1px solid #cbd5e1; border-radius: 0.5rem; resize: vertical; }",
    ".chat-form button { width: fit-content; padding: 0.75rem 1.25rem; border: none; border-radius: 0.5rem; background: #2563eb; color: white; cursor: pointer; }",
    ".chat-form button:disabled { background: #94a3b8; cursor: not-allowed; }",
  ],
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
      });

    this.chatService.connect(this.conversationId);
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
