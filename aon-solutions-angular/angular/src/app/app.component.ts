import { Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material';
import { Router } from '@angular/router';
import { Location } from '@angular/common';
import { SharedService, AonService } from './services/services';
import { Company } from './models/AonModel';
import { TediUtils } from './utils/tedi-utils';
import { RootLoader } from './utils/loader';
import './js/components/aon-desktop.js';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {

  constructor(private router: Router, private location: Location,
    public service: SharedService, public aonService: AonService,
    public dialog: MatDialog) {}

  ngOnInit() {
    this.service.isUserLoggedIn = false;
    if (this.service.getToken()) {
      this.service.isUserLoggedIn = true;
      this.aonService.getCompanies().subscribe( (companies: Company[]) => {
        if(companies.length === 1) {
          RootLoader.rootPanel('<aon-desktop></aon-desktop>');
        } else {
          RootLoader.angularPanel(this.router, this.location,  'companyList');
        }
      }, (error: any) => {
        this.service.isUserLoggedIn = false;
        TediUtils.showError(this.dialog, error.error);
        localStorage.clear();
      });
    }
  }

  isUserLoggedIn(): boolean {
    return this.service.isUserLoggedIn;
  }

  isMenuSidenavExpanded(): boolean {
    return !this.isParent() && this.service.showMenu;
  }

  isParent(): boolean {
    return this.service.company == undefined;
  }
}
