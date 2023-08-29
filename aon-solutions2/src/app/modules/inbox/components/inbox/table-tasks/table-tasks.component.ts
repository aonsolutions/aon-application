import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { CollectionFactory, ICollection, IMessage } from 'libraries/AonSDK/aon';
import { Observable } from 'rxjs';
import { MessageService } from 'src/app/core/services/message.service';
import { DatePipe } from '@angular/common';

@Component({
  selector: 'app-table-tasks',
  templateUrl: './table-tasks.component.html',
  styleUrls: ['./table-tasks.component.scss'],
})
export class TableTasksComponent implements OnInit {
  @Input() filterStatus: string[] = [];
  @Input() filter: any = {};
  @Input() public messageList: Observable<ICollection<IMessage>> | undefined;


  selectedMessage: IMessage | undefined;
  headerTable: any = {};
  bodyTable: any[] = [];
  totalMessages: number = 0;
  @Output() noPendingTasks: EventEmitter<boolean> = new EventEmitter<boolean>();

  messages: ICollection<IMessage> =
    new CollectionFactory().createMessageCollection();

  displayedColumns: string[] = [
    'name',
    'status',
    'title',
    'description',
    'date',
  ];

  constructor(private messageService: MessageService) {
    let tableRow: any[] = [];

    this.headerTable = {
      name: 'Name',
      status: 'Status',
      title: 'Title',
      description: 'Description',
      date: 'Date',
    };

    messageService.getMessageList(this.filter).then((response) => {
      let pendingTasksFound = false;

      response.forEach((message, messageKey) => {
        const column: any = Object.assign({}, message);
        column.key = messageKey;
        column.name = message.Name;

        if (
          !this.filterStatus.length ||
          this.filterStatus.includes(message.Status.toLowerCase())
        ) {
          const lowerCaseStatus = message.Status.toLowerCase();
          column.status = {
            icon: lowerCaseStatus.includes('realizada') ? [{}] : [],
            text: `<span class="${
              lowerCaseStatus.includes('pendiente')
                ? 'background-text-red-light'
                : 'background-text-griss-light'
            }">${message.Status}</span>`,
          };
          column.title = message.Title;
          column.description = message.Description;

          const datepipe: DatePipe = new DatePipe('en-US');
          column.date = datepipe.transform(message.Date, 'EEEE, HH:mm');

          tableRow.push(column);

          if (message.Status.toLowerCase().includes('pendiente')) {
            pendingTasksFound = true;
          }
        }
      });
      this.bodyTable = tableRow;

      this.noPendingTasks.emit(!pendingTasksFound);
    });
  }


  ngOnInit(): void {
    if (this.messageList) {
      this.messageList.subscribe((messages) => {
        this.messages = messages;
        this.totalMessages = this.messages.size();
      });
    }
  }


  rowClick(message: any) {

  }
}
