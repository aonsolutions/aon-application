import { Component, OnInit } from '@angular/core';
import { Message } from './../../../../core/models/class/message';
import { MessageService } from './../../../../core/services/message.service';

@Component({
  selector: 'app-inbox',
  templateUrl: './inbox-dashboard.component.html',
  styleUrls: ['./inbox-dashboard.component.scss'],
  host: {
    '[style.width]': "'100%'",
    '[style.height]': "'100%'",
  },
})
export class InboxDashboardComponent implements OnInit {

  messages: Message[] = [];

  totalMessages: number = 0;


  constructor(private messageService: MessageService) {}

  ngOnInit(): void {
    this.loadMessages();
  }

  private loadMessages(): void {
    this.messageService.getMessageList().then((response: Message[]) => {
      this.messages = response.map((message: Message) => {
        message.Status = this.getStatusByType(message.Type);
        return message;

      });
      this.totalMessages = this.messages.length;
    });
  }

  private getStatusByType(type: string): string {
    switch (type) {
      case 'consulta':
        return 'abierta';
      case 'tarea':
        return 'pendiente';
      case 'notificacion':
        return 'nueva';
      default:
        return '';
    }
  }
}
