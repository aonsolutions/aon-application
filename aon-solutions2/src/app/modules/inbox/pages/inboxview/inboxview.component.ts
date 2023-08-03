import { Component, Input, OnInit, ViewChild } from '@angular/core';
import { ModalCreateComponent } from '../../components/inbox/modal-create/modal-create.component';

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
  selectedTab: number = 0;
  tabIndex: number = 0;
  @ViewChild('modal') modalComponent: any = '';

  functionHome: any = (result:any) => this.afterModalClosed(result);

  constructor() { }

  ngOnInit(): void {
  }

  tabsConsultas: Tabs[] = [
    { name: 'Todas', color: 'black'},
    { name: 'Abiertas', color: 'black',icon : 'replay'},
    { name: 'Cerradas', color: 'black',icon : 'archive' }
  ];
  tabsTareas: Tabs[] = [
    { name: 'Todas', color: 'black'},
    { name: 'Pendientes', color: 'black', icon : 'remove_circle'},
    { name: 'Realizadas', color: 'black', icon : 'check_circle'}
  ];
  tabsNotificaciones: Tabs[] = [
    { name: 'Todas', color: 'black'},
    { name: 'Nuevas', color: 'black',icon : 'notifications_active'},
    { name: 'Vistas', color: 'black',icon : 'remove_red_eye'}
  ];

  afterModalClosed(result?: any){
    console.log(result);
  }

  showModal(){
    this.modalComponent.openDialog(ModalCreateComponent,this.functionHome, 'Data from home');
  }
}
