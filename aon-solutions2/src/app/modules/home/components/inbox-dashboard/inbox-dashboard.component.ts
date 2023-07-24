import { Component, Input, OnInit } from '@angular/core';
import { Message } from './../../../../core/models/class/message';
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


}
