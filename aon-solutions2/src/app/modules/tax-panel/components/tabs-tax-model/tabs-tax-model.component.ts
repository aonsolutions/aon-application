import { Component, EventEmitter, HostBinding, Input, OnInit, Output } from '@angular/core';
import { MatTabChangeEvent } from '@angular/material/tabs';




export interface Tabs {
  name: string; // Nombre de la tab
  icon?: string; // Icono opcional de la tab
  color?: string; // Color del texto y del icono de la tab
}

@Component({
  selector: 'app-tabs-tax-model',
  templateUrl: './tabs-tax-model.component.html',
  styleUrls: ['./tabs-tax-model.component.scss']
})
export class TabsTaxModelComponent implements OnInit {


  tabIndex:number = 0

  tabs: Tabs[] = [
    { name: 'tab1', icon: 'create', color: 'black' },
    { name: 'tab2', icon: 'delete', color: 'black' },
    { name: 'tab3', color: 'black' },
    { name: 'todos', color: 'black' },
  ];

  @Input() paddingLeftHeader: string = '';
  @Input() tabColor: string = ''
  @HostBinding('style.--styleTabColor') styleTabColor = '';
  @Output() changeTabIndex = new EventEmitter<number>();


  tabChanged(tabChangeEvent: MatTabChangeEvent): void {
    this.tabIndex = tabChangeEvent.index;
    this.changeTabIndex.emit(tabChangeEvent.index);
  }


  constructor() { }

  ngOnInit(): void {
  }

}
