import { Component, Input, OnInit } from '@angular/core';
import { Observable } from 'rxjs';
import { CollectionFactory, ICollection, IMessage } from 'libraries/AonSDK/aon';

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
  @Input() public messageList: Observable<ICollection<IMessage>> | undefined;

  messages: ICollection<IMessage> = new CollectionFactory().createMessageCollection();
  totalMessages: number = 0;

  constructor() {}

  ngOnInit(): void {
    if (this.messageList) {
      this.messageList.subscribe((messages) => {
        this.messages = messages;

        this.totalMessages = this.messages.size();
      });
    }
  }


}
