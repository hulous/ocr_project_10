import { Component, DestroyRef, effect, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { firstValueFrom } from 'rxjs';
import { toSignal } from '@angular/core/rxjs-interop';
import { ChatService, MessageDto } from '../core/services/chat.service';
import { ChatMessage } from '../core/models/chat-message.interface';

@Component({
  selector: 'app-chat',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './chat.component.html',
})
export class ChatComponent {
  private readonly defaultConversationId = 'demo';
  private readonly route = inject(ActivatedRoute);
  private readonly chatService = inject(ChatService);
  private readonly destroyRef = inject(DestroyRef);
  private readonly latestMessage = toSignal(this.chatService.messages$, { initialValue: null });
  conversationId = this.defaultConversationId;
  draft = signal('');
  messages = signal<ChatMessage[]>([]);

  constructor() {
    effect(() => {
      const message = this.latestMessage();
      if (message?.conversationId === this.conversationId) {
        this.messages.update((messages) => [...messages, this.toChatMessage(message)]);
      }
    });
    this.destroyRef.onDestroy(() => this.chatService.disconnect());

    void this.loadConversation();
  }

  private async loadConversation(): Promise<void> {
    this.conversationId =
      this.route.snapshot.paramMap.get('conversationId') ?? this.defaultConversationId;
    const messages = await firstValueFrom(this.chatService.loadHistory(this.conversationId));
    this.messages.set(messages.map((message) => this.toChatMessage(message)));
    this.chatService.connect(this.conversationId);
  }

  sendMessage() {
    const content = this.draft().trim();
    if (!content) {
      return;
    }

    this.chatService.send(this.conversationId, content);
    this.draft.set('');
  }

  private toChatMessage(message: MessageDto): ChatMessage {
    return {
      author: message.senderName || message.senderEmail,
      text: message.content,
      timestamp: new Date(message.sentAt).toLocaleTimeString('fr-FR'),
      datetime: message.sentAt,
    };
  }
}
