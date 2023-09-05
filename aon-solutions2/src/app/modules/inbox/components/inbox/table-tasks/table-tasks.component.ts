import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { CollectionFactory, FilterBuilder, ICollection, IMessage } from 'libraries/AonSDK/aon';
import { BehaviorSubject, Observable } from 'rxjs';
import { MessageService } from 'src/app/core/services/message.service';
import { DatePipe } from '@angular/common';
import { TranslateService } from '@ngx-translate/core';

@Component({
  selector: 'app-table-tasks',
  templateUrl: './table-tasks.component.html',
  styleUrls: ['./table-tasks.component.scss'],
})
export class TableTasksComponent implements OnInit {
  @Input() filterStatus: string[] = [];
  @Input() filter: any = {};
  @Input() public messageList: Observable<ICollection<IMessage>> | undefined;
  @Output() rowClicked: EventEmitter<IMessage> = new EventEmitter<IMessage>();
  @Input() id: number = 0;

  selectedMessage: IMessage | null = null;
  bodyTable: any[] = [];
  showDetail: boolean = false;
  totalMessages: number = 0;
  @Output() noPendingTasks: EventEmitter<boolean> = new EventEmitter<boolean>();
  messages: ICollection<IMessage> =
  new CollectionFactory().createMessageCollection();

    public collectionFactory = new CollectionFactory();
    //Inbox area
    messagess: ICollection<IMessage> =
      this.collectionFactory.createMessageCollection();
    private messagesSubject = new BehaviorSubject<ICollection<IMessage>>(
      this.collectionFactory.createMessageCollection()
    );
    public messages$ = this.messagesSubject.asObservable();

  displayedColumns: string[] = [
    'name',
    'status',
    'title',
    'description',
    'date',
  ];

  constructor(
    private messageService: MessageService,
    private translateService: TranslateService,
    ) {}

    headerTable: any = {
      name: 'Name',
      status: 'Status',
      title: 'Title',
      description: 'Description',
      date: 'Date',
    };

    private updateTableData() {
      const datepipe: DatePipe = new DatePipe(this.translateService.getDefaultLang());
      let filterBuilder = new FilterBuilder();
      if (this.id !== 0) {
        filterBuilder.addField('id', this.id);
      }
      let tableRow: any[] = [];

     this.messageService
     .getMessageList(filterBuilder.getFilter())
     .then((response) => {
      this.messagess = response;
      this.messagesSubject.next(this.messagess);
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
          column.date = datepipe.transform(message.Date, 'EEEE, HH:mm');
          column.class = (message.Status == 'pendiente') ? 'border-red' : '';
          tableRow.push(column);


          if (message.Status.toLowerCase().includes('pendiente')) {
            pendingTasksFound = true;
          }

          if (this.selectedMessage === null) {
            this.selectedMessage = { ...message };
          }
        }
      });
      this.bodyTable = tableRow;

      this.noPendingTasks.emit(!pendingTasksFound);
    });
  }

  ngOnInit(): void {
    this.updateTableData();
    if (this.messageList) {
      this.messageList.subscribe((messages) => {
        this.messages = messages;
        this.totalMessages = this.messages.size();
      });
    }
  }


  rowClick(message: any) {
    this.rowClicked.emit(message);
  }
}
