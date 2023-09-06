import { Component, EventEmitter, OnInit, ViewChild } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import {
  Factory,
  CollectionFactory,
  ICollection,
  FilterBuilder,
  IMessageChat,
  IMessage,
} from 'libraries/AonSDK/aon';
import { ReportingService } from 'src/app/core/services/reporting.service';
import { MessageService } from 'src/app/core/services/message.service';
import { MessageChatService } from 'src/app/core/services/message-chat.service';
import { ModalCreateComponent } from '../../components/inbox/modal-create/modal-create.component';
import { TableQueriesComponent } from '../../components/inbox/table-queries/table-queries.component';
import { DatePipe } from '@angular/common';

export interface Tabs {
  name: string;
  icon?: string;
  color?: string;
}
@Component({
  selector: 'app-inboxview',
  templateUrl: './inboxview.component.html',
  styleUrls: ['./inboxview.component.scss'],
})
export class InboxviewComponent implements OnInit {
  @ViewChild('modal') modalComponent: any = '';
  @ViewChild(TableQueriesComponent, { static: false })
  collectionFactory = new CollectionFactory();
  entityFactory = new Factory();
  datepipe: DatePipe = new DatePipe(this.translateService.getDefaultLang());

  messagesData: IMessage = this.entityFactory.createMessage();
  messagesChat: ICollection<IMessageChat> =
    this.collectionFactory.createMessageChatCollection();

  functionHome: any = (result: any) => this.afterModalClosed(result);
  tabsConsultas: Tabs[] = [];
  tabsTareas: Tabs[] = [];
  tabsNotificaciones: Tabs[] = [];
  selectedTab: number = 0;
  tabIndex: number = 0;
  showDetail: boolean = false;
  noTasksMessage: boolean = false;
  isModalVisible: boolean = false;
  showSendButton: boolean = false;
  selectedFilter: string = 'INBOX.THIS_WEEK';

  consultaMessageCount: number = 0;
  tareasMessageCount: number = 0;
  notificacionesMessageCount: number = 0;
  totalMessageCount: number = 0;
  tableQueriesComponent!: TableQueriesComponent;

  constructor(
    private translateService: TranslateService,
    public reportingService: ReportingService,
    private messageService: MessageService,
    private messageChatService: MessageChatService
  ) {
    this.translateService
      .get([
        'INBOX.ALL',
        'INBOX.PENDING',
        'INBOX.REALIZED',
        'INBOX.OPENED',
        'INBOX.CLOSED',
        'INBOX.NEWS',
        'INBOX.VIEWS',
      ])
      .subscribe((result) => {
        (this.tabsTareas = [
          { name: result['INBOX.ALL'], color: 'black' },
          {
            name: result['INBOX.PENDING'],
            color: 'black',
            icon: 'remove_circle',
          },
          {
            name: result['INBOX.REALIZED'],
            color: 'black',
            icon: 'check_circle',
          },
        ]),
          (this.tabsConsultas = [
            { name: result['INBOX.ALL'], color: 'black' },
            { name: result['INBOX.OPENED'], color: 'black', icon: 'replay' },
            { name: result['INBOX.CLOSED'], color: 'black', icon: 'archive' },
          ]),
          (this.tabsNotificaciones = [
            { name: result['INBOX.ALL'], color: 'black' },
            {
              name: result['INBOX.NEWS'],
              color: 'black',
              icon: 'notifications_active',
            },
            {
              name: result['INBOX.VIEWS'],
              color: 'black',
              icon: 'remove_red_eye',
            },
          ]);
      });
  }

  ngOnInit(): void {
    this.calculateMessageCounts();
  }

  showNoTasksMessage(hasNoTasks: boolean) {
    this.noTasksMessage = hasNoTasks;
  }

  afterModalClosed(result?: any) {
    console.log(result);
  }

  showModal() {
    this.isModalVisible = true;
    this.modalComponent.openDialog(
      ModalCreateComponent,
      this.functionHome,
      'Data from home'
    );
  }

  onTabChange() {
    this.showDetail = false;
    this.showSendButton = false;
    this.tabIndex = 0;
  }

  async rowClickHandler(message: any) {
    console.log(message);
    try {
      this.showDetail = true;

      this.messagesData = await this.messageService.getMessage(message.key);
      if (message.type == 'consulta') {
        let filterBuilder = new FilterBuilder();
        filterBuilder.addField('idMessage', message.key);
        this.messagesChat = await this.messageChatService.getMessageChatList(
          filterBuilder.getFilter()
        );
      }
    } catch (error) {
      console.error('Error al cargar el chat del mensaje:', error);
    }
  }

  consultarClicked() {
    this.showSendButton = !this.showSendButton;
  }

  filterTable(name: string) {
    if (name === 'estaSemana') {
      this.selectedFilter = 'INBOX.THIS_WEEK';
    } else if (name === 'esteMes') {
      this.selectedFilter = 'INBOX.THIS_MONTH';
    } else if (name === 'todo') {
      this.selectedFilter = 'INBOX.ALLS';
    }
  }

  async calculateMessageCounts() {
    try {
      // Calcula el recuento para "consulta"
      let filterBuilder = new FilterBuilder();
      filterBuilder.addField('type', 'consulta');
      this.consultaMessageCount = await this.messageService.getMessageCount(
        filterBuilder.getFilter()
      );

      // Calcula el recuento para "tareas"
      filterBuilder = new FilterBuilder();
      filterBuilder.addField('type', 'tarea');
      this.tareasMessageCount = await this.messageService.getMessageCount(
        filterBuilder.getFilter()
      );

      // Calcula el recuento para "notificaciones"
      filterBuilder = new FilterBuilder();
      filterBuilder.addField('type', 'notificacion');
      this.notificacionesMessageCount =
        await this.messageService.getMessageCount(filterBuilder.getFilter());

      // Calcula el recuento total
      this.totalMessageCount =
        this.consultaMessageCount +
        this.tareasMessageCount +
        this.notificacionesMessageCount;
    } catch (error) {
      console.error('Error al calcular el recuento de mensajes:', error);
    }
  }
}
