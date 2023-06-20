import { Component, OnInit, ViewChild } from '@angular/core';
import { NavigationEnd, Router } from '@angular/router';
import { DropdownMenuComponent } from '../dropdown-menu/dropdown-menu.component';
import { MenuItem } from 'src/app/core/models/interface/menu-item';
import { EnterpriseService } from 'src/app/core/services/enterprise.service';

@Component({
  selector: 'app-breadcumb',
  templateUrl: './breadcumb.component.html',
  styleUrls: ['./breadcumb.component.scss']
})
export class BreadcumbComponent implements OnInit {

  @ViewChild('enterpriseSelector') dropdownMenuComponent: DropdownMenuComponent = new DropdownMenuComponent;
  currentRoute: string = this.router.url.replace('/','');
  selectedEnterprise: string = '';
  menuItem: MenuItem [] = []
  haveData: boolean = false

  constructor(private router: Router, public enterpriseService: EnterpriseService) {
    this.router.events.subscribe((event) => {       
      event instanceof NavigationEnd ? this.checkCurrentRoute() : null     
    })
  }
  
  ngOnInit(): void {
    this.enterpriseService.getEnterprises()
    .then(
      (response:any) => {
        let actualDocument = this.enterpriseService.getEnterpriseSelected();
        for(let i = 0; i < response.length; i++){
          this.menuItem.push({root:true, text:response[i].name, click: () => this.setEnterpriseSelected(response[i].name,response[i].document)})
          if(response[i].document == actualDocument) this.selectedEnterprise = response[i].name;
        }
        this.haveData = true;
      }
    )
    .catch(
      (error:any) => {
        console.log(error);
      }
    )
  }

  checkCurrentRoute() {
    this.currentRoute = this.router.url.replace('/','');
  }

  setEnterpriseSelected(name : any, cif: any){
    this.selectedEnterprise = name;
    this.enterpriseService.setEnterprise(cif)
  }

}
