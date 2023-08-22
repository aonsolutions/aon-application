import { Component, Input, OnInit, ViewChild } from '@angular/core';
import { ModalCreateComponent } from '../../components/inbox/modal-create/modal-create.component';
import { TranslateService } from '@ngx-translate/core';

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
  tabsConsultas: Tabs[] = [];
  tabsTareas: Tabs[] = [];
  tabsNotificaciones: Tabs[] = [];
  selectedTab: number = 0;
  tabIndex: number = 0;
  @ViewChild('modal') modalComponent: any = '';

  functionHome: any = (result:any) => this.afterModalClosed(result);

  constructor(private translateService: TranslateService) {
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

  ngOnInit(): void {
  }

  afterModalClosed(result?: any){
    console.log(result);
  }

  showModal(){
    this.modalComponent.openDialog(ModalCreateComponent,this.functionHome, 'Data from home');
  }
}
