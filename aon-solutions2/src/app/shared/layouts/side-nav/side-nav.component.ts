import { Component, OnInit, EventEmitter, Output, ViewChild, Input } from '@angular/core';
import { Shortcut } from 'src/app/core/models/interface/shortcut';
import { MenuButton } from 'src/app/core/models/interface/menu-button';
import { NavigationEnd, Router } from '@angular/router';

@Component({
  selector: 'app-side-nav',
  templateUrl: './side-nav.component.html',
  styleUrls: ['./side-nav.component.scss']
})
export class SideNavComponent implements OnInit {

  selectedProduct: any;
  opened : boolean;
  resize : number;

  currentRoute: string = this.router.url.replace('/','');
  subMenuOpened: boolean;
  @Input() hide : boolean;
  @Output() onSelected = new EventEmitter<any>();
  @ViewChild('iconHover') iconHover : any;
 
  constructor(private router: Router) {
    this.hide   = false;
    this.opened = true;
    this.resize = 1;
    this.router.events.subscribe((event) => {
      event instanceof NavigationEnd ? this.currentRoute = this.router.url.replace('/','') : null
    })
    this.subMenuOpened = false;
    this.opened = false;
  }

  ngOnInit(): void {
    this.items.forEach(element => {
      if(element.routerlink == this.currentRoute){
        element.selected = true;
      }
    });
  }

  setOpened(state:boolean){
    this.opened = state;
  }

  setResize(state:number){
    this.resize = state
  }

  onSelectedProduct(selected:any) {
    this.selectedProduct = selected;
      // Coger el ancho del menu, para saber donde posicionar el boton
      setTimeout(function(){
        let menu    = document.getElementById('menu-left');   // Menu
        let button  = document.getElementById('button-open'); // Button open menu
        if(selected && menu && button){
          // Cogemos el ancho del menu abierto, menos lo que ocupa el boton (teniendo en cuenta su borde)
          const menuWidth = menu.getBoundingClientRect().width - 13;
          // Posicionamos el boton
          button.style.left = menuWidth+"px";
        }
      }, 1);
  }

  items: MenuButton[] = [
    {routerlink: 'inbox',           shape: 'inbox',       text: 'BANDEJA',            class: 'red',     selected: false},
    {routerlink: 'billing',         shape: 'assessment',  text: 'GESTIÓN',            class: 'blue',    selected: false},
    {routerlink: 'tax-panel',       shape: 'euro_symbol', text: 'PANEL DE IMPUESTOS', class: 'orange',  selected: false},
    {routerlink: 'employee-panel',  shape: 'people',      text: 'PANEL DE EMPLEADOS', class: 'green',   selected: false},
    {routerlink: 'documentation',   shape: 'description', text: 'DOCUMENTACIÓN',      class: 'pink',    selected: false},
//    {routerlink: 'consulting',      shape: 'work',        text: 'ASESORÍA',           class: 'purple',  selected: false}
  ];
  
  shortcuts: Shortcut[] = [
    {routerlink: 'home', shape: 'receipt'},
    {routerlink: 'home', shape: 'add_box'},
    {routerlink: 'home', shape: 'person_add'},
    {routerlink: 'home', shape: 'add_comment'},
    {routerlink: 'home', shape: 'add_shopping_cart'},
    {routerlink: 'home', shape: 'alarm'}
  ];

  select(item: MenuButton){
    this.items.forEach(element => {
      if(element.routerlink == item.routerlink){
        element.selected = true;
      }
    });
  }

  deselectAll(){
    this.items.forEach(element => {
      element.selected = false;
    });
  }

}
