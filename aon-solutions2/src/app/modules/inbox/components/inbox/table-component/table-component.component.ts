import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import {
  CollectionFactory,
  FilterBuilder,
  ICollection,
  IMessage,
} from 'libraries/AonSDK/aon';
import { Observable } from 'rxjs';
import { MessageService } from 'src/app/core/services/message.service';
import { DatePipe } from '@angular/common';
import { TranslateService } from '@ngx-translate/core';
import { MessageChatService } from 'src/app/core/services/message-chat.service';


@Component({
  selector: 'app-table-component',
  templateUrl: './table-component.component.html',
  styleUrls: ['./table-component.component.scss'],
})
export class TableComponentComponent implements OnInit {
  @Input() filter: any = {};
  @Input() public messageList: Observable<ICollection<IMessage>> | undefined;
  @Output() rowClicked: EventEmitter<IMessage> = new EventEmitter<IMessage>();

  headerTable: any = {};
  bodyTable: any[] = [];
  messageTotal: string = '0';
  selectedMessage: IMessage | null = null;
  messages: ICollection<IMessage> =
    new CollectionFactory().createMessageCollection();

  displayedColumns: string[] = [
    'name',
    'statusIcon',
    'status',
    'title',
    'description',
    'total',
    'date',
    'action',
  ];

  constructor(
    private messageService: MessageService,
    private translateService: TranslateService,
    private messageChatService: MessageChatService
  ) {
    let tableRow: any[] = [];
    let column: any = {};
    const datepipe: DatePipe = new DatePipe(
      this.translateService.getDefaultLang()
    );

    this.headerTable = {
      name: 'Name',
      status: 'Status',
      title: 'Title',
      description: 'Description',
      total: 'Total',
      date: 'Date',
      action: 'Action',
    };

    // Obtener la lista de mensajes
    this.messageService.getMessageList().then((response) => {
    response.forEach((message, messageKey) => {
        // Mensajes - total
        let filterBuilderTotal = new FilterBuilder();
        filterBuilderTotal.addField('idMessage', messageKey);
        this.messageChatService
          .getMessageChatCount(filterBuilderTotal.getFilter())
          .then((response) => {
            this.messageTotal! = response < 100 ? response.toString() : '+99';
            column = Object.assign({}, message);
            column.key = messageKey;
            column.name = message.Name;

            const lowerCaseStatus = message.Status.toLowerCase();
            column.statusIcon = {
              icon: lowerCaseStatus.includes('abierta')
                ? [{ reply_all: 'green' }]
                : [],
              text: '',
            };
            column.status = {
              icon: [],
              text:
                "<span class='background-text-red-light'>" +
                message.Status +
                '</span>',
            };
            column.title = message.Title;
            column.description = message.Description;
            if (message.Type === 'consulta') {
              column.total =
                "<span class='messageTotal'>" + this.messageTotal + '</span>';
            } else {
              column.total = '';
            }
            column.date = datepipe.transform(message.Date, 'EEEE, HH:mm');

            column.action = {
              icon: lowerCaseStatus.includes('abierta')
                ? [{ archive: 'grey' }]
                : [],
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
            column.class = 'border-red';
            tableRow.push(column);

            if (this.selectedMessage === null) {
              this.selectedMessage = { ...message };
            }
          });
        this.bodyTable = tableRow;
      });
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

  rowClick(message: any) {
    this.rowClicked.emit(message);
  }
}
