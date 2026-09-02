export interface MessageDto {
  id: string;
  conversationId: string;
  senderEmail: string;
  senderName?: string;
  content: string;
  sentAt: string;
}
