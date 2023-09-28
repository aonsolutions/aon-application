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
  lenghtTitle   : number = 30;
  lenghtMensage : number = 50;
  headerTable: any = {};
  bodyTable: any[] = [];
  messageTotal: string = '0';
  selectedMessage: IMessage | null = null;
  messages: ICollection<IMessage> =
  new CollectionFactory().createMessageCollection();
  displayedColumns: string[] = [
    'nameIcon',
    'name',
    'statusIcon',
    'status',
    'title',
    'description',
    'total',
    'date',
    'action'
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
      nameIcon    : 'nameIcon',
      name        : 'Name',
      status      : 'Status',
      title       : 'Title',
      description : 'Description',
      total       : 'Total',
      date        : 'Date',
      action      : 'Action'
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
            column                = Object.assign({}, message);
            const lowerCaseStatus = message.Status.toLowerCase();
            // Leido o no leido
            column.class = 'border-red';
            tableRow.push(column);
            // key
            column.key            = messageKey;
            // Icono del mensaje
            switch (message.Status) {
              case 'abierta':
              case 'cerrada':
                column.nameIcon = {
                  icon: [{ speaker_notes: 'red' }],
                  text: ''
                };
                break;
              case 'pendiente':
              case 'realizada':
                column.nameIcon = {
                  icon: [{ playlist_add_check: 'red' }],
                  text: ''
                };
                break;
              case 'nueva':
              case 'vista':
                column.nameIcon = {
                  icon: [{ notifications: 'red' }],
                  text: ''
                };
                break;
            }
            // Nombre del asesor
            column.name           = message.Name;
            // Icono del ultimo en contestar
            column.statusIcon = {
              icon: lowerCaseStatus.includes('abierta')
                ? [{ reply_all: 'green' }]
                : [],
              text: '',
            };
            column.status = {
              icon: [],
              text: "<span class='background-text-red-light'>" + message.Status + '</span>',
            };
            // Asunto del mensaje
            column.title = message.Title.length > this.lenghtTitle ? message.Title.substring(0, this.lenghtTitle) + '...' : message.Title;
            // Mensaje
            column.description = message.Description.length > this.lenghtMensage ? message.Description.substring(0, this.lenghtMensage) + '...'  : message.Description;
            // Total de respuesta de una consulta
            column.total = message.Type === 'consulta' ?
              "<span class='circle green'>" + this.messageTotal + '</span>'
              :
              '';
            // Fecha
            column.date = datepipe.transform(message.Date, 'EEEE, HH:mm');
            // Abrir o cerrar
            column.action = {
              icon: lowerCaseStatus.includes('abierta')
                ? [{ archive: 'grey' }]
                : [],
            };
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
