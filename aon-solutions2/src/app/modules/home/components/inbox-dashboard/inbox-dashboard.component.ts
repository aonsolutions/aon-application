import { Component, Input, OnInit } from '@angular/core';
import { Message } from './../../../../core/models/class/message';
import { MessageService } from './../../../../core/services/message.service';
import { Observable } from 'rxjs';

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
  @Input() public messageList: Observable<Message[]> | undefined;

  messages: Message[] = [];
  totalMessages: number = 0;

  constructor() {}

  ngOnInit(): void {
    if (this.messageList) {
      this.messageList.subscribe((messages) => {
        this.messages = messages;

        this.totalMessages = this.messages.length;
      });
    }
  }

  // private getStatusByType(type: string): string {
  //   switch (type) {
  //     case 'consulta':
  //       return 'abierta';
  //     case 'tarea':
  //       return 'pendiente';
  //     case 'notificacion':
  //       return 'nueva';
  //     default:
  //       return '';
  //   }
  // }

  // getTypeByMessage(message: Message): number {
  //   switch (message.Type) {
  //     case 'consulta':
  //       return 1;
  //     case 'tarea':
  //       return 2;
  //     case 'notificacion':
  //       return 3;
  //     default:
  //       return 0;
  //   }
  // }
}
