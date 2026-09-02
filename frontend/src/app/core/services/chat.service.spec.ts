import { HttpClient } from '@angular/common/http';
import { of } from 'rxjs';
import { IMessage, RxStomp } from '@stomp/rx-stomp';
import { AuthService } from './auth';
import { ChatService, MessageDto } from './chat.service';

describe('ChatService', () => {
  let service: ChatService;
  let authService: jasmine.SpyObj<AuthService>;
  let http: jasmine.SpyObj<HttpClient>;
  let client: jasmine.SpyObj<RxStomp>;
  const message: MessageDto = {
    id: 'message-1',
    conversationId: 'conversation-1',
    senderEmail: 'client@example.com',
    content: 'Hello',
    sentAt: '2026-01-01T12:00:00.000Z',
  };
  const frame: IMessage = {
    ack: jasmine.createSpy('ack'),
    nack: jasmine.createSpy('nack'),
    command: 'MESSAGE',
    headers: {},
    body: JSON.stringify(message),
    isBinaryBody: false,
    binaryBody: new Uint8Array(),
  };

  beforeEach(() => {
    authService = jasmine.createSpyObj('AuthService', ['getToken']);
    http = jasmine.createSpyObj('HttpClient', ['get']);
    client = jasmine.createSpyObj('RxStomp', [
      'configure',
      'activate',
      'watch',
      'publish',
      'deactivate',
    ]);
    client.watch.and.returnValue(of(frame));
    Object.defineProperty(client, 'stompClient', {
      value: {
        subscribe: jasmine.createSpy('subscribe').and.returnValue({ unsubscribe() {} }),
      },
    });
    Object.defineProperty(client, 'connected$', { value: of({}) });
    service = new ChatService(authService, http);
    Object.defineProperty(service, 'client', { value: client });
  });

  it('loads conversation history through the API', () => {
    http.get.and.returnValue(of([message]));

    service.loadHistory('conversation-1').subscribe((messages) => {
      expect(messages).toEqual([message]);
    });

    expect(http.get).toHaveBeenCalledWith('/api/conversations/conversation-1/messages');
  });

  it('connects with the bearer token and forwards incoming messages', () => {
    authService.getToken.and.returnValue('jwt-token');
    let received: MessageDto | undefined;
    service.messages$.subscribe((message) => (received = message));

    service.connect('conversation-1');

    expect(client.configure).toHaveBeenCalledWith(
      jasmine.objectContaining({
        connectHeaders: { Authorization: 'Bearer jwt-token' },
        reconnectDelay: 5000,
      }),
    );
    expect(client.activate).toHaveBeenCalled();
    const subscribeSpy = client.stompClient.subscribe as jasmine.Spy;
    expect(subscribeSpy).toHaveBeenCalledWith(
      '/topic/conversations/conversation-1',
      jasmine.any(Function),
    );
    subscribeSpy.calls.mostRecent().args[1](frame);
    expect(received).toEqual(message);
  });

  it('does not reconnect to the already active conversation', () => {
    authService.getToken.and.returnValue('jwt-token');

    service.connect('conversation-1');
    client.configure.calls.reset();
    service.connect('conversation-1');

    expect(client.configure).not.toHaveBeenCalled();
  });

  it('disconnects before switching conversations and publishes messages', () => {
    authService.getToken.and.returnValue('jwt-token');
    service.connect('conversation-1');
    client.deactivate.calls.reset();

    service.connect('conversation-2');
    service.send('conversation-2', 'Hi there');

    expect(client.deactivate).toHaveBeenCalled();
    expect(client.publish).toHaveBeenCalledWith({
      destination: '/app/chat.send',
      body: JSON.stringify({
        conversationId: 'conversation-2',
        content: 'Hi there',
      }),
    });
  });

  it('deactivates the client when disconnected', () => {
    service.disconnect();

    expect(client.deactivate).toHaveBeenCalled();
  });
});
