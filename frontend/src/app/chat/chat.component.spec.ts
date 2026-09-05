import { ActivatedRoute } from "@angular/router";
import { Subject, of } from "rxjs";
import { ChatComponent } from "./chat.component";
import { ChatService, MessageDto } from "../core/services/chat.service";
import { TestBed } from "@angular/core/testing";

describe("ChatComponent", () => {
  let component: ChatComponent;
  let messagesSubject: Subject<MessageDto>;
  let chatService: jasmine.SpyObj<ChatService>;
  const historyMessage: MessageDto = {
    id: "history-1",
    conversationId: "conversation-1",
    senderEmail: "agent@example.com",
    content: "Welcome",
    sentAt: "2026-01-01T12:00:00.000Z",
  };

  beforeEach(() => {
    messagesSubject = new Subject<MessageDto>();
    chatService = jasmine.createSpyObj("ChatService", [
      "loadHistory",
      "connect",
      "send",
      "disconnect",
    ]);
    Object.defineProperty(chatService, "messages$", {
      value: messagesSubject.asObservable(),
    });
    chatService.loadHistory.and.returnValue(of([historyMessage]));
    const route = {
      snapshot: { paramMap: { get: () => "conversation-1" } },
    } as unknown as ActivatedRoute;
    TestBed.configureTestingModule({
      providers: [
        { provide: ActivatedRoute, useValue: route },
        { provide: ChatService, useValue: chatService },
      ],
    });
    component = TestBed.runInInjectionContext(() => new ChatComponent());
  });

  it("loads history, connects, and accepts messages for the active conversation", async () => {
    await Promise.resolve();
    messagesSubject.next({
      ...historyMessage,
      id: "live-1",
      content: "Live message",
    });
    messagesSubject.next({
      ...historyMessage,
      conversationId: "other",
      id: "ignored",
    });
    TestBed.flushEffects();
    for (let index = 0; index < 5; index += 1) {
      await Promise.resolve();
      TestBed.flushEffects();
    }

    expect(chatService.loadHistory).toHaveBeenCalledWith("conversation-1");
    expect(chatService.connect).toHaveBeenCalledWith("conversation-1");
    expect(component.messages().length).toBe(2);
    expect(component.messages()[1].text).toBe("Live message");
  });

  it("uses the demo conversation when the route has no id", async () => {
    const route = {
      snapshot: { paramMap: { get: () => null } },
    } as unknown as ActivatedRoute;
    TestBed.resetTestingModule();
    TestBed.configureTestingModule({
      providers: [
        { provide: ActivatedRoute, useValue: route },
        { provide: ChatService, useValue: chatService },
      ],
    });
    component = TestBed.runInInjectionContext(() => new ChatComponent());
    await Promise.resolve();

    expect(chatService.loadHistory).toHaveBeenCalledWith("demo");
    expect(chatService.connect).toHaveBeenCalledWith("demo");
  });

  it("sends trimmed drafts and clears them", () => {
    component.conversationId = "conversation-1";
    component.draft.set("  Hello  ");

    component.sendMessage();

    expect(chatService.send).toHaveBeenCalledWith("conversation-1", "Hello");
    expect(component.draft()).toBe("");
  });

  it("sends the draft when Enter is pressed", () => {
    component.conversationId = "conversation-1";
    component.draft.set("Hello");
    const event = {
      shiftKey: false,
      preventDefault: jasmine.createSpy("preventDefault"),
    } as unknown as KeyboardEvent;

    component.handleEnterKey(event);

    expect(event.preventDefault).toHaveBeenCalled();
    expect(chatService.send).toHaveBeenCalledWith("conversation-1", "Hello");
  });

  it("keeps the newline behavior for Shift+Enter", () => {
    component.draft.set("Hello");
    const event = {
      shiftKey: true,
      preventDefault: jasmine.createSpy("preventDefault"),
    } as unknown as KeyboardEvent;

    component.handleEnterKey(event);

    expect(event.preventDefault).not.toHaveBeenCalled();
    expect(chatService.send).not.toHaveBeenCalled();
  });

  it("ignores empty drafts and disconnects on destroy", () => {
    component.draft.set("   ");

    component.sendMessage();
    TestBed.resetTestingModule();

    expect(chatService.send).not.toHaveBeenCalled();
    expect(chatService.disconnect).toHaveBeenCalled();
  });
});
