import { Component, Input, OnInit } from '@angular/core';
import { CollectionFactory, ICollection, IMessage } from 'libraries/AonSDK/aon';
import { Observable } from 'rxjs';
import { MessageService } from 'src/app/core/services/message.service';
import { DatePipe } from '@angular/common';

@Component({
  selector: 'app-table-component',
  templateUrl: './table-component.component.html',
  styleUrls: ['./table-component.component.scss'],
})
export class TableComponentComponent implements OnInit {
  @Input() filter: any = {};
  @Input() public messageList: Observable<ICollection<IMessage>> | undefined;

  headerTable: any = {};
  bodyTable: any[] = [];

  messages: ICollection<IMessage> =
    new CollectionFactory().createMessageCollection();

  displayedColumns: string[] = [
    'name',
    'status',
    'title',
    'description',
    'date',
    'action',
  ];

  constructor(private messageService: MessageService) {
    let tableRow: any[] = [];
    let column: any = {};

    this.headerTable = {
      name: 'Name',
      status: 'Status',
      title: 'Title',
      description: 'Description',
      date: 'Date',
      action: 'Action',
    };

    messageService.getMessageList().then((response) => {
      console.log(response);
      response.forEach((message, messageKey) => {
        column = Object.assign({}, message);
        column.key = messageKey;
        column.name = message.Name;

        const lowerCaseStatus = message.Status.toLowerCase();
        if (
          lowerCaseStatus.includes('abierta') ||
          lowerCaseStatus.includes('nueva') ||
          lowerCaseStatus.includes('pendiente')
        ) {
          column.status = {
            icon: lowerCaseStatus.includes('abierta')
              ? [{ reply_all: 'green' }]
              : [],
            text:
              "<span class='background-text-red-light'>" +
              message.Status +
              '</span>',
          };
          column.title = message.Title;
          column.description = message.Description;

          const datepipe: DatePipe = new DatePipe('en-US');
          column.date = datepipe.transform(message.Date, 'EEEE, HH:mm');
          column.action = {
            icon: lowerCaseStatus.includes('abierta')
            ? [{ archive: 'grey' }]
            : []
          };
          switch (message.Status) {
            case 'abierta':
            case 'cerrada':
              column.name = {
                icon: [{ speaker_notes: 'red' }],
                text: message.Name,
              };
              break;
            case 'pendiente':
            case 'realizada':
              column.name = {
                icon: [{ playlist_add_check: 'red' }],
                text: message.Name,
              };
              break;
            case 'nueva':
            case 'vista':
              column.name = {
                icon: [{ notifications: 'red' }],
                text: message.Name,
              };
              break;
          }
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
      });
    }
  }

}
