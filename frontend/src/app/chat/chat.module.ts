import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ChatComponent } from './chat.component';
import { ChatRoutingModule } from './chat-routing.module';

@NgModule({
  declarations: [ChatComponent],
  imports: [CommonModule, FormsModule, ChatRoutingModule],
})
export class ChatModule {}
