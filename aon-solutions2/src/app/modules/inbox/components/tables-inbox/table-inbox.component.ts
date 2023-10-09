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
  FilterBuilder,
  ICollection,
  IMessage,
} from 'libraries/AonSDK/src/aon';
import { BehaviorSubject, Observable } from 'rxjs';
import { MessageService } from 'src/app/core/services/message.service';

@Component({
  selector: 'app-table-inbox',
  templateUrl: './table-inbox.component.html',
  styleUrls: ['./table-inbox.component.scss'],
})
export class TablesInboxComponent implements OnChanges {
  filterStatus: string[] = ['todas', 'pendiente', 'realizada', 'nueva',
'vista', 'cerrada', 'abierta'
];
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
  selectedMessage: IMessage | null = null;
  bodyTable: any[] = [];
  showDetail: boolean = false;
  totalMessages: number = 0;
  message: string = '';
  lenghtTitle: number = 30;
  lenghtMensage: number = 50;

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
    console.log('estring',this.selectedTab.toString());

     console.log('filterTabSelec:', this.filterTabSelec);

    // console.log('filterDate:', this.filterDate);
    // console.log('selectedTab:', this.selectedTab);
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
      'inbox': '',
      'status': ''
    };

    switch (this.selectedTab.toString()) {
      case '1':
        type.inbox = 'consulta'
        switch (this.filterTabSelec) {
          case 1:
            type.status = 'abierta';
            break;
          case 2:
            type.status = 'cerrada';
            break;
        }
        break;
      case '2' :
        type.inbox = 'tarea'
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
        type.inbox = 'notificacion'
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
    const month = startDate.getMonth() + 1;
    startDate.setDate(1);
    return this.formatDate(startDate);
  }

  // Filtros fechas final mes
  private getEndDateOfMonth(): string {
    const endDate = new Date();
    endDate.setMonth(endDate.getMonth() + 1);
    const month = endDate.getMonth() === 0 ? 12 : endDate.getMonth();
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
      filterBuilder.addInterval('date', dates.start, dates.end);
    }

    // Obtener la lista de mensajes
    this.messageService
      .getMessageList(filterBuilder.getFilter())
      .then((response) => {
        this.messagess = response;
        this.messagesSubject.next(this.messagess);
        let pendingItemsFound = false;
console.log(1, response);

        response.forEach((message, messageKey) => {
          const column: any = Object.assign({}, message);
          column.key = messageKey;
          column.name = message.Name;
          const lowerCaseStatus = message.Status.toLowerCase();

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
          console.log(message.Status);

            column.statusIcon = {
              icon: message.Status.toLowerCase().includes('abierta')
                ? [{ reply_all: 'green' }]
                : [],
              text: "",
            };
            // Marcar como nueva
            column.action = {
              icon: lowerCaseStatus.includes('abierta')
                ? [{ archive: 'grey' }]
                : [{ replay: 'grey' }],
            };
            column.status = {
              icon: [],
              text:
              `<span class="${message.Status.toLowerCase() === 'cerrada' ? 'background-text-griss-light' : 'background-text-red-light'}">` +
              message.Status +
              '</span>',
            };
          } else if (message.Type === 'notificacion') {
            console.log(message.Status);

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
          column.title = message.Title;
          // Mensaje
          column.description = message.Description;
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
      });
  }

  functionHome: any = (result: any) => this.afterModalClosed(result);
  afterModalClosed(result?: any) {}

  rowClick(message: any) {
    this.rowClicked.emit(message);
  }
}
