import { Component, Input, OnInit, ViewChild } from '@angular/core';
import { MatMenuTrigger } from '@angular/material/menu';
import { MenuItem } from 'src/app/core/models/interface/menu-item';
@Component({
  selector: 'app-dropdown-menu',
  templateUrl: './dropdown-menu.component.html',
  styleUrls: ['./dropdown-menu.component.scss']
})
export class DropdownMenuComponent implements OnInit {

  @Input() submenu: MenuItem = {root:true, text:''}
  @Input() menuItem: MenuItem [] = [{root:false,text:''}];
  @ViewChild(MatMenuTrigger) trigger: MatMenuTrigger | undefined;

  openDropdownMenu() {
    this.trigger?.openMenu();
  }

  closeDropdownMenu(){
    this.trigger?.closeMenu();
  }

  constructor() {
  }

  ngOnInit(): void {
  }

}
