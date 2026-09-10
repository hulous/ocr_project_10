import { Component, DestroyRef, effect, inject, signal } from "@angular/core";

import { ActivatedRoute, Router } from "@angular/router";
import { FormsModule } from "@angular/forms";
import { firstValueFrom } from "rxjs";
import { ChatService, MessageDto } from "../core/services/chat.service";
import { ChatMessage } from "../core/models/chat-message.interface";
import { AuthService } from "../core/services/auth";

@Component({
  selector: "app-chat",
  imports: [FormsModule],
  templateUrl: "./chat.component.html",
})
export class ChatComponent {
  private readonly defaultConversationId = "demo";
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly chatService = inject(ChatService);
  private readonly authService = inject(AuthService);
  private readonly destroyRef = inject(DestroyRef);

  conversationId = this.defaultConversationId;
  draft = signal("");
  messages = signal<ChatMessage[]>([]);
  isReady = signal(false);

  constructor() {
    effect(() => {
      const message = this.chatService.messages();
      if (message) {
        if (message?.conversationId === this.conversationId) {
          this.messages.update((messages) => [
            ...messages,
            this.toChatMessage(message),
          ]);
        }
      }
    });
    this.destroyRef.onDestroy(() => this.chatService.disconnect());

    void this.loadConversation();
  }

  private async loadConversation(): Promise<void> {
    this.conversationId =
      this.route.snapshot.paramMap.get("conversationId") ??
      this.defaultConversationId;
    await this.chatService.connect(this.conversationId);
    const messages = await firstValueFrom(
      this.chatService.loadHistory(this.conversationId),
    );
    this.messages.update((currentMessages) => [
      ...messages.map((message) => this.toChatMessage(message)),
      ...currentMessages,
    ]);
    this.isReady.set(true);
  }

  sendMessage() {
    const content = this.draft().trim();
    if (!content) {
      return;
    }

    this.chatService.send(this.conversationId, content);
    this.draft.set("");
  }

  handleEnterKey(event: KeyboardEvent): void {
    if (!event.shiftKey) {
      event.preventDefault();
      this.sendMessage();
    }
  }

  logout(): void {
    this.authService.logout();
    void this.router.navigate(["/login"]);
  }

  private toChatMessage(message: MessageDto): ChatMessage {
    return {
      author: message.senderName || message.senderEmail,
      text: message.content,
      timestamp: new Date(message.sentAt).toLocaleTimeString("fr-FR"),
      datetime: message.sentAt,
    };
  }
}
