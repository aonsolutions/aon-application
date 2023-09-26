import {
  Component,
  OnInit,
  ViewChild,
} from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import {
  Factory,
  CollectionFactory,
  ErrorResponse,
  ICollection,
  FilterBuilder,
  IMessageChat,
  IMessage,
} from 'libraries/AonSDK/aon';
import { ReportingService } from 'src/app/core/services/reporting.service';
import { MessageService } from 'src/app/core/services/message.service';
import { MessageChatService } from 'src/app/core/services/message-chat.service';
import { ModalCreateComponent } from '../components/modal-create/modal-create.component';
import { TableQueriesComponent } from '../components/table-queries/table-queries.component';
import { DatePipe } from '@angular/common';
import { MenuItem } from 'src/app/core/models/interface/menu-item';
import { DropdownMenuComponent } from 'src/app/shared/components/dropdown-menu/dropdown-menu.component';

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
  messageChat: IMessageChat = this.entityFactory.createMessageChat();

  messagesChat: ICollection<IMessageChat> =
    this.collectionFactory.createMessageChatCollection();

  functionHome: any = (result: any) => this.afterModalClosed(result);
  tabsConsultas: Tabs[] = [];
  tabsTareas: Tabs[] = [];
  tabsNotificaciones: Tabs[] = [];
  selectedTab: number = 0;
  selectedFilterDate: number = 0;
  tabIndex: number = 0;
  showDetail: boolean = false;
  noTasksMessage: boolean = false;
  isModalVisible: boolean = false;
  showSendButton: boolean = false;
  showSendButtons: boolean = false;
  filterDate: number = 1;
  selectedFilterText: string = 'Esta semana';
  expandedIndex: number = -1;
  newMessageDescription: string = '';
  noTasksMessageText: string = '';
  filterPending: boolean = false;
  noTasksThisWeekMessage: string = 'No hay tareas pendientes esta semana.';
  noTasksThisMonthMessage: string = 'No hay tareas pendientes este mes.';
  noTasksPendingThisMonthMessage: string = 'No hay tareas realizadas este mes.';
  noTasksPendingThisWeekMessage: string =
    'No hay tareas realizadas esta semana.';
  noNotificationsThisWeek: string = 'No hay notificaciones nuevas esta semana.';
  noNotificationsThisMonth: string = 'No hay notificaciones nuevas este mes.';
  noNotificationsViewThisMonth: string =
    'No hay notificaciones vistas este mes.';
  noNotificationsViewThisWeek: string =
    'No hay notificaciones vistas esta semana.';
  noQueriesThisWeek: string = 'No hay consultas abiertas esta semana.';
  noQueriesThisMonth: string = 'No hay consultas abiertas este mes.';
  noQueriesClosedThisMonth: string = 'No hay consultas cerradas este mes.';
  noQueriesClosedThisWeek: string = 'No hay consultas cerradas esta semana.';
  @ViewChild('menu') dropdownMenuComponent: DropdownMenuComponent =
    new DropdownMenuComponent();

  consultaMessageCount: number = 0;
  tareasMessageCount: number = 0;
  notificacionesMessageCount: number = 0;
  totalMessageCount: number = 0;
  tableQueriesComponent!: TableQueriesComponent;
  menuItem: MenuItem[] = [];
  selected: string = '';
  items: any[] = [];
  data: any[] = [];

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
        'INBOX.THIS_WEEK',
        'INBOX.THIS_MONTH',
        'INBOX.ALLS',
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
          ]),
          (this.menuItem! = [
            {
              root: true,
              text: result['INBOX.THIS_WEEK'],
              click: () => this.filterTable(1, result['INBOX.THIS_WEEK']),
            },
            {
              root: true,
              text: result['INBOX.THIS_MONTH'],
              click: () =>
                this.filterTable(2, result['INBOX.THIS_MONTHTHIS_MONTH']),
            },
            {
              root: true,
              text: result['INBOX.ALLS'],
              click: () => this.filterTable(0, result['INBOX.ALLS']),
            },
          ]);

        this.calculateMessageCounts();
      });
  }

  ngOnInit(): void {}

  //No hay tareas pendientes ni realizadas:
  showNoTasksMessage(hasNoTasks: boolean) {
    this.noTasksMessage = hasNoTasks;

    if (hasNoTasks) {
      if (this.filterDate === 1) {
        this.noTasksMessageText = this.filterPending
          ? this.noTasksPendingThisWeekMessage
          : this.noTasksThisWeekMessage;
      } else if (this.filterDate === 2) {
        this.noTasksMessageText = this.filterPending
          ? this.noTasksPendingThisMonthMessage
          : this.noTasksThisMonthMessage;
      }
    }
  }

  //No hay notificaciones nuevas ni vistas:
  showNoNotificacions(hasNoTasks: boolean) {
    this.noTasksMessage = hasNoTasks;

    if (hasNoTasks) {
      if (this.filterDate === 1) {
        this.noTasksMessageText = this.filterPending
          ? this.noNotificationsThisWeek
          : this.noNotificationsViewThisWeek;
      } else if (this.filterDate === 2) {
        this.noTasksMessageText = this.filterPending
          ? this.noNotificationsThisMonth
          : this.noNotificationsViewThisWeek;
      }
    }
  }

  //No hay consultas abiertas ni cerradas:
  showNoQueries(hasNoTasks: boolean) {
    this.noTasksMessage = hasNoTasks;

    if (hasNoTasks) {
      if (this.filterDate === 1) {
        this.noTasksMessageText = this.filterPending
          ? this.noQueriesThisWeek
          : this.noQueriesClosedThisWeek;
      } else if (this.filterDate === 2) {
        this.noTasksMessageText = this.filterPending
          ? this.noQueriesThisMonth
          : this.noQueriesClosedThisWeek;
      }
    }
  }

  afterModalClosed(result?: any) {
    console.log(result);
  }

  showModal() {
    this.modalComponent.openDialog(
      ModalCreateComponent,
      this.functionHome,
      'Data from home'
    );
  }

  onTabChange() {
    this.showDetail = false;
    this.showSendButton = false;
    this.showSendButtons = false;
    this.tabIndex = 0;
  }

  backTable() {
    this.showDetail = false;
  }

  async rowClickHandler(message: any) {
    console.log(message, 'message');

    try {
      const isSameRow =
        this.messagesData && this.messagesData.Id === message.key;
      this.showDetail = !isSameRow ? true : !this.showDetail;
      this.messagesData = await this.messageService.getMessage(message.key);

      if (message.type == 'consulta') {
        let filterBuilder = new FilterBuilder();
        filterBuilder.addField('idMessage', message.key);
        this.messagesChat = await this.messageChatService.getMessageChatList(
          filterBuilder.getFilter()
        );
      }
      this.changeView();
    } catch (error) {
      console.error('Error al cargar el chat del mensaje:', error);
    }
  }

  consultarClicked() {
    this.showSendButton = !this.showSendButton;
  }

  replyconsultarClicked() {
    this.showSendButtons = !this.showSendButtons;
  }

  filterTable(optionValue: number, name: string) {
    this.filterDate = optionValue;
    this.selected = name;
    switch (optionValue) {
      case 1:
        this.selectedFilterText = 'Esta semana';
        break;
      case 2:
        this.selectedFilterText = 'Este mes';
        break;
      case 0:
        this.selectedFilterText = 'Todo';
        break;
      default:
        this.selectedFilterText = 'Esta semana';
        break;
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

  closeDetail() {
    this.showDetail = false;
  }

  toggleDescription(index: number): void {
    this.expandedIndex = this.expandedIndex === index ? -1 : index;
  }

  changeView(): void {
    this.showSendButton = false;
    this.showSendButtons = false;
  }

  newMessageDescriptionChange(newValue: string) {
    this.newMessageDescription = newValue;
  }

  async createChatMessage(description: string) {
    if (this.messagesData && this.messageChat) {

      const newMessageChat: IMessageChat = {
        Id: this.messageChat.Id,
        IdMessage: this.messagesData.Id,
        Name: '',
        Description: description,
        Date: new Date(),
        Type: 'chat',
        getKey: () => this.messageChat.Id,
        getFilterableFields: () => new Map(),
        getSortableFields: () => new Map(),
      };

      try {
        // Crear el mensaje
        const createdMessageChat =
          await this.messageChatService.createMessageChat(newMessageChat);

        // Agregar el nuevo mensaje
        this.messagesChat.add(createdMessageChat);

      } catch (error) {
        throw error instanceof ErrorResponse ?  error : new ErrorResponse(error);
      }
    }
  }

  sendChatMessage() {
    // para no enviar mensajes vacíos
    if (this.newMessageDescription.trim() === '') {
      return;
    }

    // Llama a la función para crear un nuevo mensaje de chat
    this.createChatMessage(this.newMessageDescription);

    // Limpia el campo de entrada después de enviar el mensaje
    this.newMessageDescription = '';
  }
}
