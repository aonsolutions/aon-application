import { Component, OnInit, ViewChild } from '@angular/core';
import { NavigationEnd, Router } from '@angular/router';
import { DropdownMenuComponent } from '../dropdown-menu/dropdown-menu.component';
import { MenuItem } from 'src/app/core/models/interface/menu-item';
import { EnterpriseService } from 'src/app/core/services/enterprise.service';
import { AuthService } from 'src/app/core/services/auth.service';
import { IEnterprise } from 'libraries/AonSDK/aon';

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

  constructor(private router: Router, public enterpriseService: EnterpriseService, public authService: AuthService) {
    this.router.events.subscribe((event) => {       
      event instanceof NavigationEnd ? this.checkCurrentRoute() : null     
    })
  }
  
  ngOnInit(): void {
    this.enterpriseService.getEnterpriseList()
    .then(
      (response) => {
        if(response.size() > 0){
          let actualDocument = this.authService.getEnterpriseSelected();
          response.forEach((element) => {
            this.menuItem.push({root:true, text:element.Name, click: () => this.setEnterpriseSelected(element)})
            if(element.Document == actualDocument) this.selectedEnterprise = element.Name;
          })
          this.haveData = true;
        }
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

  setEnterpriseSelected(element: IEnterprise){
    this.selectedEnterprise = element.Name;
    this.authService.setEnterprise(element);
  }

}
