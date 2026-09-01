import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Subject } from 'rxjs';
import { RxStomp } from '@stomp/rx-stomp';
import SockJS from 'sockjs-client';
import { AuthService } from '../core/services/auth';

export interface MessageDto {
  id: string;
  conversationId: string;
  senderEmail: string;
  content: string;
  sentAt: string;   // ISO-8601, sérialisé par Jackson côté backend
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

  constructor(
    private authService: AuthService,
    private http: HttpClient,
  ) {}

  connect(conversationId: string): void {
    if (this.connected) {
      return;
    }

    this.client.configure({
      webSocketFactory: () => new SockJS('/ws'),
      connectHeaders: { Authorization: `Bearer ${this.authService.getToken()}` },
      reconnectDelay: 5000,
    });

    this.client.activate();
    this.connected = true;

    this.client.watch(`/topic/conversations/${conversationId}`).subscribe((frame) => {
      const message: MessageDto = JSON.parse(frame.body);
      this.messagesSubject.next(message);
    });
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
    this.client.deactivate();
    this.connected = false;
  }
}
