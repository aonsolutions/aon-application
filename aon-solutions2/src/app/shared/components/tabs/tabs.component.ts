import { Component, EventEmitter, HostBinding, Input, OnInit, Output } from '@angular/core';
import { MatTabChangeEvent } from '@angular/material/tabs';
import { Tabs } from 'src/app/core/models/interface/tabs';

@Component({
  selector: 'app-tabs',
  templateUrl: './tabs.component.html',
  styleUrls: ['./tabs.component.scss'],
})
export class TabsComponent implements OnInit {
  tabIndex                  : number  = 0;
  @Input() paddingLeftHeader: string  = '';
  @Input() tabColor         : string  = ''
  @Input() tabs             : Tabs [] = [];
  @Output() changeTabIndex = new EventEmitter<number>();
//  @HostBinding('style.--styleTabColor') styleTabColor = '';

  constructor() {
  }
  
  ngOnInit(): void {
    
//    this.styleTabColor = this.tabColor
  }
  
  tabChanged(tabChangeEvent: MatTabChangeEvent): void {
    this.tabIndex = tabChangeEvent.index;
    this.changeTabIndex.emit(tabChangeEvent.index);
  }
}
