import { ActivatedRoute } from '@angular/router';
import { Subject, of } from 'rxjs';
import { ChatComponent } from './chat.component';
import { ChatService, MessageDto } from '../core/services/chat.service';

describe('ChatComponent', () => {
  let component: ChatComponent;
  let messagesSubject: Subject<MessageDto>;
  let chatService: jasmine.SpyObj<ChatService>;
  const historyMessage: MessageDto = {
    id: 'history-1',
    conversationId: 'conversation-1',
    senderEmail: 'agent@example.com',
    content: 'Welcome',
    sentAt: '2026-01-01T12:00:00.000Z',
  };

  beforeEach(() => {
    messagesSubject = new Subject<MessageDto>();
    chatService = jasmine.createSpyObj('ChatService', [
      'loadHistory',
      'connect',
      'send',
      'disconnect',
    ]);
    chatService.messages$ = messagesSubject.asObservable();
    chatService.loadHistory.and.returnValue(of([historyMessage]));
    const route = {
      snapshot: { paramMap: { get: () => 'conversation-1' } },
    } as unknown as ActivatedRoute;
    component = new ChatComponent(route, chatService);
  });

  it('loads history, connects, and accepts messages for the active conversation', () => {
    component.ngOnInit();
    messagesSubject.next({
      ...historyMessage,
      id: 'live-1',
      content: 'Live message',
    });
    messagesSubject.next({
      ...historyMessage,
      conversationId: 'other',
      id: 'ignored',
    });

    expect(chatService.loadHistory).toHaveBeenCalledWith('conversation-1');
    expect(chatService.connect).toHaveBeenCalledWith('conversation-1');
    expect(component.messages.length).toBe(2);
    expect(component.messages[1].text).toBe('Live message');
  });

  it('uses the demo conversation when the route has no id', () => {
    const route = {
      snapshot: { paramMap: { get: () => null } },
    } as unknown as ActivatedRoute;
    component = new ChatComponent(route, chatService);

    component.ngOnInit();

    expect(chatService.loadHistory).toHaveBeenCalledWith('demo');
    expect(chatService.connect).toHaveBeenCalledWith('demo');
  });

  it('sends trimmed drafts and clears them', () => {
    component.conversationId = 'conversation-1';
    component.draft = '  Hello  ';

    component.sendMessage();

    expect(chatService.send).toHaveBeenCalledWith('conversation-1', 'Hello');
    expect(component.draft).toBe('');
  });

  it('ignores empty drafts and disconnects on destroy', () => {
    component.draft = '   ';

    component.sendMessage();
    component.ngOnDestroy();

    expect(chatService.send).not.toHaveBeenCalled();
    expect(chatService.disconnect).toHaveBeenCalled();
  });
});
