import { Component, Input, OnInit } from '@angular/core';
import { DatePipe } from '@angular/common';
import { TranslateService } from '@ngx-translate/core';
import { MessageService } from 'src/app/core/services/message.service';
import { FilterBuilder } from 'libraries/AonSDK/src/aon';

@Component({
  selector    : 'app-inbox',
  templateUrl : './inbox-dashboard.component.html',
  styleUrls   : ['./inbox-dashboard.component.scss']
})
export class InboxDashboardComponent implements OnInit {
  routerlink      : string = '/inbox';
  headerTable     : any = {};
  bodyTable       : any[] = [];
  displayedColumns: string[] = [
    'name',
    'statusIcon',
    'status',
    'title',
    'description',
    'date',
    'action',
  ];
  messageTotal : string = '0';

  constructor(
    private translateService: TranslateService,
    private messageService : MessageService
  ) {
    let tableRow  : any[] = [];
    let column    : any = {};
    const datepipe: DatePipe = new DatePipe(this.translateService.getDefaultLang());

    this.headerTable = {
      name        : 'Name',
      status      : 'Status',
      title       : 'Title',
      description : 'Description',
      date        : 'Date',
      action      : 'Action',
    };

    // Mensajes - datos de la tabla
    let filterBuilder = new FilterBuilder();
    filterBuilder.setPageNumAndItems(1, 4);
//    filterBuilder.addField('status', 'abierta');
//    filterBuilder.addField('status', 'nueva');
//    filterBuilder.addField('status', 'pendiente');
    messageService.getMessageList(filterBuilder.getFilter()).then((response) => {
      response.forEach((message, messageKey) => {
        column = Object.assign({}, message);
        column.key = messageKey;
        column.name = message.Name;
        column.statusIcon = {
          icon: message.Status.toLowerCase().includes('abierta')
            ? [{ reply_all: 'green' }]
            : [],
          text:"",
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
        column.date = datepipe.transform(message.Date, 'EEEE, HH:mm');
        column.action = {
          icon: message.Status.toLowerCase().includes('abierta') ? [{ archive: 'grey' }] : []
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
      });
      this.bodyTable = tableRow;
    });

    // Mensajes - total
    let filterBuilderTotal = new FilterBuilder();
//    filterBuilderTotal.addField('status', 'abierta');
//    filterBuilderTotal.addField('status', 'nueva');
//    filterBuilderTotal.addField('status', 'pendiente');
    messageService.getMessageCount(filterBuilderTotal.getFilter()).then((response) => {
      this.messageTotal = response < 100 ? response.toString() : '+99';
    });
  }

  ngOnInit(): void {

  }

  rowClick(object: any) {
    // Fila de la tabla que se esta usando
    // Fila que ha sido clickeado
    console.log(object);
  }

}
