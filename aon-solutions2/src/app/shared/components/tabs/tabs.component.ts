import { Component, EventEmitter, HostBinding, Input, OnInit, Output } from '@angular/core';
import { MatTabChangeEvent } from '@angular/material/tabs';
import { Tabs } from 'src/app/core/models/interface/tabs';

@Component({
  selector: 'app-tabs',
  templateUrl: './tabs.component.html',
  styleUrls: ['./tabs.component.scss'],
})
export class TabsComponent implements OnInit {

  @Input() paddingLeftHeader: string = '';
  @Input() tabColor: string = ''
  @HostBinding('style.--styleTabColor') styleTabColor = '';
  tabIndex: number = 0;
  @Output() changeTabIndex = new EventEmitter<number>();
  @Input() tabs: Tabs [] = [];

  tabChanged(tabChangeEvent: MatTabChangeEvent): void {
    this.tabIndex = tabChangeEvent.index;
    this.changeTabIndex.emit(tabChangeEvent.index);
  }

  constructor() {
  }

  ngOnInit(): void {
    this.styleTabColor = this.tabColor
  }

}
