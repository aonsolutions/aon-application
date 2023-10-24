import { Component, OnInit, EventEmitter, Output, ViewChild, Input} from '@angular/core';
import { Router, NavigationEnd } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';

import { MenuButton } from 'src/app/core/models/interface/menu-button';
import { OptionsService } from '../../services/options.service';
import { Shortcut } from 'src/app/core/models/interface/shortcut';
import { ModalCreateComponent } from 'src/app/modules/inbox/components/modal-create/modal-create.component';

@Component({
  selector: 'app-side-nav',
  templateUrl: './side-nav.component.html',
  styleUrls: ['./side-nav.component.scss'],
})
export class SideNavComponent implements OnInit {
  items: MenuButton[] = [];
  shortcuts: Shortcut[] = [];
  selectedProduct: boolean = false;
  opened: boolean;
  resize: number;

  currentRoute: string = this.router.url.replace('/', '');
  subMenuOpened: boolean;
  @Input() hide: boolean;
  @Output() onSelected = new EventEmitter<any>();
  @ViewChild('iconHover') iconHover: any;
  @ViewChild('modalCreateInbox') modalCreateInboxComponent: any = '';
  functionHome: any = (result: any) => this.afterModalClosed(result);

  constructor(
    private optionsService: OptionsService,
    private router: Router,
    private translateService: TranslateService
  ) {
    this.hide = false;
    this.opened = false;
    this.resize = 1;
    this.subMenuOpened = false;

    this.router.events.subscribe((event) => {
      event instanceof NavigationEnd ? this.checkCurrentRoute() : null;
    });

    this.translateService
      .get([
        'MENU.INBOX',
        'MENU.BILLING',
        'MENU.TAX_PANEL',
        'MENU.EMPLOYEE_PANEL',
        'MENU.DOCUMENTATION',
        'HOME.CREATE_QUERY',
      ])
      .subscribe((result) => {
        this.items = [
          { routerlink: 'inbox', shape: 'inbox', text: result['MENU.INBOX'], class: 'red', selected: false, },
          { routerlink: 'billing', shape: 'assessment', text: result['MENU.BILLING'], class: 'blue', selected: false,},
          { routerlink: 'tax-panel', shape: 'euro_symbol', text: result['MENU.TAX_PANEL'], class: 'orange', selected: false, },
          { routerlink: 'employee-panel', shape: 'people', text: result['MENU.EMPLOYEE_PANEL'], class: 'green', selected: false, },
          { routerlink: 'documentation', shape: 'description', text: result['MENU.DOCUMENTATION'], class: 'pink', selected: false, },
          //    {routerlink: 'consulting',      shape: 'work',        text: 'ASESORÍA',           class: 'purple',  selected: false}
        ];
        this.shortcuts = [
          { routerlink: 'home', shape: 'add_box', toolTip: 'En construcción' },
          { routerlink: 'tax-panel', shape: 'person_add', toolTip: 'En construcción', },
          { routerlink: 'inbox/create', shape: 'add_comment', toolTip: result['HOME.CREATE_QUERY'], options: { showModal: true }, },
          { routerlink: 'tax-panel', shape: 'alarm', toolTip: 'En construcción', },
        ];
      });
  }

  ngOnInit(): void {
    this.items.forEach((element) => {
      if (element.routerlink == this.currentRoute) {
        element.selected = true;
      }
    });
  }

  setOpened(state: boolean) {
    this.opened = state;
  }

  setResize(state: number) {
    this.resize = state;
  }

  onSelectedProduct(selected: any) {
    this.selectedProduct = selected;
    // Coger el ancho del menu, para saber donde posicionar el boton
    setTimeout(function () {
      let menu = document.getElementById('menu-left'); // Menu
      let button = document.getElementById('button-open'); // Button open menu
      if (selected && menu && button) {
        // Cogemos el ancho del menu abierto, menos lo que ocupa el boton (teniendo en cuenta su borde)
        const menuWidth = menu.getBoundingClientRect().width - 13;
        // Posicionamos el boton
        button.style.left = menuWidth + 'px';
      }
    }, 1);
  }

  checkCurrentRoute() {
    let exist = false;
    this.items.forEach((element) => {
      if (this.router.url.split('/', 2)[1] === element.routerlink) {
        exist = true;
      }
    });
    if (!exist) {
      this.deselectAll();
    }
  }

  select(item: MenuButton) {
    this.items.forEach((element) => {
      if (element.routerlink == item.routerlink) {
        // Si seleccionamos una vista, volvemos a reducir
        if (this.opened === true) this.opened = false;
        // La ruta seleccionada
        element.selected = true;
        this.showModal();
      }
    });
  }

  afterModalClosed(result?: any) {
    console.log(result);
  }

  showModal() {
    this.modalCreateInboxComponent.openDialog(
      ModalCreateComponent,
      this.functionHome
    );
  }

  deselectAll() {
    this.items.forEach((element) => {
      element.selected = false;
    });
  }

  setOptions(data: any) {
    this.optionsService.setOptions(data.options);
  }
}
