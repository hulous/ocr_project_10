import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Subject, Subscription, take } from 'rxjs';
import { RxStomp } from '@stomp/rx-stomp';
import SockJS from 'sockjs-client';
import { AuthService } from './auth';

export interface MessageDto {
  id: string;
  conversationId: string;
  senderEmail: string;
  content: string;
  sentAt: string;
}

interface IncomingMessagePayload {
  conversationId: string;
  content: string;
}

@Injectable({ providedIn: 'root' })
export class ChatService {
  private client = new RxStomp();
  private messagesSubject = new Subject<MessageDto>();
  readonly messages$ = this.messagesSubject.asObservable();
  private connected = false;
  private connectedConversationId: string | null = null;
  private messageSubscription?: Subscription;

  constructor(
    private authService: AuthService,
    private http: HttpClient,
  ) {}

  connect(conversationId: string): void {
    if (this.connected && this.connectedConversationId === conversationId) {
      return;
    }

    if (this.connected) {
      this.disconnect();
    }

    this.client.configure({
      webSocketFactory: () => new SockJS('/ws'),
      connectHeaders: {
        Authorization: `Bearer ${this.authService.getToken()}`,
      },
      reconnectDelay: 5000,
    });

    this.client.connected$.pipe(take(1)).subscribe(() => {
      const stompSubscription = this.client.stompClient.subscribe(
        `/topic/conversations/${conversationId}`,
        (frame) => {
          const message: MessageDto = JSON.parse(frame.body);
          this.messagesSubject.next(message);
        },
      );
      this.messageSubscription = new Subscription(() => stompSubscription.unsubscribe());
    });

    this.client.activate();
    this.connected = true;
    this.connectedConversationId = conversationId;
  }

  send(conversationId: string, content: string): void {
    const payload: IncomingMessagePayload = { conversationId, content };
    this.client.publish({
      destination: '/app/chat.send',
      body: JSON.stringify(payload),
    });
  }

  loadHistory(conversationId: string) {
    return this.http.get<MessageDto[]>(`/api/conversations/${conversationId}/messages`);
  }

  disconnect(): void {
    this.messageSubscription?.unsubscribe();
    this.messageSubscription = undefined;
    this.client.deactivate();
    this.connected = false;
    this.connectedConversationId = null;
  }
}
