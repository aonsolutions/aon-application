import { Component, EventEmitter, Input, OnChanges, OnInit, Output, SimpleChanges } from '@angular/core';
import { CollectionFactory, FilterBuilder, ICollection, IMessage } from 'libraries/AonSDK/src/aon';
import { BehaviorSubject, Observable } from 'rxjs';
import { MessageService } from 'src/app/core/services/message.service';
import { DatePipe } from '@angular/common';
import { TranslateService } from '@ngx-translate/core';

@Component({
  selector: 'app-table-tasks',
  templateUrl: './table-tasks.component.html',
  styleUrls: ['./table-tasks.component.scss'],
})
export class TableTasksComponent implements OnChanges {
  @Input() filterTabSelec: number = 0;
  filterStatus: string[] = [
    'todas',
    'pendiente',
    'realizada',
  ];
  @Input() filterDate: number = 0;
  @Input() filter: any = {};
  @Input() public messageList: Observable<ICollection<IMessage>> | undefined;
  @Output() rowClicked: EventEmitter<IMessage> = new EventEmitter<IMessage>();
  @Input() id: number = 0;

  selectedMessage: IMessage | null = null;
  bodyTable: any[] = [];
  showDetail: boolean = false;
  totalMessages: number = 0;
  @Output() noPendingTasks: EventEmitter<boolean> = new EventEmitter<boolean>();
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
    'status',
    'title',
    'description',
    'date',
  ];

  constructor(
    private messageService: MessageService,
    private translateService: TranslateService,
    ) {}

    headerTable: any = {
      name: 'Name',
      status: 'Status',
      title: 'Title',
      description: 'Description',
      date: 'Date',
    };

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
      filterBuilder.addField('type', 'tarea');
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
      let pendingTasksFound = false;
      response.forEach((message, messageKey) => {
        const column: any = Object.assign({}, message);
        column.key = messageKey;
        column.name = message.Name;
          // eliminar mi if cuando tenga el filtro desde la api y descomentar lo de arriba
        if (
          this.filterStatus[this.filterTabSelec] === message.Status.toLowerCase()
          || this.filterStatus[this.filterTabSelec] === 'todas'
        ) {
          const lowerCaseStatus = message.Status.toLowerCase();
          column.status = {
            icon: lowerCaseStatus.includes('realizada') ? [{}] : [],
            text: `<span class="${
              lowerCaseStatus.includes('pendiente')
                ? 'background-text-red-light'
                : 'background-text-griss-light'
            }">${message.Status}</span>`,
          };
          column.title = message.Title;
          column.description = message.Description;
          column.date = datepipe.transform(message.Date, 'EEEE, HH:mm');
          column.class = (message.Status == 'pendiente') ? 'border-red' : '';
          tableRow.push(column);


          if (message.Status.toLowerCase().includes('pendiente')) {
            pendingTasksFound = true;
          }

          if (this.selectedMessage === null) {
            this.selectedMessage = { ...message };
          }
        }
      });
      this.bodyTable = tableRow;

      this.noPendingTasks.emit(!pendingTasksFound);
    });
  }

  rowClick(message: any) {
    this.rowClicked.emit(message);
  }
}
