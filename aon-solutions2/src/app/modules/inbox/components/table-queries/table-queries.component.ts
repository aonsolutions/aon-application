import { Component, EventEmitter, Input, OnChanges, Output, SimpleChanges } from '@angular/core';
import {
  CollectionFactory,
  FilterBuilder,
  ICollection,
  IMessage,
} from 'libraries/AonSDK/aon';
import { BehaviorSubject, Observable } from 'rxjs';
import { MessageService } from 'src/app/core/services/message.service';
import { DatePipe } from '@angular/common';
import { TranslateService } from '@ngx-translate/core';
import { MessageChatService } from 'src/app/core/services/message-chat.service';

@Component({
  selector: 'app-table-queries',
  templateUrl: './table-queries.component.html',
  styleUrls: ['./table-queries.component.scss'],
})
export class TableQueriesComponent implements OnChanges {
  @Input() filterTabSelec: number = 0;
  filterStatus: string[] = [
    'todas',
    'abierta',
    'cerrada',
  ];
  @Input() filterDate: number = 0;
  @Input() filter: any = {};
  @Input() public messageList: Observable<ICollection<IMessage>> | undefined;
  @Output() messageTitleSelected: EventEmitter<string> = new EventEmitter<string>();
  @Output() rowClicked: EventEmitter<IMessage> = new EventEmitter<IMessage>();
  @Output() noPendingQueries: EventEmitter<boolean> = new EventEmitter<boolean>();
  @Input() id: number = 0;
  messageTotal: string = '0';
  bodyTable: any[] = [];
  showDetail: boolean = false;
  totalMessages: number = 0;
  currentFilterDate: number = 0;
  message: string = '';
  selectedMessage: IMessage | null = null;
  messages: ICollection<IMessage> =
  new CollectionFactory().createMessageCollection();

  public collectionFactory = new CollectionFactory();
  // Inbox area
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

  headerTable: any = {
    name: 'Name',
    status: 'Status',
    title: 'Title',
    description: 'Description',
    total: 'Total',
    date: 'Date',
    action: 'Action',
  };

  constructor(
    public messageService: MessageService,
    private translateService: TranslateService,
    private messageChatService: MessageChatService
  ) {
  }

  ngOnChanges(changes: SimpleChanges): void {
    this.updateTableData();
  }

  filterTableDate() {
    let date: { start: string; end: string } = { start: '', end: '' };
    switch (this.filterDate) {
      case 1:
        date.start = this.getStartDateOfWeek();
        date.end   = this.getEndDateOfWeek();
      break
      case 2:
        date.start = this.getStartDateOfMonth();
        date.end = this.getEndDateOfMonth();
      break
    }
    return date;
  }

  //Filtros para las fechas
  private getStartDateOfWeek(): string {
    const currentDate = new Date();
    const startDate = new Date(currentDate);
    startDate.setDate(startDate.getDate() - startDate.getDay()); // Inicio de la semana actual
    return this.formatDate(startDate);
  }

  private getEndDateOfWeek(): string {
    const currentDate = new Date();
    const endDate = new Date(currentDate);
    endDate.setDate(endDate.getDate() + (6 - endDate.getDay())); // Fin de la semana actual
    return this.formatDate(endDate);
  }

  private getStartDateOfMonth(): string {
    const currentDate = new Date();
    const startDate = new Date(currentDate.getFullYear(), currentDate.getMonth(), 1);
    return this.formatDate(startDate);
  }

  private getEndDateOfMonth(): string {
    const currentDate = new Date();
    const endDate = new Date(currentDate.getFullYear(), currentDate.getMonth() + 1, 0);
    return this.formatDate(endDate);
  }

  private formatDate(date: Date): string {
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
  }

    //TODO Método para cambiar el estado de una consulta a "Cerrada"
    archiveMessage(message: IMessage) {
      this.messageService.archiveMessage(message).then((updatedMessage) => {
        console.log('Consulta archivada:', updatedMessage);
      }).catch((error) => {
        console.error('Error al archivar consulta:', error);
      });
    }

    //TODO Método para cambiar el estado de una consulta a "Abierta"
    reopenMessage(message: IMessage) {
      this.messageService.reopenMessage(message).then((updatedMessage) => {
        console.log('Consulta abierta:', updatedMessage);
      }).catch((error) => {
        console.error('Error al reabrir consulta:', error);
      });
    }

  private updateTableData() {
    let tableRow: any[] = [];
    const datepipe: DatePipe = new DatePipe(this.translateService.getDefaultLang());

    let filterBuilder = new FilterBuilder();
    filterBuilder.addField('type', 'consulta');
    if (this.filterDate > 0) {
      let dates = this.filterTableDate()

      filterBuilder.addInterval('date', dates.start, dates.end);
    }
    this.messageService
      .getMessageList(filterBuilder.getFilter())
      .then((response) => {
        this.messagess = response;
        this.messagesSubject.next(this.messagess);
        let pendingQueriesFound = false;
        response.forEach((message, messageKey) => {
          // Mensajes - total
          let filterBuilderTotal = new FilterBuilder();
          filterBuilderTotal.addField('idMessage', messageKey);
          this.messageChatService
            .getMessageChatCount(filterBuilderTotal.getFilter())
            .then((response) => {
              this.messageTotal! = response < 100 ? response.toString() : '+99';

            column.total = "<span class='circle green'>" + this.messageTotal + '</span>'
          });

          const column: any = Object.assign({}, message);
          column.key = messageKey;
          column.name = message.Name;

          if (
            this.filterStatus[this.filterTabSelec] === message.Status.toLowerCase()
            || this.filterStatus[this.filterTabSelec] === 'todas'
          ) {
            const lowerCaseStatus = message.Status.toLowerCase();
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
            column.date = datepipe.transform(message.Date, 'MM/dd/yyyy, HH:mm');
            column.action = {
              icon: lowerCaseStatus.includes('abierta')
                ? [{ archive: 'grey' }]
                : [{ replay: 'grey' }],
            };
            column.class = (message.Status == 'abierta') ? 'border-red' : '';
            tableRow.push(column);

            if (message.Status.toLowerCase().includes('nueva')) {
              pendingQueriesFound = true;
            }
          }
        });

        this.bodyTable = tableRow;
      this.noPendingQueries.emit(!pendingQueriesFound);
      // No tenemos mensaje en la tabla
      if (response.size() === 0) {
        this.translateService.get([
          'INBOX.NOQUERIESTHISWEEK',
          'INBOX.NOQUERIESTHISMONTH',
          ''
        ]).subscribe((result) => {
        switch (this.filterDate) {
          case 1:
              this.message = result['INBOX.NOQUERIESTHISWEEK'];
            break;
          case 2:
            this.message = result['INBOX.NOQUERIESTHISMONTH'];
            break;
          default:
            this.message = result['INBOX.NOMESSAGES']
            break;
        }
      });
      } else {
        this.message = '';
      }
        this.noPendingQueries.emit(!pendingQueriesFound);
      });
  }

  functionHome: any = (result: any) => this.afterModalClosed(result);
  afterModalClosed(result?: any) {}

  rowClick(message: any) {
    this.rowClicked.emit(message);
  }
}


