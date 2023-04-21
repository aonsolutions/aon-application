import {
  Component,
  Input,
  OnChanges,
  OnInit,
  SimpleChanges,
} from '@angular/core';
import { Message } from 'src/app/core/models/message';
import { InboxService } from '../../services/inbox.service';


@Component({
  selector: 'app-content-inbox',
  templateUrl: './content-inbox.component.html',
  styleUrls: ['./content-inbox.component.scss'],
  host: {
    '[style.width]': "'100%'",
    '[style.height]': "'100%'",
  },
})
export class ContentInboxComponent implements OnInit, OnChanges {

  @Input() selectedMenu: string = '';
  tabs: Array<{ header: string; icon: string; type: number}> = [];
  items : Message[] = [];
  selectedTab: number = 2;
  selectedFilter : string = "ESTA SEMANA";
  itemsFilter : string[] = ["ESTA SEMANA","ESTE MES","TODO"];
  mainTabContent : string = '';
  detail : string = '';
  detailOpen : string = 'detailOpen';
  detailClose : string = 'detailClose';
  mainTabContentOpen : string = 'mainTabContentOpen';
  mainTabContentClose : string = 'mainTabContentClose';
  hiddenDetail: boolean = false;
  sendItem: Message = new Message();


  constructor(private inboxService: InboxService) {}

  ngOnInit(): void {
    this.selectedTab = 2;
    this.chooseBetweenTabs();
    this.inboxService.getMessages(this.selectedMenu,this.selectedTab);
    this.mainTabContent = this.mainTabContentClose;
    this.detail = this.detailClose;
  }

  ngOnChanges(changes: SimpleChanges): void {
    this.chooseBetweenTabs();
    this.callGetMessages();
  }

  showDetail(item : Message){
    this.mainTabContent = this.mainTabContentOpen;
    this.detail = this.detailOpen;
    this.hiddenDetail = true;
    this.sendItem = item;
  }

  closeDetail(){
    this.mainTabContent = this.mainTabContentClose;
    this.detail = this.detailClose;
    this.hiddenDetail = false; 
  }

  callGetMessages(){
    this.items = this.inboxService.getMessages(this.selectedMenu,this.selectedTab);
  }

  calcTiles(item: Message): any {
    if(this.hiddenDetail != true){
      if(item.type == 3){
        return [5,5,14];
      } else {
        return [5,5,8,5,1];
      }
    }else{
      if(item.type == 3){
        return [5,7,12];
      } else {
        return [5,7,6,6,0];
      }
    }
  }

  calcAllTiles(item: Message): any{
    if(this.hiddenDetail != true){
      if(item.type == 3){
        return false;
      }else{
        return true;
      }
    }else{
      if(item.type == 3){
        return false;
      }else{
        return true;
      }
    }
    
  }

  chooseBetweenTabs() {
    switch (this.selectedMenu) {
      case 'bandeja': {
        this.tabs = [
          {
            header: 'Todas',
            icon: '',
            type: 2
          },
        ];
        break;
      }
      case 'consultas': {
        this.tabs = [
          {
            header: 'Todas',
            icon: '',
            type: 2
          },
          {
            header: 'Abiertas',
            icon: 'replay',
            type: 1
          },
          {
            header: 'Cerradas',
            icon: 'archive',
            type: 0
          },
        ];
        break;
      }
      case 'tareas': {
        this.tabs = [
          {
            header: 'Todas',
            icon: '',
            type: 2
          },
          {
            header: 'Pendientes',
            icon: 'remove_circle',
            type: 1
          },
          {
            header: 'Realizadas',
            icon: 'check_circle',
            type: 0
          },
        ];
        break;
      }
      case 'notificaciones': {
        this.tabs = [
          {
            header: 'Todas',
            icon: '',
            type: 2
          },
          {
            header: 'Nuevas',
            icon: 'notifications_active',
            type: 1
          },
          {
            header: 'Vistas',
            icon: 'remove_red_eye',
            type: 0
          },
        ];
        break;
      }
    }
  }
}
