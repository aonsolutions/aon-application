import { Component, OnChanges, OnInit, ViewChild, ViewEncapsulation } from '@angular/core';
import { Router, NavigationEnd } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';
import { AuthService } from 'src/app/core/services/auth.service';
import { MenuItem } from 'src/app/core/models/interface/menu-item';
import { DropdownMenuComponent } from '../../components/dropdown-menu/dropdown-menu.component';
import { AppComponent } from '../../../app.component';

export class Enterprise {
  name: string;
  constructor(name: string){
    this.name = name;
  }
}

@Component({
  selector    : 'app-topbar',
  templateUrl : './top-bar.component.html',
  styleUrls   : ['./top-bar.component.scss'],
  host        : {
    '[style.width]' : "'100%'",
    '[style.height]': "'100%'",
  },
})

export class TopBarComponent implements OnInit, OnChanges {
  @ViewChild('first') dropdownMenuComponent: DropdownMenuComponent = new DropdownMenuComponent;
  menuItem        : MenuItem [] = []
  displayHomeIcon : boolean     = false;
  usserLoggged    : boolean     = this.auth.isLoggedIn();

  constructor(
    private router          : Router,
    private auth            : AuthService,
    private translateService: TranslateService,
    private appComponent    : AppComponent
  ){
    this.router.events.subscribe((event) => {
      const contentContainer = document.querySelector('.mat-sidenav-content') || window;
      contentContainer.scrollTo(0, 0);
      event instanceof NavigationEnd ? this.checkCurrentRoute() : null
    })

    this.translateService.get(
      ['HEADER.EDIT_PROFILE','HEADER.HELP','HEADER.LOGOUT', 'HEADER.LANGUAGE', 'LANGUAGE.ENGLISH', 'LANGUAGE.SPANISH']
    ).subscribe( result => {
      this.menuItem = [
        {root:true, text: result['HEADER.EDIT_PROFILE'] , icon:'person_pin' , colorIcon:'black', routerlink: 'edit-profile'},
        // {root:true, text: result['HEADER.HELP']         , icon:'help'       , colorIcon:'black'},
        {root:true, text: result['HEADER.LANGUAGE'], icon:'language',  colorIcon:'black', children:[
          {root:false, text: result['LANGUAGE.ENGLISH'] , click:() => this.selectLanguage('en')},
          {root:false, text: result['LANGUAGE.SPANISH'] , click:() => this.selectLanguage('es')},
        ]},
        {root:true, text: result['HEADER.LOGOUT']       , icon:'exit_to_app', colorIcon:'black', click:() => this.logout()},
      ]
    });
  }

  ngOnInit(): void {
  }

  checkCurrentRoute() {
    this.displayHomeIcon = this.router.url != '/home'? true : false;
  }

  ngOnChanges(): void {
  }

  logout() {
    this.auth.logout();
  }

  selectLanguage(language: string) {
    this.appComponent.selectLanguage(language)
  }

}
