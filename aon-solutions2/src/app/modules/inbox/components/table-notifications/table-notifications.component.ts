import {
  Component,
  EventEmitter,
  Input,
  OnChanges,
  Output,
  SimpleChanges,
} from '@angular/core';
import {
  CollectionFactory,
  FilterBuilder,
  ICollection,
  IMessage,
} from 'libraries/AonSDK/src/aon';
import { Observable } from 'rxjs';
import { MessageService } from 'src/app/core/services/message.service';
import { DatePipe } from '@angular/common';
import { TranslateService } from '@ngx-translate/core';

@Component({
  selector: 'app-table-notifications',
  templateUrl: './table-notifications.component.html',
  styleUrls: ['./table-notifications.component.scss'],
})
export class TableNotificationsComponent implements OnChanges {
  @Input() public messageList: Observable<ICollection<IMessage>> | undefined;
  @Input() filterDate: number = 0;
  @Output() rowClicked: EventEmitter<IMessage> = new EventEmitter<IMessage>();
  @Input() filterTabSelec: number = 0;
  @Output() noPendingNotification: EventEmitter<boolean> =
    new EventEmitter<boolean>();

  filterStatus: string[] = ['todas', 'nueva', 'vista'];
  bodyTable: any[] = [];
  totalMessages: number = 0;
  lenghtTitle: number = 30;
  lenghtMensage: number = 50;
  message: string = '';
  messages: ICollection<IMessage> =
    new CollectionFactory().createMessageCollection();

  displayedColumns: string[] = [
    'name',
    'status',
    'title',
    'description',
    'date',
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
    date: 'Date',
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

  // Filtros fechas principio semana
  private getStartDateOfWeek(): string {
    const currentDate = new Date();
    const startDate = new Date(currentDate);
    startDate.setDate(startDate.getDate() - startDate.getDay()); // Inicio de la semana actual (domingo)
    return this.formatDate(startDate);
  }

  // Filtos fechas final semana
  private getEndDateOfWeek(): string {
    const currentDate = new Date();
    const endDate = new Date(currentDate);
    endDate.setDate(endDate.getDate() + (6 - endDate.getDay())); // Fin de la semana actual (sábado)
    return this.formatDate(endDate);
  }

  // Filtros fechas principio mes
  private getStartDateOfMonth(): string {
    const currentDate = new Date();
    const startDate = new Date(
      currentDate.getFullYear(),
      currentDate.getMonth(),
      1
    );
    return this.formatDate(startDate);
  }

  // Filtros fechas final mes
  private getEndDateOfMonth(): string {
    const currentDate = new Date();
    const endDate = new Date(
      currentDate.getFullYear(),
      currentDate.getMonth() + 1,
      0
    );
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
    filterBuilder.addField('type', 'notificacion');
    if (this.filterDate > 0) {
      let dates = this.filterTableDate();

      filterBuilder.addInterval('date', dates.start, dates.end);
    }
    this.messageService
      .getMessageList(filterBuilder.getFilter())
      .then((response) => {
        let pendingNotificacionsFound = false;
        response.forEach((message, messageKey) => {
          const column: any = Object.assign({}, message);
          const lowerCaseStatus = message.Status.toLowerCase();
          // key
          column.key = messageKey;
          // Nombre del asesor
          column.name = message.Name;
          if (
            this.filterStatus[this.filterTabSelec] ===
              message.Status.toLowerCase() ||
            this.filterStatus[this.filterTabSelec] === 'todas'
          ) {
            column.status = {
              icon: lowerCaseStatus.includes('nueva') ? [{}] : [],
              text: `<span class="${
                lowerCaseStatus.includes('nueva')
                  ? 'background-text-red-light'
                  : 'background-text-griss-light'
              }">${message.Status}</span>`,
            };

            // Asunto del mensaje
            column.title = message.Title;
            // Mensaje
            column.description = message.Description;
            // Fecha
            column.date = datepipe.transform(message.Date, 'MM/dd/yyyy, HH:mm');
            // Marcar como nueva
            column.class = lowerCaseStatus.includes('nueva')
              ? 'border-red'
              : '';
            // Agregamos el mensaje
            tableRow.push(column);
          }
        });

        this.bodyTable = tableRow;
        this.noPendingNotification.emit(!pendingNotificacionsFound);

        // No tenemos mensaje en la tabla
        if (response.size() === 0) {
          this.translateService
            .get([
              'INBOX.NONOTIFICATIONSTHISWEEK',
              'INBOX.NONOTIFICATIONSTHISMONTH',
              'NOMESSAGES',
            ])
            .subscribe((result) => {
              switch (this.filterDate) {
                case 1:
                  this.message = result['INBOX.NONOTIFICATIONSTHISWEEK'];
                  break;
                case 2:
                  this.message = result['INBOX.NONOTIFICATIONSTHISMONTH'];
                  break;
                default:
                  this.message = result['INBOX.NOMESSAGES'];
                  break;
              }
            });
        } else {
          this.message = '';
        }

        this.noPendingNotification.emit(true);
      });
  }

  functionHome: any = (result: any) => this.afterModalClosed(result);
  afterModalClosed(result?: any) {}

  rowClick(message: IMessage) {
    this.rowClicked.emit(message);
  }
}
