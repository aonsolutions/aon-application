import { DatePipe } from '@angular/common';
import { Component, EventEmitter, Input, OnChanges, Output, SimpleChanges } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { CollectionFactory, Factory, ICollection, IMessage, IMessageChat } from 'libraries/AonSDK/src/aon';
import { FilterBuilder } from 'libraries/AonSDK/src/utils/FilterBuilder';
import { BehaviorSubject, Observable } from 'rxjs';
import { MessageService } from 'src/app/core/services/message.service';
import { MessageChatService } from 'src/app/core/services/message-chat.service';
import { DateFormat, getEndDateOfMonth, getEndDateOfWeek, getStartDateOfMonth, getStartDateOfWeek } from 'src/app/core/utilities/time';

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
  @Input() type: string = '';
  @Input() selectedTab: number = 0;
  @Input() tabIndex: number = 0;
  @Output() rowClicked: EventEmitter<IMessage> = new EventEmitter<IMessage>();
  @Output() noPendingItems: EventEmitter<boolean> = new EventEmitter<boolean>();
  @Output() statusChanged = new EventEmitter<void>();
  @Output() archiveMessageEvent: EventEmitter<IMessage> = new EventEmitter<IMessage>();
  @Input() updateTable: boolean = false;

  public collectionFactory = new CollectionFactory();
  messagesChat: ICollection<IMessageChat> =
  this.collectionFactory.createMessageChatCollection();
  entityFactory = new Factory();
  messagesData: IMessage = this.entityFactory.createMessage();
  dataBody: any[] = [];
  bodyTable: any[] = [];
  showDetail: boolean = false;
  totalMessages: string = '0';
  message: string = '';
  spinner: boolean = true;

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
    'actions',
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
    actions: 'Actions',
  };

  ngOnChanges(changes: SimpleChanges): void {
    this.updateTableData();
  }

  // Casos de fechas para usar en mi tabla
  filterTableDate() {
    let date: { start: string; end: string } = { start: '', end: '' };
    switch (this.filterDate) {
      case 1:
        date.start = getStartDateOfWeek();
        date.end = getEndDateOfWeek();
        break;
      case 2:
        date.start = getStartDateOfMonth();
        date.end = getEndDateOfMonth();
        break;
    }
    return date;
  }


   // Esta función filtra el tipo según la pestaña seleccionada.
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

  // Tabla
  updateTableData() {
    let tableRow: any[] = [];

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
        dates.start,
        dates.end
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
          // tipo tarea
          if (message.Type === 'tarea') {
            column.status = {
              icon: lowerCaseStatus.includes('realizada') ? [{}] : [],
              text: `<span class="${
                lowerCaseStatus.includes('pendiente')
                  ? 'background-text-red-light'
                  : 'background-text-griss-light'
              }">${message.Status}</span>`,
            };
            // tipo consulta
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
            // Marcar como nueva o cerrada
            switch (message.Status) {
              case 'abierta':
                column.actions = ['archive'];
                break;
              case 'cerrada':
                column.actions = ['replay'];
                break;
              default:
                column.actions = [];
                break;
            }
            column.status = {
              icon: [],
              text:
                `<span class="${
                  message.Status.toLowerCase() === 'abierta'
                  ? 'background-text-red-light'
                  : 'background-text-griss-light'
                }">` +
                message.Status +
                '</span>',
            };
            // tipo notificación
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
          column.date = DateFormat(message.Date, 'dd/MM/yyyy, HH:mm');
          // Marcar como nueva
          column.class = ['pendiente', 'abierta', 'nueva'].includes(message.Status) ? 'border-red' : '';
          // Agregamos el mensaje
          tableRow.push(column);
        });
        this.dataBody = tableRow;
        this.bodyTable = tableRow;
        this.noPendingItems.emit(!pendingItemsFound);

        // Tipo de mensajes
        let messageType: string = '';
        if (this.selectedTab === 1) {
          messageType = 'QUERIES';
        } else if (this.selectedTab === 2) {
          messageType = 'TASKS';
        } else if (this.selectedTab === 3) {
          messageType = 'NOTIFICATIONS';
        }
        // No tenemos mensajes en la tabla
        if (response.size() === 0) {
          this.translateService
            .get([
              `INBOX.NO${messageType.toUpperCase()}THISWEEK`,
              `INBOX.NO${messageType.toUpperCase()}THISMONTH`,
              'INBOX.NOMESSAGES',
            ])
            .subscribe((result) => {
              switch (this.filterDate) {
                case 1:
                  this.message = result[`INBOX.NO${messageType.toUpperCase()}THISWEEK`];
                  break;
                case 2:
                  this.message = result[`INBOX.NO${messageType.toUpperCase()}THISMONTH`];
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

  // Método para cambiar el estado de un mensaje consulta
  iconAction(object: any) {
    this.messageService.getMessage(object.key).then((messageStatus) => {
    switch (object.keyButton) {
      case 'archive':
        this.messageService.archiveMessage(messageStatus)
      this.updateTableData();
        break;
      case 'replay':
        this.messageService.reopenMessage(messageStatus)
        this.updateTableData();
        break;
      default:
      }
      this.statusChanged.emit();
    });
  }

  //Función para devolver una cadena de texto sin tildes
  removeAccents(str: string): string {
    return str.normalize('NFD').replace(/[\u0300-\u036f]/g, '');
  }

  // Filtro de busqueda teniendo en cuenta mayúsculas y minúsculas
  searchMessage(search: string) {
    // Si el término de búsqueda está vacío, muestra todos los elementos en la tabla.
    if (!search) {
      this.bodyTable = this.dataBody;
      return;
    }
    // Convierte el término de búsqueda a minúsculas.
    search = search.toLowerCase();
    search = this.removeAccents(search);

    // Filtra la tabla en función del término de búsqueda
    this.bodyTable = this.dataBody.filter((item: any) => {
      // Comprueba si alguna de las columnas contiene el término de búsqueda.
      return Object.values(item).some((value: any) => {
        if (typeof value === 'string') {
          // Convierte el valor a minúsculas y quita las tildes antes de comparar
        const cleanedValue = this.removeAccents(value.toLowerCase());
        return cleanedValue.includes(search);
        } else if (typeof value === 'object') {
      // Utilizar JSON.stringify para convertir el objeto en una cadena de texto para la búsqueda.
        const cleanedValue = this.removeAccents(JSON.stringify(value).toLowerCase());
        return cleanedValue.includes(search);
      }
        return false;
      });
    });
  }

  functionHome: any = (result: any) => this.afterModalClosed(result);
  afterModalClosed(result?: any) {}

  async rowClick(message: any) {
    this.rowClicked.emit(message);
  }
}
