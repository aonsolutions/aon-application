import { Component, EventEmitter, HostBinding, Input, OnInit, Output } from '@angular/core';



export interface Tabs {
  name: string; // Nombre de la tab
}

@Component({
  selector: 'app-tabs-tax-model',
  templateUrl: './tabs-tax-model.component.html',
  styleUrls: ['./tabs-tax-model.component.scss']
})
export class TabsTaxModelComponent implements OnInit {

  asd:any;
getValue($event: any) {

}


  tabIndex:number = 0

  tabs: Tabs[] = [
    { name: '1 Trimestre'},
    { name: '2 Trimestre' },
    { name: '3 Trimestre'},
    { name: '4 Trimestre'},
    { name: 'Todos'},
  ];

  @Input() paddingLeftHeader: string = '';
  @Input() tabColor: string = ''
  @HostBinding('style.--styleTabColor') styleTabColor = '';
  @Output() changeTabIndex = new EventEmitter<number>();




  constructor() { }

  ngOnInit(): void {
  }

}
