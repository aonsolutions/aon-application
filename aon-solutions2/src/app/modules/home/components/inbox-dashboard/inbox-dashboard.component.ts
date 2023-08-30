import { Component, Input, OnInit } from '@angular/core';
import { DatePipe } from '@angular/common';
import { TranslateService } from '@ngx-translate/core';
//import { CollectionFactory, ICollection, IMessage } from 'libraries/AonSDK/aon';
import { MessageService } from 'src/app/core/services/message.service';
import { FilterBuilder } from 'libraries/AonSDK/aon';

@Component({
  selector    : 'app-inbox',
  templateUrl : './inbox-dashboard.component.html',
  styleUrls   : ['./inbox-dashboard.component.scss']
})
export class InboxDashboardComponent implements OnInit {
  headerTable: any = {};
  bodyTable: any[] = [];
  displayedColumns: string[] = [
    'name',
    'status',
    'title',
    'description',
    'date',
    'action',
  ];
  
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

    let filterBuilder = new FilterBuilder();
//    filterBuilder.getFilter(
//      [{'status', 'abierta'}]
//    );
    filterBuilder.addField('status', 'nueva');
    filterBuilder.addField('status', 'pendiente');

    messageService.getMessageList(filterBuilder.getFilter()).then((response) => {
      response.forEach((message, messageKey) => {
        column = Object.assign({}, message);
        column.key = messageKey;
        column.name = message.Name;

        const lowerCaseStatus = message.Status.toLowerCase();
        if (
          lowerCaseStatus.includes('abierta') || lowerCaseStatus.includes('nueva') || lowerCaseStatus.includes('pendiente')
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
          column.date = datepipe.transform(message.Date, 'EEEE, HH:mm');
          column.action = {
            icon: lowerCaseStatus.includes('abierta') ? [{ archive: 'grey' }] : []
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

  ngOnInit(): void {

  }

  rowClick(object: any) {
    // Fila de la tabla que se esta usando
    // Boton que ha sido clickeado
    console.log(object);
  }

}
