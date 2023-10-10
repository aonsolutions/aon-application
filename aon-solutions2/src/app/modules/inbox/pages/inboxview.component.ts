import { Component, OnInit, ViewChild } from '@angular/core';
import { DatePipe } from '@angular/common';
import { TranslateService } from '@ngx-translate/core';

import { DropdownMenuComponent } from 'src/app/shared/components/dropdown-menu/dropdown-menu.component';
import { Factory, CollectionFactory, ErrorResponse, ICollection, FilterBuilder, IMessageChat, IMessage, StatusMessage, TypeMessage } from 'libraries/AonSDK/src/aon';
import { MenuItem } from 'src/app/core/models/interface/menu-item';
import { MessageChatService } from 'src/app/core/services/message-chat.service';
import { MessageService } from 'src/app/core/services/message.service';
import { ModalCreateComponent } from '../components/modal-create/modal-create.component';
import { OptionsService } from 'src/app/shared/services/options.service';
import { ReportingService } from 'src/app/core/services/reporting.service';
import { TablesInboxComponent } from '../components/tables-inbox/table-inbox.component';

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
  @ViewChild(TablesInboxComponent, { static: false })

  collectionFactory = new CollectionFactory();
  entityFactory = new Factory();
  datepipe: DatePipe = new DatePipe(this.translateService.getDefaultLang());
  messages: ICollection<IMessage> =
  new CollectionFactory().createMessageCollection();
  messagesChat: ICollection<IMessageChat> =
  this.collectionFactory.createMessageChatCollection();

  messagesData: IMessage = this.entityFactory.createMessage();
  messageChat: IMessageChat = this.entityFactory.createMessageChat();

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
  selectedFilterText: string = '';
  selectedFilterType: string = '';
  expandedIndex: number = -1;
  newMessageDescription: string = '';
  spinner: boolean = true;
  @ViewChild('menu') dropdownMenuComponent: DropdownMenuComponent =
    new DropdownMenuComponent();

  consultaMessageCount: number = 0;
  tareasMessageCount: number = 0;
  notificacionesMessageCount: number = 0;
  totalMessageCount: number = 0;
  menuItem: MenuItem[] = [];
  selected: string = '';
  items: any[] = [];
  data: any[] = [];

  constructor(
    private translateService: TranslateService,
    public reportingService: ReportingService,
    private messageService: MessageService,
    private messageChatService: MessageChatService,
    private optionsService: OptionsService
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
        this.selectedFilterText =
          this.translateService.instant('INBOX.THIS_WEEK');
      });
  }

  ngOnInit(): void {
    const url = window.location.href.split('/')[4];

    if(url)
      this.messageService.getMessage(url).then((message) => {
        this.rowClickHandler(message);
      });
  }

  ngAfterViewInit(): void {
    this.checkOpenModal();
  }

  /**
  * Comprueba si el parametro "showModal" esta presente en los parametros de la consulta y, en caso afirmativo, muestra el modal.
  *
  * @private
  * @returns {void}
  */
  private checkOpenModal(): void {
    const options = this.optionsService.getOptions();

    if (options && options.showModal) {
      this.optionsService.clearOptions();
      this.showModal();
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

      this.messageService.getMessage(message.key).then((messageStatus) => {

          if (message.type === 'notificacion') {
            if (messageStatus.Status === StatusMessage.NUEVA) {
            messageStatus.Status = StatusMessage.VISTA;
            }
          }

          try {
//            this.messageService.updateMessage(messageStatus);
          } catch (error) {
            throw error instanceof ErrorResponse ?  error : new ErrorResponse(error);
          }

      });
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
        this.selectedFilterText =
          this.translateService.instant('INBOX.THIS_WEEK');
        break;
      case 2:
        this.selectedFilterText =
          this.translateService.instant('INBOX.THIS_MONTH');
        break;
      case 0:
        this.selectedFilterText = this.translateService.instant('INBOX.ALLS');
        break;
      default:
        this.selectedFilterText =
          this.translateService.instant('INBOX.THIS_WEEK');
        break;
    }
  }

  async calculateMessageCounts() {
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
  }

  // Cerrar details
  closeDetail() {
    this.showDetail = false;
  }

  toggleDescription(index: number): void {
    this.expandedIndex = this.expandedIndex === index ? -1 : index;
  }

  // ocultar o ver botones
  changeView(): void {
    this.showSendButton = false;
    this.showSendButtons = false;
  }

  //Crear mensaje de consulta
  newMessageDescriptionChange(newValue: string) {
    this.newMessageDescription = newValue;
  }

  //mensaje de chat de consultas
  async createChatMessage(description: string) {

    if (this.messageChat && this.messagesData) {

      const newMessageChat = this.messageService.objectFactory.createMessageChat()
      .setIdMessage(this.messagesData.Id)
      .setName(this.messagesData.Name)
      .setDescription(description)
      .setType('chat');

        // Crear el mensaje
        const createdMessageChat =
        await this.messageChatService.createMessageChat(newMessageChat);

        // Agregar el nuevo mensaje
        this.messagesChat.add(createdMessageChat);
        // Desactivo el spinner
        this.spinner = false;
    }
  }

  //mensaje de consulta
  async createMessage(description: string){
    if (this.messagesData) {

      const newMessage = this.messageService.objectFactory.createMessage()
      .setName(this.messagesData.Name)
      .setDescription(description)
      .setType(TypeMessage.CONSULTA)
      .setStatus(StatusMessage.CERRADA)
      .setLastMessageChatOrigin(false);

        // Crear el mensaje
        const createdMessage = await this.messageService.createMessage(newMessage);
        this.messagesData = createdMessage;
        // Agregar el nuevo mensaje
        this.messages.add(createdMessage);
        // Desactivo el spinner
        setTimeout(() => {
          this.spinner = false;

        }, 2000)
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
}
