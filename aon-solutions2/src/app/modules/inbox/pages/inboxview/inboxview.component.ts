import { Component, OnInit, ViewChild } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { CollectionFactory, ICollection, FilterBuilder, IMessageChat, IMessage } from 'libraries/AonSDK/aon';
import { ReportingService } from 'src/app/core/services/reporting.service';
import { MessageService } from 'src/app/core/services/message.service';
import { MessageChatService } from 'src/app/core/services/message-chat.service';
import { ModalCreateComponent } from '../../components/inbox/modal-create/modal-create.component';
import { TableQueriesComponent } from '../../components/inbox/table-queries/table-queries.component';

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
  collectionFactory = new CollectionFactory();
  functionHome: any = (result: any) => this.afterModalClosed(result);
  tabsConsultas: Tabs[] = [];
  tabsTareas: Tabs[] = [];
  tabsNotificaciones: Tabs[] = [];
  selectedTab: number = 0;
  tabIndex: number = 0;
  showDetail: boolean = false;
  noTasksMessage: boolean = false;
  isModalVisible: boolean = false;
  messagesChat    : ICollection<IMessageChat> = this.collectionFactory.createMessageChatCollection();
  id: number = 0;
  @ViewChild(TableQueriesComponent, { static: false })
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
        'INBOX.ALL',
        'INBOX.OPENED',
        'INBOX.CLOSED',
        'INBOX.ALL',
        'INBOX.NEWS',
        'INBOX.VIEWS',
      ])
      .subscribe((result) => {
        (this.tabsTareas = [
          { name: result['INBOX.ALL'], color: 'black' },
          { name: result['INBOX.PENDING'], color: 'black', icon: 'remove_circle'},
          { name: result['INBOX.REALIZED'], color: 'black', icon: 'check_circle'},
        ]),
          (this.tabsConsultas = [
            { name: result['INBOX.ALL'], color: 'black' },
            { name: result['INBOX.OPENED'], color: 'black', icon: 'replay' },
            { name: result['INBOX.CLOSED'], color: 'black', icon: 'archive' },
          ]),
          (this.tabsNotificaciones = [
            { name: result['INBOX.ALL'], color: 'black' },
            { name: result['INBOX.NEWS'], color: 'black', icon: 'notifications_active'},
            { name: result['INBOX.VIEWS'], color: 'black', icon: 'remove_red_eye'},
          ]);
      });
  }

  ngOnInit(): void {}

  showNoTasksMessage(hasNoTasks: boolean) {
    this.noTasksMessage = hasNoTasks;
  }

  afterModalClosed(result?: any) {
    console.log(result);
  }

  showModal() {
    this.isModalVisible = true;
    this.modalComponent.openDialog(ModalCreateComponent, this.functionHome, 'Data from home');
  }

  rowClickHandler(message: any) {
    let filterBuilder = new FilterBuilder();
    filterBuilder.addField('idMessage', message.key);

    this.messageChatService
      .getMessageChatList(filterBuilder.getFilter())
      .then((response) => {
        this.messagesChat = response;
    })
    this.showDetail = true;
  }
}
