import { Component } from '@angular/core';

interface ChatMessage {
  author: string;
  text: string;
  timestamp: string;
}

@Component({
  selector: 'app-chat',
  template: `
    <section aria-labelledby="chat-title">
      <h2 id="chat-title">Tchat</h2>

      <div class="chat-window" role="log" aria-live="polite" aria-label="Messages du tchat">
        <ng-container *ngIf="messages.length; else emptyState">
          <article class="chat-message" *ngFor="let message of messages">
            <strong>{{ message.author }}</strong>
            <time [attr.datetime]="message.timestamp">{{ message.timestamp }}</time>
            <p>{{ message.text }}</p>
          </article>
        </ng-container>

        <ng-template #emptyState>
          <p>Aucun message pour l'instant. Envoyez le premier message.</p>
        </ng-template>
      </div>

      <form class="chat-form" (ngSubmit)="sendMessage()">
        <label for="messageInput">Message</label>
        <textarea
          id="messageInput"
          name="message"
          rows="3"
          [(ngModel)]="draft"
          required
          aria-required="true"
          placeholder="Tapez votre message ici"
        ></textarea>

        <button type="submit" [disabled]="!draft.trim()">Envoyer</button>
      </form>
    </section>
  `,
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
    ".chat-form button:disabled { background: #94a3b8; cursor: not-allowed; }"
  ]
})
export class ChatComponent {
  draft = '';
  messages: ChatMessage[] = [
    { author: 'Système', text: 'Bienvenue dans le PoC tchat.', timestamp: new Date().toLocaleTimeString('fr-FR') }
  ];

  sendMessage() {
    const content = this.draft.trim();
    if (!content) {
      return;
    }

    this.messages.push({
      author: 'Utilisateur',
      text: content,
      timestamp: new Date().toLocaleTimeString('fr-FR')
    });

    this.draft = '';
  }
}
