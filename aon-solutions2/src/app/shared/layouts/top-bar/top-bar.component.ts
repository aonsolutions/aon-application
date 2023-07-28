import { Component, OnChanges, OnInit, ViewChild, ViewEncapsulation } from '@angular/core';
import { Router, NavigationEnd } from '@angular/router';
import { MenuItem } from 'src/app/core/models/interface/menu-item';
import { AuthService } from 'src/app/core/services/auth.service';
import { DropdownMenuComponent } from '../../components/dropdown-menu/dropdown-menu.component';
import { TranslateService } from '@ngx-translate/core';

export class Enterprise {
  name: string;
  constructor(name: string){
    this.name = name;
  }
}

@Component({
  selector: 'app-topbar',
  templateUrl: './top-bar.component.html',
  styleUrls: ['./top-bar.component.scss'],
  host: {
    '[style.width]' : "'100%'",
    '[style.height]': "'100%'",
  },
})
export class TopBarComponent implements OnInit, OnChanges {

  @ViewChild('first') dropdownMenuComponent: DropdownMenuComponent = new DropdownMenuComponent;
  menuItem: MenuItem [] = [
    {root:true, text:'Editar perfil'  , icon:'person_pin' , colorIcon:'black'},
    {root:true, text:'Ayuda'          , icon:'help'       , colorIcon:'black'},
    {root:true, text:'Cerrar sesión' , icon:'exit_to_app', colorIcon:'black', click:() => this.logout()},
  ]

  displayHomeIcon : boolean = false;
  usserLoggged    : boolean = this.auth.isLoggedIn();
  currentRoute    : string  = this.router.url.replace('/','');

  constructor(
    private router: Router, public auth: AuthService,
    private translateService: TranslateService
    ){
    this.router.events.subscribe((event) => {
      event instanceof NavigationEnd ? this.checkCurrentRoute() : null
    })
  }

  ngOnInit(): void {
    this.translateMenuItems();
  }

  checkCurrentRoute() {
    this.displayHomeIcon = this.router.url != '/home'? true : false;
    this.currentRoute    = this.router.url.replace('/','');
  }

  ngOnChanges(): void {
  }

  logout() {
    this.auth.logout();
  }

  // Función para traducir desde el archivo .json
  translateMenuItems(): void {
    this.translateService.get('HEADER').subscribe((translation) => {
      this.menuItem.forEach((item) => {
        switch (item.text) {
          case 'Editar perfil':
            item.text = translation['EDIT_PROFILE'];
            break;
          case 'Ayuda':
            item.text = translation['HELP'];
            break;
          case 'Cerrar sesión':
            item.text = translation['LOGOUT'];
            break;
          default:
            break;
        }
      });
    });
  }
}
