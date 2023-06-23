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
    this.hide = false;
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
        element.selected = "true";
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
  }

  items: MenuButton[] = [
    {routerlink: 'inbox', shape: 'inbox', text: 'BANDEJA', color: '#f6655a', hover: '#feedec', selected: 'false'},
    {routerlink: 'billing', shape: 'assessment', text: 'FACTURACIÓN', color: '#4f91ff', hover: '#ebf2ff', selected: 'false'},
    {routerlink: 'tax-panel', shape: 'euro_symbol', text: 'PANEL DE IMPUESTOS', color: '#fb982e', hover: '#fef3e7', selected: 'false'},
    {routerlink: 'employee-panel', shape: 'people', text: 'PANEL DE EMPLEADOS', color: '#33a9a9', hover: '#e8f5f5', selected: 'false'},
    {routerlink: 'documentation', shape: 'description', text: 'DOCUMENTACIÓN', color: '#ef6292', hover: '#fdedf2', selected: 'false'},
    {routerlink: 'consulting', shape: 'work', text: 'ASESORÍA', color: '#7985cb', hover: '#f0f1f9', selected: 'false'}
  ];

  shortcuts: Shortcut[] = [
    {routerlink: 'home', shape: 'inbox'},
    {routerlink: 'home', shape: 'assessment'},
    {routerlink: 'home', shape: 'inbox'},
    {routerlink: 'home', shape: 'assessment'},
    {routerlink: 'home', shape: 'inbox'},
    {routerlink: 'home', shape: 'assessment'}
  ];

  select(item: MenuButton){
    this.items.forEach(element => {
      if(element.routerlink == item.routerlink){
        element.selected = "true";
      }
    });
  }

  deselectAll(){
    this.items.forEach(element => {
      element.selected = "false";
    });
  }

}
