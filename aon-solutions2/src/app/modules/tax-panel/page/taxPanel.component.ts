import { Component, OnInit } from '@angular/core';


export interface Tabs {
  name: string; // Nombre de la tab
  icon?: string; // Icono opcional de la tab
  color?: string; // Color del texto y del icono de la tab
}


@Component({
  selector: 'app-taxPanel',
  templateUrl: './taxPanel.component.html',
  styleUrls: ['./taxPanel.component.scss']
})




export class TaxPanelComponent implements OnInit {

  tabIndex:number = 0

  tabs: Tabs[] = [
    { name: 'tab1', icon: 'create', color: 'black' },
    { name: 'tab2', icon: 'delete', color: 'black' },
    { name: 'tab3', color: 'black' },
    { name: 'todos', color: 'black' },
  ];


  constructor() { }

  ngOnInit() {
  }

}
