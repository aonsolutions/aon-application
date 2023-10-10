import { DatePipe } from '@angular/common';
import {
  Component,
  EventEmitter,
  Input,
  OnChanges,
  Output,
  SimpleChanges,
} from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import {
  CollectionFactory,
  Factory,
  ICollection,
  IMessage,
  StatusMessage,
} from 'libraries/AonSDK/src/aon';
import { FilterBuilder } from 'libraries/AonSDK/src/utils/FilterBuilder';
import { BehaviorSubject, Observable } from 'rxjs';
import { MessageService } from 'src/app/core/services/message.service';
import { MessageChatService } from 'src/app/core/services/message-chat.service';
import { Status } from '../../../../../../libraries/AonSDK/src/interfaces/modelsInterfaces';

@Component({
  selector: 'app-table-inbox',
  templateUrl: './table-inbox.component.html',
  styleUrls: ['./table-inbox.component.scss'],
})
export class TablesInboxComponent implements OnChanges {
  @Input() filterTabSelec: number = 0;
  @Input() filterDate: number = 0;
  @Input() filter: any = {};
  @Input() public messageList: Observable<ICollection<IMessage>> | undefined;
  @Input() id: number = 0;
  @Input() type: string = '';
  @Input() selectedTab: number = 0;
  @Input() tabIndex: number = 0;
  @Output() rowClicked: EventEmitter<IMessage> = new EventEmitter<IMessage>();
  @Output() noPendingItems: EventEmitter<boolean> = new EventEmitter<boolean>();

  newMessageDescription: string = '';
  entityFactory = new Factory();
  messagesData: IMessage = this.entityFactory.createMessage();
  bodyTable: any[] = [];
  showDetail: boolean = false;
  totalMessages: string = '0';
  message: string = '';
  spinner: boolean = true;
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
    private messageChatService: MessageChatService,
    private translateService: TranslateService
  ) {}

  headerTable: any = {
    name: 'Name',
    status: 'Status',
    title: 'Title',
    description: 'Description',
    total: 'Total',
    date: 'Date',
    action: 'Action',
  };

  ngOnChanges(changes: SimpleChanges): void {
    this.updateTableData();
  }

  // Casos de fechas para usar en mi tabla
  filterTableDate() {
    let date: { start: string; end: string } = { start: '', end: '' };
    switch (this.filterDate) {
      case 1:
        date.start = this.getStartDateOfWeek();
        date.end = this.getEndDateOfWeek();
        break;
      case 2:
        date.start = this.getStartDateOfMonth();
        date.end = this.getEndDateOfMonth();
        break;
    }
    return date;
  }

  /**
   * Esta función filtra el tipo según la pestaña seleccionada.
   *
   * @returns {string} El tipo filtrado.
   */
  private filterType() {
    let type = {
      inbox: '',
      status: '',
      icon: '',
    };

    switch (this.selectedTab.toString()) {
      case '1':
        type.inbox = 'consulta';
        switch (this.filterTabSelec) {
          case 1:
            type.status = 'abierta';
            break;
          case 2:
            type.status = 'cerrada';
            break;
        }
        break;
      case '2':
        type.inbox = 'tarea';
        switch (this.filterTabSelec) {
          case 1:
            type.status = 'pendiente';
            break;
          case 2:
            type.status = 'realizada';
            break;
        }
        break;
      case '3':
        type.inbox = 'notificacion';
        switch (this.filterTabSelec) {
          case 1:
            type.status = 'nueva';
            break;
          case 2:
            type.status = 'vista';
            break;
        }
        break;
    }
    return type;
  }

  // Filtros fechas principio semana
  private getStartDateOfWeek(): string {
    const startDate = new Date();
    const currentDay = startDate.getDay();
    const startDay = currentDay === 0 ? 6 : currentDay - 1;
    startDate.setDate(startDate.getDate() - startDay);
    return this.formatDate(startDate);
  }

  // Filtos fechas final semana
  private getEndDateOfWeek(): string {
    const endDate = new Date();
    const currentDay = endDate.getDay();
    const remainingDays = 7 - currentDay - 1;
    endDate.setDate(endDate.getDate() + remainingDays);
    return this.formatDate(endDate);
  }

  // Filtros fechas principio mes
  private getStartDateOfMonth(): string {
    const startDate = new Date();
    startDate.setDate(1);
    return this.formatDate(startDate);
  }

  // Filtros fechas final mes
  private getEndDateOfMonth(): string {
    const endDate = new Date();
    endDate.setMonth(endDate.getMonth() + 1);
    endDate.setDate(0);
    return this.formatDate(endDate);
  }

  // Filtros fechas
  private formatDate(date: Date): string {
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
  }

  private updateTableData() {
    let tableRow: any[] = [];
    const datepipe: DatePipe = new DatePipe(
      this.translateService.getDefaultLang()
    );

    let filterBuilder = new FilterBuilder();
    if (this.filterType().inbox !== '') {
      filterBuilder.addField('type', this.filterType().inbox);
    }
    if (this.filterType().status !== '') {
      filterBuilder.addField('status', this.filterType().status);
    }
    if (this.filterDate > 0 && this.filterType().inbox !== '') {
      let dates = this.filterTableDate();
      filterBuilder.addInterval(
        'date',
        new Date(dates.start),
        new Date(dates.end)
      );
    }

    // Obtener la lista de mensajes
    this.messageService
      .getMessageList(filterBuilder.getFilter())
      .then((response) => {
        this.messagess = response;
        this.messagesSubject.next(this.messagess);
        let pendingItemsFound = false;

        response.forEach((message, messageKey) => {
          const column: any = Object.assign({}, message);
          column.key = messageKey;
          column.name = message.Name;
          const lowerCaseStatus = message.Status.toLowerCase();

          // Icono del mensaje
          if (this.selectedTab === 0) {
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
          }
          // Lógica específica para cada tipo de mensaje
          if (message.Type === 'tarea') {
            column.status = {
              icon: lowerCaseStatus.includes('realizada') ? [{}] : [],
              text: `<span class="${
                lowerCaseStatus.includes('pendiente')
                  ? 'background-text-red-light'
                  : 'background-text-griss-light'
              }">${message.Status}</span>`,
            };
          } else if (message.Type === 'consulta') {
            // Obtener el total de mensajes
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
            // obtener el status
            column.statusIcon = {
              icon: message.Status.toLowerCase().includes('abierta')
                ? [{ reply_all: 'green' }]
                : [],
              text: '',
            };
            // Marcar como nueva
            // column.action = {
            //   icon: lowerCaseStatus.includes('abierta')
            //     ? [{ archive: 'grey' }]
            //     : [{ replay: 'grey' }],
            // };
            console.log(message.Status);
            switch (message.Status) {
              case 'abierta':
              case 'cerrada':
                column.action = {
                  icon: lowerCaseStatus.includes('abierta')
                    ? [{ archive: 'grey' }]
                    : [{ replay: 'grey' }],
                  status: message.Status,
                };
                break;
              default:
                column.action = [];
                break;
            }
            column.status = {
              icon: [],
              text:
                `<span class="${
                  message.Status.toLowerCase() === 'cerrada'
                    ? 'background-text-griss-light'
                    : 'background-text-red-light'
                }">` +
                message.Status +
                '</span>',
            };
          } else if (message.Type === 'notificacion') {
            column.status = {
              icon: lowerCaseStatus.includes('nueva') ? [{}] : [],
              text: `<span class="${
                lowerCaseStatus.includes('nueva')
                  ? 'background-text-red-light'
                  : 'background-text-griss-light'
              }">${message.Status}</span>`,
            };
          }

          // Asunto del mensaje
          const maxLength = 20;
          if (message.Title.length > maxLength) {
            column.title = message.Title.substring(0, maxLength) + '...';
          } else {
            column.title = message.Title;
          }
          // Mensaje
          column.description = message.Description.substring(0, 50) + '...';
          // Fecha
          column.date = datepipe.transform(message.Date, 'dd/MM/yyyy, HH:mm');
          // Marcar como nueva
          column.class = message.Status == 'pendiente' ? 'border-red' : '';

          // Agregamos el mensaje
          tableRow.push(column);
        });

        this.bodyTable = tableRow;
        this.noPendingItems.emit(!pendingItemsFound);

        // No tenemos mensajes en la tabla
        if (response.size() === 0) {
          this.translateService
            .get([
              'INBOX.NOMESSAGESTHISWEEK',
              'INBOX.NOMESSAGESTHISMONTH',
              'NOMESSAGES',
            ])
            .subscribe((result) => {
              switch (this.filterDate) {
                case 1:
                  this.message = result['INBOX.NOMESSAGESTHISWEEK'];
                  break;
                case 2:
                  this.message = result['INBOX.NOMESSAGESTHISMONTH'];
                  break;
                default:
                  this.message = result['INBOX.NOMESSAGES'];
                  break;
              }
            });
        } else {
          this.message = '';
        }

        this.noPendingItems.emit(!pendingItemsFound);
        // Desactivo el spinner
        this.spinner = false;
      });
  }

  iconAction(message: any) {
    console.log('entra en iconAction');
    console.log(message);
    this.messageService.getMessage(message.key).then((messageStatus) => {
      if (message.Status === 'abierta') {
        this.messageService.archiveMessage(messageStatus).then(() => {
          this.updateTableData();
        });
      } else if (message.Status === 'cerrada') {
        this.messageService.reopenMessage(messageStatus).then(() => {
          this.updateTableData();
        });
      }
    });
  }

  functionHome: any = (result: any) => this.afterModalClosed(result);
  afterModalClosed(result?: any) {}

  rowClick(message: any) {
    this.rowClicked.emit(message);
    this.iconAction(message);
    console.log('sttus', message.Status);
  }
}
