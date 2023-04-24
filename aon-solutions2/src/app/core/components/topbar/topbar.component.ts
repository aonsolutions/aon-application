import { Component, OnChanges, OnInit, ViewEncapsulation } from '@angular/core';
import { Router, NavigationEnd } from '@angular/router';
import { AuthService } from '../../services/auth.service';

export class Enterprise {
  name: string;
  constructor(name: string){
    this.name = name;
  }
}

@Component({
  selector: 'app-topbar',
  templateUrl: './topbar.component.html',
  styleUrls: ['./topbar.component.scss'],
  host: {
    '[style.width]': "'100%'",
    '[style.height]': "'100%'",
  },
})
export class TopbarComponent implements OnInit, OnChanges {

  displayHomeIcon: boolean = false;
  currentRoute: string = this.router.url.replace('/','');
  selectedEnterprise: string = 'Nombre de empresa 1';
  enterprises: Enterprise [] = [
    {name: 'Nombre de empresa 1'},
    {name: 'Nombre de empresa 2'},
    {name: 'Nombre de empresa 3'},
    {name: 'Nombre de empresa 4'},
  ];

  constructor(private router: Router, public auth: AuthService) {
    this.router.events.subscribe((event) => {
      event instanceof NavigationEnd ? this.checkCurrentRoute() : null
    })
  }

  ngOnInit(): void {
  }

  checkCurrentRoute() {
    if(this.router.url != '/home'){
      this.displayHomeIcon = true;
    }else{
      this.displayHomeIcon = false;
    }
    this.currentRoute = this.router.url.replace('/','');
  }

  ngOnChanges(): void {
  }

}
