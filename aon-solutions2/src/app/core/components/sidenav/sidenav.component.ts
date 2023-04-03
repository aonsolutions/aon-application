import { Component, OnInit, EventEmitter, Output } from '@angular/core';
import { Shortcut } from '../../models/shortcut';
import { MenuButton } from '../../models/menu-button';
import { SidenavService } from '../sidenav/sidenav.service';

@Component({
  selector: 'app-sidenav',
  templateUrl: './sidenav.component.html',
  styleUrls: ['./sidenav.component.scss']
})

export class SidenavComponent implements OnInit {

  subMenuOpened: boolean;
  @Output() onSelected = new EventEmitter<any>();

  constructor(public service : SidenavService){
    this.subMenuOpened = false;
    this.service.opened = false;
  }

  items: MenuButton[] = [
    {routerlink: 'inbox', shape: 'inbox', color: 'alt-attention', text: 'BANDEJA'},
    {routerlink: 'billing', shape: 'assessment', color: 'primary', text: 'FACTURACIÓN'},
    {routerlink: 'tax-panel', shape: 'euro_symbol', color: 'attention', text: 'PANEL DE IMPUESTOS'},
    {routerlink: 'employee-panel', shape: 'people', color: 'alt-positive', text: 'PANEL DE EMPLEADOS'},
    {routerlink: 'documentation', shape: 'description', color: 'alt-warning', text: 'DOCUMENTACIÓN'},
    {routerlink: 'consulting', shape: 'work', color: 'alt-primary', text: 'ASESORÍA'}
  ];

  shortcuts: Shortcut[] = [
    {routerlink: 'home', shape: 'inbox'},
    {routerlink: 'home', shape: 'assessment'},
    {routerlink: 'home', shape: 'inbox'},
    {routerlink: 'home', shape: 'assessment'},
    {routerlink: 'home', shape: 'inbox'},
    {routerlink: 'home', shape: 'assessment'}
  ];

  ngOnInit(): void {}
  
  onSelectedProduct(selected:any) {
    this.onSelected.emit(selected);
  }

}
