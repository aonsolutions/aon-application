import { Component, OnInit, ViewChild } from '@angular/core';
import { NavigationEnd, Router } from '@angular/router';
import { DropdownMenuComponent } from '../dropdown-menu/dropdown-menu.component';
import { MenuItem } from 'src/app/core/models/interface/menu-item';

@Component({
  selector: 'app-breadcumb',
  templateUrl: './breadcumb.component.html',
  styleUrls: ['./breadcumb.component.scss']
})
export class BreadcumbComponent implements OnInit {

  @ViewChild('enterpriseSelector') dropdownMenuComponent: DropdownMenuComponent = new DropdownMenuComponent;
  currentRoute: string = this.router.url.replace('/','');
  selectedEnterprise: string = 'Nombre de empresa 1';
  menuItem: MenuItem [] = [
    {root:true, text: 'Nombre de empresa 1', click: () => this.setEnterpriseSelected('Nombre de empresa 1')},
    {root:true, text: 'Nombre de empresa 2', click: () => this.setEnterpriseSelected('Nombre de empresa 2')},
    {root:true, text: 'Nombre de empresa 3', click: () => this.setEnterpriseSelected('Nombre de empresa 3')},
    {root:true, text: 'Nombre de empresa 4', click: () => this.setEnterpriseSelected('Nombre de empresa 4')},
  ];

  constructor(private router: Router) {
    this.router.events.subscribe((event) => {       
      event instanceof NavigationEnd ? this.checkCurrentRoute() : null     
    })
  }
  
  ngOnInit(): void {
  }

  checkCurrentRoute() {
    this.currentRoute = this.router.url.replace('/','');
  }

  setEnterpriseSelected(value : any){
    this.selectedEnterprise = value;
  }

}
