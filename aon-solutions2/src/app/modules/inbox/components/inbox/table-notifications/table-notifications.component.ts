import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { CollectionFactory, ICollection, IMessage } from 'libraries/AonSDK/aon';
import { Observable } from 'rxjs';
import { MessageService } from 'src/app/core/services/message.service';
import { DatePipe } from '@angular/common'

@Component({
  selector: 'app-table-notifications',
  templateUrl: './table-notifications.component.html',
  styleUrls: ['./table-notifications.component.scss']
})
export class TableNotificationsComponent implements OnInit {

  @Input() filterStatus: string[] = [];
  @Input() public messageList: Observable<ICollection<IMessage>> | undefined;

  headerTable: any = {};
  bodyTable: any[] = [];
  totalMessages: number = 0;

  messages: ICollection<IMessage> = new CollectionFactory().createMessageCollection();

  displayedColumns: string[] = ['name', 'status', 'title', 'description', 'date'];

  constructor(
    private messageService: MessageService,
  ) {
    let tableRow: any[] = [];
    let column: any = {};

    this.headerTable = {
      name: 'Name',
      status: 'Status',
      title: 'Title',
      description: 'Description',
      date: 'Date',
    };

      messageService
      .getMessageList()
      .then((response) => {
      response.forEach((message, messageKey) => {

        const column: any = Object.assign({}, message);

        column.key = messageKey;
        column.name = message.Name;

        if (!this.filterStatus.length || this.filterStatus.includes(message.Status.toLowerCase())) {
          const lowerCaseStatus = message.Status.toLowerCase();
          console.log(lowerCaseStatus)
          column.status = {
            icon: lowerCaseStatus.includes('nueva') ? [{ }] : [],
            text: `<span class="${lowerCaseStatus.includes('nueva') ? 'background-text-red-light' : 'background-text-griss-light'}">${message.Status}</span>`
          };
          column.title = message.Title;
          column.description = message.Description;
          const datepipe: DatePipe = new DatePipe('en-US');
          column.date = datepipe.transform(message.Date, 'EEEE, HH:mm');

          tableRow.push(column);
        }
      });

      this.bodyTable = tableRow;
    });
  }


  functionHome: any = (result: any) => this.afterModalClosed(result);
  afterModalClosed(result?: any) {}

  ngOnInit(): void {
    if (this.messageList) {
      this.messageList.subscribe((messages) => {
        this.messages = messages;
        this.totalMessages = this.messages.size();
      });
    }
  }


}
