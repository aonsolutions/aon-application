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
  @Input() id: number = 0;
  messageTotal: string = '0';
  bodyTable: any[] = [];
  showDetail: boolean = false;
  totalMessages: number = 0;
  currentFilterDate: number = 0;
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

  filterTable(filterType: number) {
    if (filterType === 1) {
      // Filtro por semana
      const startDate = this.getStartDateOfWeek();
      const endDate = this.getEndDateOfWeek();
      this.updateTableData(startDate, endDate);
    } else if (filterType === 2) {
      // Filtro por mes
      const startDate = this.getStartDateOfMonth();
      const endDate = this.getEndDateOfMonth();
      this.updateTableData(startDate, endDate);
    } else {
      // Filtro por todas las fechas
      this.updateTableData();
    }
  }

  //Filtros para las fechas
  private getStartDateOfWeek(): string {
    const currentDate = new Date();
    const startDate = new Date(currentDate);
    startDate.setDate(startDate.getDate() - startDate.getDay()); // Inicio de la semana actual (domingo)
    return this.formatDate(startDate);
  }

  private getEndDateOfWeek(): string {
    const currentDate = new Date();
    const endDate = new Date(currentDate);
    endDate.setDate(endDate.getDate() + (6 - endDate.getDay())); // Fin de la semana actual (sábado)
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

  private updateTableData(startDate?: string, endDate?: string) {
    let tableRow: any[] = [];
    const datepipe: DatePipe = new DatePipe(this.translateService.getDefaultLang());

    let filterBuilder = new FilterBuilder();
    filterBuilder.addField('type', 'consulta');
    // if(this.filterStatus[this.filterTabSelec] === 'todas')
    //   filterBuilder.addField('status', this.filterStatus[this.filterTabSelec]);

    if (this.filterDate > 0) {
      filterBuilder.addInterval('date', startDate, endDate);
    }

    this.messageService
      .getMessageList(filterBuilder.getFilter())
      .then((response) => {
        this.messagess = response;
        this.messagesSubject.next(this.messagess);
        response.forEach((message, messageKey) => {
          // Mensajes - total
          let filterBuilderTotal = new FilterBuilder();
          filterBuilderTotal.addField('idMessage', messageKey);
          this.messageChatService
            .getMessageChatCount(filterBuilderTotal.getFilter())
            .then((response) => {
              this.messageTotal! = response < 100 ? response.toString() : '+99';
              console.log(this.messageTotal);

            column.total = "<span class='circle green'>" + this.messageTotal + '</span>'

          });

          const column: any = Object.assign({}, message);
          column.key = messageKey;
          column.name = message.Name;
          // eliminar mi if cuando tenga el filtro desde la api y descomentar lo de arriba
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
            column.date = datepipe.transform(message.Date, 'EEEE, HH:mm');
            column.action = {
              icon: lowerCaseStatus.includes('abierta')
                ? [{ archive: 'grey' }]
                : [{ replay: 'grey' }],
            };
            column.class = (message.Status == 'abierta') ? 'border-red' : '';
            tableRow.push(column);

            if (this.selectedMessage === null) {
              this.selectedMessage = { ...message };
            }
          }
        });

        this.bodyTable = tableRow;
      });
  }

  functionHome: any = (result: any) => this.afterModalClosed(result);
  afterModalClosed(result?: any) {}

  rowClick(message: any) {
    this.rowClicked.emit(message);
  }
}


