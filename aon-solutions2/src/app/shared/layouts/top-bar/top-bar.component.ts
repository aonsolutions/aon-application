import { Component, OnChanges, OnInit, ViewEncapsulation } from '@angular/core';
import { Router, NavigationEnd } from '@angular/router';
@Component({
  selector: 'app-topbar',
  templateUrl: './top-bar.component.html',
  styleUrls: ['./top-bar.component.scss'],
  host: {
    '[style.width]': "'100%'",
    '[style.height]': "'100%'",
  },
})
export class TopBarComponent implements OnInit, OnChanges {

  displayHomeIcon: boolean = false;
  currentRoute: string = this.router.url.replace('/','');

  constructor(private router: Router) { 
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
