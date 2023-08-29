import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import {
  CollectionFactory,
  FilterBuilder,
  ICollection,
  IMessage,
} from 'libraries/AonSDK/aon';
import { BehaviorSubject, Observable } from 'rxjs';
import { MessageService } from 'src/app/core/services/message.service';
import { DatePipe } from '@angular/common';

@Component({
  selector: 'app-table-queries',
  templateUrl: './table-queries.component.html',
  styleUrls: ['./table-queries.component.scss'],
})
export class TableQueriesComponent implements OnInit {
  @Input() filterStatus: string[] = [];
  @Input() filter: any = {};
  @Input() public messageList: Observable<ICollection<IMessage>> | undefined;
  @Output() messageTitleSelected: EventEmitter<string> = new EventEmitter<string>();
  @Output() rowClicked: EventEmitter<IMessage> = new EventEmitter<IMessage>();
  @Input() id: number = 0;

  bodyTable: any[] = [];
  showDetail: boolean = false;
  totalMessages: number = 0;
  selectedMessage: IMessage | null = null;
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
    'action',
  ];

  constructor(public messageService: MessageService) {}
  headerTable: any = {
    name: 'Name',
    status: 'Status',
    title: 'Title',
    description: 'Description',
    date: 'Date',
    action: 'Action',
  };

  ngOnInit(): void {
    this.updateTableData();
    if (this.messageList) {
      this.messageList.subscribe((messages) => {
        this.messages = messages;
        this.totalMessages = this.messages.size();
      });
    }
  }

  private updateTableData() {
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
              icon: lowerCaseStatus.includes('abierta')
                ? [{ reply_all: 'green' }]
                : [],
              text: `<span class="${
                lowerCaseStatus.includes('abierta')
                  ? 'background-text-red-light'
                  : 'background-text-griss-light'
              }">${message.Status}</span>`,
            };
            column.title = message.Title;
            column.description = message.Description;

            const datepipe: DatePipe = new DatePipe('en-US');
            column.date = datepipe.transform(message.Date, 'EEEE, HH:mm');
            column.action = {
              icon: lowerCaseStatus.includes('abierta')
                ? [{ archive: 'grey' }]
                : [{ replay: 'grey' }],
            };

            tableRow.push(column);

            if (this.selectedMessage === null) {
              this.selectedMessage = { ...message };
            }
          }
        });

        this.bodyTable = tableRow;
      });
  }

  functionHome: any = (result: any) => this.afterModalClosed(result);
  afterModalClosed(result?: any) {}

  rowClick(message: any) {
    this.rowClicked.emit(message);
  }
}
