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
  tabs: Array<{ header: string; icon: string; type: number }> = [];
  items: Message[] = [];
  selectedTab: number = 2;
  selectedFilter: string = 'ESTA SEMANA';
  itemsFilter: string[] = ['ESTA SEMANA', 'ESTE MES', 'TODO'];
  mainTabContent: string = '';
  detail: string = '';
  detailOpen: string = 'detailOpen';
  detailClose: string = 'detailClose';
  mainTabContentOpen: string = 'mainTabContentOpen';
  mainTabContentClose: string = 'mainTabContentClose';
  hiddenDetail: boolean = false;
  sendItem: Message = new Message();
  initialElement: number = 1;
  finalElement: number = 20;
  totalElement: number = 0;
  searchWidth: string = '40%';

  constructor(private inboxService: InboxService) {}

  // Valores por defecto para llamar a la primera tab y rellenar el contenido, así como ocultar la vista detalle
  ngOnInit(): void {
    this.selectedTab = 2;
    this.chooseBetweenTabs();
    this.inboxService.getMessages(this.selectedMenu, this.selectedTab);
    this.mainTabContent = this.mainTabContentClose;
    this.detail = this.detailClose;
  }

  // listener para comprobar si hay algun cambio, de ser asi comprueba el tab elegido y los mensajes según su filtro(el filtro en un futuro)
  ngOnChanges(changes: SimpleChanges): void {
    this.chooseBetweenTabs();
    this.callGetMessages();
  }

  // Abre la vista de detalle de un mensaje
  showDetail(item: Message) {
    this.mainTabContent = this.mainTabContentOpen;
    this.detail = this.detailOpen;
    this.hiddenDetail = true;
    this.sendItem = item;
    this.searchWidth = '70%';
  }

  // Cierra la vista de detalle de un mensaje
  closeDetail() {
    this.mainTabContent = this.mainTabContentClose;
    this.detail = this.detailClose;
    this.hiddenDetail = false;
    this.searchWidth = '40%';
  }

  // futura llamada a la api para traer los mensajes, se ahce a traves del servicio inboxService
  callGetMessages() {
    this.items = this.inboxService.getMessages(
      this.selectedMenu,
      this.selectedTab
    );
    this.totalElement = this.items.length;
    if (this.finalElement > this.totalElement)
      this.finalElement = this.totalElement;
  }

  // Permite volver una página atrás y calcula los números a mostrar
  prevPag() {
    if (this.initialElement != 1) {
      if(this.finalElement == this.totalElement){
        this.finalElement = this.initialElement-1;
        this.initialElement-=20;
      }else{
        this.initialElement-=20;
        this.finalElement-=20;
      }
    }
    // callAPI
  }

  // Permite avanzar una página y calcula los números a mostrar
  nextPag() {
    if (this.finalElement != this.totalElement) {
      if((this.finalElement + 20) < this.totalElement){
        this.initialElement+=20;
        this.finalElement+=20;
      }else{
        this.initialElement+=20;
        this.finalElement=this.totalElement;
      }
    }
    // callAPI
  }

  //Calcula la distribucion de las columnas de los grids para los mensajes dependiendo si la vista detalle esta abierta o no
  calcTiles(item: Message): any {
    if (this.hiddenDetail != true) {
      if (item.type == 3) {
        return [5, 5, 14];
      } else {
        return [5, 5, 8, 5, 1];
      }
    } else {
      if (item.type == 3) {
        return [5, 7, 12];
      } else {
        return [5, 7, 6, 6, 0];
      }
    }
  }

  // Permite ocultar la parte final de los mensajes de notificaciones(la parte de la fecha), ya que estos son el unico tipo de mensaje que no lo incluye
  calcAllTiles(item: Message): any {
    if (this.hiddenDetail != true) {
      if (item.type == 3) {
        return false;
      } else {
        return true;
      }
    } else {
      if (item.type == 3) {
        return false;
      } else {
        return true;
      }
    }
  }

  //permite navegas entre las diferentes tabs que hacen falta en el modulo inbox
  chooseBetweenTabs() {
    switch (this.selectedMenu) {
      case 'bandeja': {
        this.tabs = [
          {
            header: 'Todas',
            icon: '',
            type: 2,
          },
        ];
        break;
      }
      case 'consultas': {
        this.tabs = [
          {
            header: 'Todas',
            icon: '',
            type: 2,
          },
          {
            header: 'Abiertas',
            icon: 'replay',
            type: 1,
          },
          {
            header: 'Cerradas',
            icon: 'archive',
            type: 0,
          },
        ];
        break;
      }
      case 'tareas': {
        this.tabs = [
          {
            header: 'Todas',
            icon: '',
            type: 2,
          },
          {
            header: 'Pendientes',
            icon: 'remove_circle',
            type: 1,
          },
          {
            header: 'Realizadas',
            icon: 'check_circle',
            type: 0,
          },
        ];
        break;
      }
      case 'notificaciones': {
        this.tabs = [
          {
            header: 'Todas',
            icon: '',
            type: 2,
          },
          {
            header: 'Nuevas',
            icon: 'notifications_active',
            type: 1,
          },
          {
            header: 'Vistas',
            icon: 'remove_red_eye',
            type: 0,
          },
        ];
        break;
      }
    }
  }
}
