import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { Location } from '@angular/common';
import { UserDialogComponent } from '../dialogs/user-dialog/user-dialog.component';
import { ExtensionDialogComponent } from '../dialogs/extension-dialog/extension-dialog.component';
import { MatDialog } from '@angular/material';
import { SharedService } from '../../services/shared.service';
import { RootLoader, GwtLoader} from '../../utils/loader';
import { environment } from '../../../environments/environment';

@Component({
  selector: 'aon-toolbar',
  templateUrl: './aon-toolbar.component.html',
  styleUrls: ['./aon-toolbar.component.css'],
})
export class AonToolbarComponent implements OnInit {
  logo = environment.logo;
  logoMobile = environment.logoMobile;
  mobileSearching: boolean = false;

  constructor(private router: Router, private location: Location, public dialog: MatDialog, private service: SharedService) {

  }

  ngOnInit() {

  }

  getCompanyName() : string {
    return this.service.getCompanyName();
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
  }

  openDialog(): void {
    const dialogRef = this.dialog.open(UserDialogComponent, {
      width: '250px',
      backdropClass: 'tedi-user-dialog-backdrop',
      panelClass: 'tedi-user-dialog-panel',
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

  }

  apps(): void {
    const dialogRef = this.dialog.open(ExtensionDialogComponent, {
      width: '300px',
      backdropClass: 'tedi-user-dialog-backdrop',
      panelClass: 'tedi-user-dialog-panel',
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

  isCompanySelected(): boolean {
      return this.service.company == undefined;
  }

  closeSearch() : void {
    this.mobileSearching = false;
  }

  search(): void {
    this.mobileSearching = true;
  }
}
