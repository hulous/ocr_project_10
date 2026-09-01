import { Component } from '@angular/core';

@Component({
  selector: 'app-root',
  template: `
    <div class="app-shell">
      <header class="app-header">
        <h1>Your Car Your Way — PoC Tchat</h1>
        <p>Application Angular minimale pour la preuve de concept tchat.</p>
      </header>
      <main class="app-card">
        <router-outlet></router-outlet>
      </main>
    </div>
  `,
})
export class AppComponent {}
