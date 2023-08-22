import { Component, Input, OnInit } from '@angular/core';
import { CollectionFactory, ICollection, IMessage } from 'libraries/AonSDK/aon';
import { Observable } from 'rxjs';
import { MessageService } from 'src/app/core/services/message.service';
import { DatePipe } from '@angular/common'

@Component({
  selector: 'app-table-queries',
  templateUrl: './table-queries.component.html',
  styleUrls: ['./table-queries.component.scss']
})
export class TableQueriesComponent implements OnInit {
  @Input() filterStatus: string[] = [];
  @Input() filter: any = {};
  @Input() public messageList: Observable<ICollection<IMessage>> | undefined;

  headerTable: any = {};
  bodyTable: any[] = [];
  totalMessages: number = 0;

  messages: ICollection<IMessage> = new CollectionFactory().createMessageCollection();

  displayedColumns: string[] = [
    'name', 'status', 'title', 'description', 'date', 'action'
  ];

  constructor(
    private messageService: MessageService,
  ) {
    let tableRow: any[] = [];

    this.headerTable = {
      name: 'Name',
      status: 'Status',
      title: 'Title',
      description: 'Description',
      date: 'Date',
      action: 'Action'
    };

    messageService
    .getMessageList(this.filter)
    .then((response) => {

      response.forEach((message, messageKey) => {
        const column: any = Object.assign({}, message);
        column.key = messageKey;
        column.name = message.Name;

        if (!this.filterStatus.length || this.filterStatus.includes(message.Status.toLowerCase())) {
          const lowerCaseStatus = message.Status.toLowerCase();
          column.status = {
            icon: lowerCaseStatus.includes('abierta') ? [{ reply_all: 'green' }] : [],
            text: `<span class="${lowerCaseStatus.includes('abierta') ? 'background-text-red-light' : 'background-text-griss-light'}">${message.Status}</span>`
          };
          column.title = message.Title;
          column.description = message.Description;

          const datepipe: DatePipe = new DatePipe('en-US');
          column.date = datepipe.transform(message.Date, 'EEEE, HH:mm');
         column.action = {
            icon: lowerCaseStatus.includes('abierta') ? [{ archive: 'grey' }] : [{ replay: 'grey' }]
          };

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

  modalClick(object: any) {
    console.log(object);
    console.log(object.keyButton);


    this.messageService.getMessage(object.key).then((response) => {});
  }

}

