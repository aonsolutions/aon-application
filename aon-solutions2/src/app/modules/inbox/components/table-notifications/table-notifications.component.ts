import {
  Component,
  EventEmitter,
  Input,
  OnChanges,
  OnInit,
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
  filterStatus: string[] = ['todas', 'nueva', 'vista'];
  bodyTable: any[] = [];
  totalMessages: number = 0;
  lenghtTitle   : number = 30;
  lenghtMensage : number = 50;

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
    const datepipe: DatePipe = new DatePipe(
      this.translateService.getDefaultLang()
    );
    let filterBuilder = new FilterBuilder();
    filterBuilder.addField('type', 'notificacion');
    // if(this.filterStatus[this.filterTabSelec] === 'todas')
    //   filterBuilder.addField('status', this.filterStatus[this.filterTabSelec]);

    if (this.filterDate > 0) {
      filterBuilder.addInterval('date', startDate, endDate);
    }
    this.messageService
      .getMessageList(filterBuilder.getFilter())
      .then((response) => {
        response.forEach((message, messageKey) => {
          const column: any = Object.assign({}, message);
          // key
          column.key = messageKey;
          // Nombre del asesor
          column.name = message.Name;

          if (
            this.filterStatus[this.filterTabSelec] ===
              message.Status.toLowerCase() ||
            this.filterStatus[this.filterTabSelec] === 'todas'
          ) {
            const lowerCaseStatus = message.Status.toLowerCase();
            console.log(lowerCaseStatus);
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
            column.date = datepipe.transform(message.Date, 'EEEE, HH:mm');
            column.class = message.Status == 'nueva' ? 'border-red' : '';
            tableRow.push(column);
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
