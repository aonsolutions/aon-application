import { Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material';
import { Router } from '@angular/router';
import { CompanyService, SharedService, UserService } from '../../services/services';

@Component({
  selector: 'app-account',
  templateUrl: './my-account.component.html',
  styleUrls: ['./my-account.component.css'],
})
export class MyAccountComponent implements OnInit {
  isExpand: boolean;

  constructor(public dialog: MatDialog, private router: Router,
      public service: SharedService,
      public companyService: CompanyService, public userService: UserService) {
    this.isExpand = !this.isMobile();
  }

  ngOnInit() {}

  general(): void {
    this.service.selection = 'general';
    this.userService.user = this.service.user;
    this.router.navigate(['myAccount/general']);
    if (this.isMobile()) {
      this.openSidenav();
    }
  }

  users(): void {
    this.service.selection = 'users';
    this.router.navigate(['/myAccount/users']);
    if (this.isMobile()) {
      this.openSidenav();
    }
  }

  companies(): void {
    this.service.selection = 'companies';
    this.router.navigate(['/myAccount/companies']);
    if (this.isMobile()) {
      this.openSidenav();
    }
  }

  add(): void {
    this.router.routeReuseStrategy.shouldReuseRoute = function() {
      return false;
    };

    if (this.service.selection === 'users') {
      this.router.navigate(['/myAccount/createUser']);
    } else if (this.service.selection === 'companies') {
      this.companyService.isNew = true;
      this.router.navigate(['/myAccount/createCompany']);
    }
  }

  showAdd() : boolean {
    return this.service.selection && this.service.selection !== 'general';
  }

  openSidenav(): void {
    this.isExpand = !this.isExpand;
  }

  closeSidenav(): void {}

  isMobile(): boolean {
    return this.service.isMobile;
  }
}
