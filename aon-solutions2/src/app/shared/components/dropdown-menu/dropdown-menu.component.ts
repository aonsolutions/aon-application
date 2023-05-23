import { Component, EventEmitter, Input, OnInit, Output, ViewChild } from '@angular/core';
import { MatMenu, MatMenuTrigger } from '@angular/material/menu';

export interface MenuItem {
  root: boolean;
  text: string;
  icon?: string;
  colorIcon?: string;
  routerlink?: string;
  value?: number;
  children?: MenuItem[];
}

@Component({
  selector: 'app-dropdown-menu',
  templateUrl: './dropdown-menu.component.html',
  styleUrls: ['./dropdown-menu.component.scss']
})
export class DropdownMenuComponent implements OnInit {

  @Input() submenu: MenuItem = {root:true, text:''}
  @Output() getValue = new EventEmitter<number>();
  @Input() menuItem: MenuItem [] = [{root:false,text:''}];
  @ViewChild(MatMenuTrigger) trigger: MatMenuTrigger | undefined;

  someMethod() {
    this.trigger?.openMenu();
  }

  constructor() {
  }

  ngOnInit(): void {
  }

  sendValue(value: number) {
    this.getValue.emit(value);
  }

}
