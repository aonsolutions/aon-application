import { Component, Input, OnInit } from '@angular/core';
import { DatePipe } from '@angular/common';
import { TranslateService } from '@ngx-translate/core';
import { MessageService } from 'src/app/core/services/message.service';
import { FilterBuilder } from 'libraries/AonSDK/src/aon';
import { Router } from '@angular/router';
import { MessageChatService } from 'src/app/core/services/message-chat.service';

@Component({
  selector: 'app-inbox',
  templateUrl: './inbox-dashboard.component.html',
  styleUrls: ['./inbox-dashboard.component.scss'],
})
export class InboxDashboardComponent implements OnInit {
  routerlink: string = '/inbox';
  bodyTable: any[] = [];
  displayedColumns: string[] = [
    'name',
    'statusIcon',
    'status',
    'title',
    'description',
    'total',
    'date',
    'actions',
  ];
  messageTotal: string = '0';
  totalMessages: string = '0';
  constructor(
    private translateService: TranslateService,
    private messageService: MessageService,
    private messageChatService: MessageChatService,
    private router: Router
    ) {}

    headerTable: any = {
      name: 'Name',
      status: 'Status',
      title: 'Title',
      description: 'Description',
      total: 'Total',
      date: 'Date',
      actions: 'Actions',
    };

    ngOnInit(): void {
      this.tableData();
    }

  // Mensajes - datos de la tabla
  private tableData() {
    let tableRow: any[] = [];
    const datepipe: DatePipe = new DatePipe(
      this.translateService.getDefaultLang()
    );
    let filterBuilder = new FilterBuilder();
    filterBuilder.setPageNumAndItems(1, 4);
    filterBuilder.addField('status', 'abierta');
    filterBuilder.addField('status', 'nueva');
    filterBuilder.addField('status', 'pendiente');

    this.messageService
      .getMessageList(filterBuilder.getFilter())
      .then((response) => {
        response.forEach((message, messageKey) => {
          const column: any = Object.assign({}, message);
          column.key = messageKey;
          column.name = message.Name;
          column.statusIcon = {
            icon: message.Status.toLowerCase().includes('abierta')
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
          // Asunto del mensaje
          column.title = message.Title;
          // Mensaje
          column.description = `<span class="dashboard-text">${message.Description.substring(0, 50)}...</span>`;
          // Obtener fecha
          column.date = `<span class="dashboard-text">${datepipe.transform(message.Date, 'dd/MM/yyyy, HH:mm')}</span>`;
          // Marcar como cerrado un mensaje
          switch (message.Status) {
            case 'abierta':
              column.actions = ['archive'];
              break;
            default:
              column.actions = [];
              break;
          }
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
          // total de mensajes de chat
          if (message.Status === 'abierta') {
            let FilterBuilderTotal = new FilterBuilder();
            FilterBuilderTotal.addField('idMessage', messageKey);
            this.messageChatService
              .getMessageChatCount(FilterBuilderTotal.getFilter())
              .then((response) => {
                this.totalMessages! =
                  response < 100 ? response.toString() : '+99';

                column.total =
                  "<span class='circle green'>" +
                  this.totalMessages +
                  '</span>';
              });
          }
          column.class = 'border-red';
          tableRow.push(column);
        });
        this.bodyTable = tableRow;
      });

    // Mensajes - total
    let filterBuilderTotal = new FilterBuilder();
    filterBuilderTotal.addField('status', 'abierta');
    filterBuilderTotal.addField('status', 'nueva');
    filterBuilderTotal.addField('status', 'pendiente');
    this.messageService
      .getMessageCount(filterBuilderTotal.getFilter())
      .then((response) => {
        this.messageTotal = response < 100 ? response.toString() : '+99';
      });
  }

  // Método para cambiar el estado de un mensaje consulta
  actionIcon(object: any) {
    this.messageService.getMessage(object.key).then((messageStatus) => {
      switch (object.keyButton) {
        case 'archive':
          this.messageService.archiveMessage(messageStatus);
          this.tableData();
          break;
        default:
      }
    });
  }

  rowClick(object: any) {
    // Fila de la tabla que se esta usando
    // Fila que ha sido clickeado
    // console.log(object);
    this.router.navigate([this.routerlink, object.key]);
    // Cambiar el estado de notificación al hacer click en el mensaje
    this.messageService.getMessage(object.key).then((messageStatus) => {
      if (object.type === 'notificacion') {
        this.messageService.markAsReadNotification(messageStatus);
        this.tableData();
      }
    });
  }
}
