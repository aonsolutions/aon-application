import { Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material';
import { Router } from '@angular/router';
import { Location } from '@angular/common';
import { SharedService, AonService } from './services/services';
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
      RootLoader.angularPanel(this.router, this.location,  'companyList');
    }
  }

  isUserLoggedIn(): boolean {
    return this.service.isUserLoggedIn;
  }

  isMenuSidenavExpanded(): boolean {
    return (this.isMobile() || !this.isParent()) && this.service.showMenu;
  }

  isParent(): boolean {
    return this.service.company == undefined;
  }

  isMobile(): boolean {
    return this.service.isMobile;
  }
}
