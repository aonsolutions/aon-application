import { Component, Input, OnInit, Output, SimpleChanges, ViewChild } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { ICollection, IMessage } from 'libraries/AonSDK/aon';
import { CollectionFactory } from '../../../../../../libraries/AonSDK/aon';
import { BehaviorSubject } from 'rxjs';
import { ReportingService } from 'src/app/core/services/reporting.service';
import { MessageService } from 'src/app/core/services/message.service';

export interface Tabs {
  name: string;
  icon?: string;
  color?: string;
}
@Component({
  selector: 'app-inboxview',
  templateUrl: './inboxview.component.html',
  styleUrls: ['./inboxview.component.scss']
})
export class InboxviewComponent implements OnInit {
  tabsConsultas     : Tabs[]  = [];
  tabsTareas        : Tabs[]  = [];
  tabsNotificaciones: Tabs[]  = [];
  selectedTab       : number  = 0;
  tabIndex          : number  = 0;
  showDetail        : boolean = false;
  noTasksMessage    : boolean = false;

  constructor(
    private translateService: TranslateService,
    public reportingService: ReportingService,
    private messageService: MessageService,
    ) {

    this.translateService.get(
      ['INBOX.ALL', 'INBOX.OPENED', 'INBOX.CLOSED']
    ).subscribe( result => {
      this.tabsConsultas = [
        { name: result['INBOX.ALL'], color: 'black'},
        { name: result['INBOX.OPENED'], color: 'black',icon : 'replay'},
        { name: result['INBOX.CLOSED'], color: 'black',icon : 'archive' }
      ]
    })

    this.translateService.get(
      ['INBOX.ALL', 'INBOX.PENDING', 'INBOX.REALIZED']
    ).subscribe( result => {
      this.tabsTareas = [
        { name: result['INBOX.ALL'], color: 'black'},
        { name: result['INBOX.PENDING'], color: 'black',icon : 'replay'},
        { name: result['INBOX.REALIZED'], color: 'black',icon : 'archive' }
      ]
    })

    this.translateService.get(
      ['INBOX.ALL', 'INBOX.NEWS', 'INBOX.VIEWS']
    ).subscribe( result => {
      this.tabsNotificaciones = [
        { name: result['INBOX.ALL'], color: 'black'},
        { name: result['INBOX.NEWS'], color: 'black',icon : 'replay'},
        { name: result['INBOX.VIEWS'], color: 'black',icon : 'archive' }
      ]
    })
  }

  public collectionFactory = new CollectionFactory();

    //Inbox area
    messages: ICollection<IMessage> =
    this.collectionFactory.createMessageCollection();
  private messagesSubject = new BehaviorSubject<ICollection<IMessage>>(
    this.collectionFactory.createMessageCollection()
  );
  public messages$ = this.messagesSubject.asObservable();

  ngOnInit(): void {

    //MessageService
    this.messageService.getMessageList().then((response) => {
      this.messages = response;
      this.messagesSubject.next(this.messages);
    });

  }

  showNoTasksMessage(hasNoTasks: boolean) {
    this.noTasksMessage = hasNoTasks;
  }

  openCreateQueryComponent() {
    this.selectedTab = 4;
  }

  //  const clickTr = document.querySelector("tr");

  //  clickTr.addEventListener("click", (event) => {
  //    showDetail = true,
  //  });

}
