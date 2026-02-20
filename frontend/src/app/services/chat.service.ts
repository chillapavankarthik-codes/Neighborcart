import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

import { environment } from '../../environments/environment';
import { ChatMessage } from '../models/chat-message.model';

@Injectable({ providedIn: 'root' })
export class ChatService {
  private readonly http = inject(HttpClient);

  getMessages(postId: string): Observable<ChatMessage[]> {
    return this.http.get<ChatMessage[]>(`${environment.apiUrl}/posts/${postId}/chat`);
  }

  sendMessage(postId: string, text: string): Observable<ChatMessage> {
    return this.http.post<ChatMessage>(`${environment.apiUrl}/posts/${postId}/chat`, {
      text
    });
  }
}
