import { Component, OnInit, EventEmitter, Output, ViewChild } from '@angular/core';
import { Shortcut } from '../../models/shortcut';
import { MenuButton } from '../../models/menu-button';
import { SidenavService } from '../sidenav/sidenav.service';
import { NavigationEnd, Router } from '@angular/router';

@Component({
  selector: 'app-sidenav',
  templateUrl: './sidenav.component.html',
  styleUrls: ['./sidenav.component.scss']
})

export class SidenavComponent implements OnInit {

  currentRoute: string = this.router.url.replace('/','');
  subMenuOpened: boolean;
  @Output() onSelected = new EventEmitter<any>();
  @ViewChild('iconHover') iconHover : any; 

  constructor(public service : SidenavService, private router: Router){
    this.router.events.subscribe((event) => {
      event instanceof NavigationEnd ? this.currentRoute = this.router.url.replace('/','') : null     
    })
    this.subMenuOpened = false;
    this.service.opened = false;
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

  ngOnInit(): void {
    this.items.forEach(element => {
      if(element.routerlink == this.currentRoute){
        element.selected = "true";
      }
    });
  }
  
  onSelectedProduct(selected:any) {
    this.onSelected.emit(selected);
  }

  select(item: MenuButton){
    this.items.forEach(element => {
      if(element.routerlink == item.routerlink){
        element.selected = "true";
      }
    });
    console.log(this.iconHover.nativeElement.value);
  }

  deselectAll(){
    this.items.forEach(element => {
      element.selected = "false";
    });
  }
}
