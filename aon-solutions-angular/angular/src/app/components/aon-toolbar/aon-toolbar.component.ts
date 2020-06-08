import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { Location } from '@angular/common';
import { UserDialogComponent } from '../dialogs/user-dialog/user-dialog.component';
import { HelpDialogComponent } from '../dialogs/help-dialog/help-dialog.component';
import { ExtensionDialogComponent } from '../dialogs/extension-dialog/extension-dialog.component';
import { MatDialog } from '@angular/material';
import { SharedService} from '../../services/services';
import { RootLoader, GwtLoader} from '../../utils/loader';
import { environment } from '../../../environments/environment';

@Component({
  selector: 'aon-toolbar',
  templateUrl: './aon-toolbar.component.html',
  styleUrls: ['./aon-toolbar.component.css'],
})
export class AonToolbarComponent implements OnInit {
  logo = environment.logo;
  logoCompany = environment.logoCompany;
  logoMobile = environment.logoMobile;
  mobileSearching: boolean = false;
  searching: boolean = true;
  showMenuIcon: string = "keyboard_arrow_right";

  constructor(private router: Router, private location: Location, public dialog: MatDialog, private service: SharedService) {

  }

  ngOnInit() {

  }

  getDescription() : string {
    return this.isParent() ? 'Entorno General': '';
  }

  getCompanyName() : string {
    return this.isParent() ? '' : this.service.getCompanyName();
  }

  payroll(): void {
    GwtLoader.startModule('aon_gwt_aio', 'employees');
  }

  callcenter() :void {
    GwtLoader.startModule('aon_gwt_aio', 'issues');
  }

  documental(): void {
    GwtLoader.startModule('aon_gwt_aio', 'documents');
  }

  company(): void {
    RootLoader.angularPanel(this.router, this.location, 'companyList');
    this.service.company = undefined;
  }

  openDialog(): void {
    const dialogRef = this.dialog.open(UserDialogComponent, {
      width: '250px',
      backdropClass: 'aon-user-dialog-backdrop',
      panelClass: 'aon-user-dialog-panel',
      position: {
        top: '50px',
        right: '20px'
      },
      data: {}
    });

    dialogRef.afterClosed().subscribe();
  }

  invoice(): void {
    RootLoader.angularPanel(this.router, this.location, 'invoice');
  }

  home(): void {
    if(this.isParent()){
      RootLoader.angularPanel(this.router, this.location, 'companyList');
    } else {
      RootLoader.rootPanel('<aon-desktop></aon-desktop>', '#f1f1f1');
    }
  }

  help(): void {
    const dialogRef = this.dialog.open(HelpDialogComponent, {
      width: '250px',
      backdropClass: 'aon-user-dialog-backdrop',
      panelClass: 'aon-user-dialog-panel',
      position: {
        top: '50px',
        right: '60px'
      },
      data: {}
    });

    dialogRef.afterClosed().subscribe();
  }

  apps(): void {
    const dialogRef = this.dialog.open(ExtensionDialogComponent, {
      width: '300px',
      backdropClass: 'aon-user-dialog-backdrop',
      panelClass: 'aon-user-dialog-panel',
      position: {
        top: '50px',
        right: '50px'
      },
      data: {}
    });
    dialogRef.afterClosed().subscribe();
  }

  getUserEmail(): string {
    return this.service.getUserEmail();
  }

  isMobile(): boolean {
    return this.service.isMobile;
  }

  closeSearch() : void {
    this.mobileSearching = false;
  }

  search(): void {
    this.searching = !this.searching;
  }

  isParent(): boolean {
    return this.service.company == undefined;
  }

  showMenu(): void {
    if('keyboard_arrow_right' === this.showMenuIcon){
      document.getElementById('showMenuButton').style.paddingRight = '0px';
      this.showMenuIcon = 'keyboard_arrow_left';
      this.service.showMenu = false;
    } else {
      document.getElementById('showMenuButton').style.paddingRight = '20px';
      this.showMenuIcon = 'keyboard_arrow_right';
      this.service.showMenu = true;
    }
  }

  getShowMenuStyle(): string {
    return 'keyboard_arrow_right' === this.showMenuIcon ? 'padding-right:0px' : 'padding-right:20px';
  }

  getCompanyParentLogo() : string{
    return this.service.company ? this.service.company.parentLogo : '';
  }

  getCompanyLogo() : string{
    return this.service.company ? this.service.company.logo : '';
  }
}
