import { DropdownMenuComponent } from 'src/app/shared/components/dropdown-menu/dropdown-menu.component';
import { Factory, CollectionFactory, ICollection, FilterBuilder, IMessageChat, IMessage, StatusMessage, TypeMessage } from 'libraries/AonSDK/src/aon';
import { MenuItem } from 'src/app/core/models/interface/menu-item';
import { MessageChatService } from 'src/app/core/services/message-chat.service';
import { MessageService } from 'src/app/core/services/message.service';
import { ModalCreateComponent } from '../components/modal-create/modal-create.component';
import { OptionsService } from 'src/app/shared/services/options.service';
import { ReportingService } from 'src/app/core/services/reporting.service';
import { TablesInboxComponent } from '../components/tables-inbox/table-inbox.component';
import { Component, EventEmitter, OnInit, Output, ViewChild } from '@angular/core';
import { DatePipe } from '@angular/common';
import { TranslateService } from '@ngx-translate/core';
import { EnterpriseService } from 'src/app/core/services/enterprise.service';

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
  @ViewChild('menu') dropdownMenuComponent: DropdownMenuComponent = new DropdownMenuComponent();
  @ViewChild('modalCreateInbox') modalCreateInboxComponent: any = '';
  functionModalCreateInbox: any = (result: any) => this.afterModalCreateInboxClosed(result);
  @ViewChild(TablesInboxComponent, { static: false }) tablesInboxComponent!: TablesInboxComponent;
  @Output() consultaCreated: EventEmitter<void> = new EventEmitter<void>();
  messChat: IMessageChat[] = [];
  datepipe: DatePipe = new DatePipe(this.translateService.getDefaultLang());
  collectionFactory = new CollectionFactory();
  entityFactory = new Factory();
  messages: ICollection<IMessage> =
  new CollectionFactory().createMessageCollection();
  messagesChat: ICollection<IMessageChat> =
  this.collectionFactory.createMessageChatCollection();
  messagesData: IMessage = this.entityFactory.createMessage();
  messageChat: IMessageChat = this.entityFactory.createMessageChat();

  tabsConsultas: Tabs[] = [];
  tabsTareas: Tabs[] = [];
  tabsNotificaciones: Tabs[] = [];
  selectedTab: number = 0;
  selectedFilterDate: number = 0;
  tabIndex: number = 0;
  showDetail: boolean = false;
  isModalVisible: boolean = false;
  showSendButton: boolean = false;
  showSendButtons: boolean = false;
  filterDate: number = 1;
  selectedFilterText: string = '';
  expandedIndex: number = -1;
  newMessageDescription: string = '';
  spinner: boolean = true;
  totalMessageCount: number = 0;
  menuItem: MenuItem[] = [];
  selected: string = '';
  consultaMessageCount: number = 0;
  tareasMessageCount: number = 0;
  notificacionesMessageCount: number = 0;
  updateTable: boolean = false;
  constructor(
    private translateService: TranslateService,
    public reportingService: ReportingService,
    private messageService: MessageService,
    private messageChatService: MessageChatService,
    private optionsService: OptionsService,
    private enterpriseService: EnterpriseService
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
                this.filterTable(2, result['INBOX.THIS_MONTH']),
            },
            {
              root: true,
              text: result['INBOX.ALLS'],
              click: () => this.filterTable(0, result['INBOX.ALLS']),
            },
          ]);

        this.calculateMessageCounts();
        this.selectedFilterText =
          this.translateService.instant('INBOX.THIS_WEEK');
      });
  }

  ngOnInit(): void {

  }

  ngAfterViewInit(): void {
    const url = window.location.href.split('/').pop();

    if (url === 'create'){
      this.showModal();
    } else if(url !== 'inbox'){
      this.messageService.getMessage(url).then((message) => {
        this.rowClickHandler(message);
      });
    }
  }

  afterModalCreateInboxClosed(result?: any) {
    // console.log(result);
  }

  showModal() {
    this.modalCreateInboxComponent.openDialog(
      ModalCreateComponent,
      this.functionModalCreateInbox,
    );
  }

  onTabChange() {
    this.showDetail = false;
    this.showSendButton = false;
    this.showSendButtons = false;
    this.tabIndex = 0;
  }

  async rowClickHandler(message: any) {
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
    if (message.type === 'notificacion') {
      this.messageService.markAsReadNotification(this.messagesData);
      this.tablesInboxComponent.updateTableData();
    }
    this.changeView();
    // Desactivo el spinner
    this.spinner = false;
  }

  // Ocultar o ver botones
  consultarClicked() {
    this.showSendButton = !this.showSendButton;
  }

  // Ocultar o ver botones
  replyconsultarClicked() {
    this.showSendButtons = !this.showSendButtons;
  }

  // Filtros para las tablas(semana, mes o todos)
  filterTable(optionValue: number, name: string) {
    this.filterDate = optionValue;
    this.selected = name;
    switch (optionValue) {
      case 0:
        this.selectedFilterText = this.translateService.instant('INBOX.ALLS');
        break;
      case 1:
        this.selectedFilterText =
          this.translateService.instant('INBOX.THIS_WEEK');
        break;
      case 2:
        this.selectedFilterText =
          this.translateService.instant('INBOX.THIS_MONTH');
        break;
      default:
        this.selectedFilterText =
          this.translateService.instant('INBOX.THIS_WEEK');
        break;
      }
      console.log(this.selected);
  }

  // Calcula el total de mensajes
  async calculateMessageCounts() {
    const types = ['consulta', 'tarea', 'notificacion'];
    this.totalMessageCount = 0;

    for (const type of types) {
      const filterBuilder = new FilterBuilder();
      filterBuilder.addField('type', type);

      if (type === 'consulta') {
        filterBuilder.addField('status', 'abierta');
      } else if (type === 'tarea') {
        filterBuilder.addField('status', 'pendiente');
      } else if (type === 'notificacion') {
        filterBuilder.addField('status', 'nueva');
      }
      const count = await this.messageService.getMessageCount(
        filterBuilder.getFilter()
      );

      if (type === 'consulta') {
        this.consultaMessageCount = count;
      } else if (type === 'tarea') {
        this.tareasMessageCount = count;
      } else if (type === 'notificacion') {
        this.notificacionesMessageCount = count;
      }

      this.totalMessageCount += count;
    }
  }

  // Llamamos al servicio para cambiar el estado a "abierta"
   archiveMessage() {
    this.messageService.archiveMessage(this.messagesData);
    this.calculateMessageCounts();
    this.updateTable = !this.updateTable;
  }

  // Cerrar details
  closeDetail() {
    this.showDetail = false;
  }

  // expande la descripción del mensaje
  toggleDescription(index: number): void {
    this.expandedIndex = this.expandedIndex === index ? -1 : index;
  }

  // ocultar botones
  changeView(): void {
    this.showSendButton = false;
    this.showSendButtons = false;
  }

  //Crear mensaje de consulta
  newMessageDescriptionChange(newValue: string) {
    this.newMessageDescription = newValue;
  }

  //Crear mensaje de chat de consultas
  async createChatMessage(description: string) {

    const currentEnterprise = await this.enterpriseService.getCurrentEntepriseData()
    if (this.messageChat && this.messagesData) {

      const newMessageChat = this.messageService.objectFactory.createMessageChat()
      .setIdMessage(this.messagesData.Id)
      .setName(currentEnterprise.Name)
      .setDescription(description)
      .setType('chat');

        // Crear el mensaje
        await this.messageChatService.createMessageChat(newMessageChat).then((response) => {
          this.messageChat = response;
          this.tablesInboxComponent.updateTableData();
          // Desactivo el spinner
          this.spinner = false;
        })
        // Agregar el nuevo mensaje
        this.messagesChat.add(this.messageChat);
    }
  }

  //Crear mensaje tipo consulta
  async createMessage(description: string){
    const currentEnterprise = await this.enterpriseService.getCurrentEntepriseData()

    if (this.messagesData) {

      const newMessage = this.messageService.objectFactory.createMessage()
      .setName(currentEnterprise.Name)
      .setDescription(description)
      .setType(TypeMessage.CONSULTA)
      .setStatus(StatusMessage.ABIERTA)
      .setLastMessageChatOrigin(false);

        // Crear el mensaje
        await this.messageService.createMessage(newMessage).then((response) => {
          this.messagesData = response;
          this.tablesInboxComponent.updateTableData();
          // actualizar total de mensajes
          this.calculateMessageCounts();
          // Desactivo el spinner
          this.spinner = false;
        })
        // Agregar el nuevo mensaje
        this.messages.add(this.messagesData);
        this.messChat = [];
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

  sendMessage() {
    // para no enviar mensajes vacíos
    if (this.newMessageDescription.trim() === '') {
      return;
    }
    // Llama a la función para crear un nuevo mensaje
    this.createMessage(this.newMessageDescription);

    // Limpia el campo de entrada después de enviar el mensaje
    this.newMessageDescription = '';
  }

  // Filtro de busqueda atraves de la tabla
  search(search: string) {
    if (this.tablesInboxComponent) {
      this.tablesInboxComponent.searchMessage(search);
    }
  }
}
