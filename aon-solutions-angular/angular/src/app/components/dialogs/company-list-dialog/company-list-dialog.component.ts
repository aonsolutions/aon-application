import { MAT_DIALOG_DATA, MatDialogRef, MatDialog } from '@angular/material';
import { Component, OnInit, Inject } from '@angular/core';
import { SharedService } from '../../../services/shared.service';
import { Company, User } from '../../../models/models';
import { TediUtils } from '../../../utils/tedi-utils';

@Component({
  templateUrl: './company-list-dialog.component.html',
  styleUrls: ['./company-list-dialog.component.css'],
})
export class CompanyListDialogComponent implements OnInit {
  title = 'Empresas';
  companies: Company[] = [];
  user: User;

  constructor(public dialogRef: MatDialogRef<CompanyListDialogComponent>,
              @Inject(MAT_DIALOG_DATA) public data: User,
              private service: SharedService, public dialog: MatDialog) {
    this.user = data;
  }

  ngOnInit() {
    this.service.getCompanies().subscribe(
      (result: Company[]) => this.companies =  result,
      (error: any) => TediUtils.showError(this.dialog, error.error)
    );
  }

  onNoClick(): void {

  }

  search(value: string): void {
    this.service.getCompanies({name: value}).subscribe(
      (result: Company[]) => this.companies =  result,
      (error: any) => TediUtils.showError(this.dialog, error.error)
    );
  }

  onSelect(company: any, event: any): void {
    if (event) {
       company.users.push(this.user.email);
    } else {
      const index = company.users.indexOf(this.user.email, 0);
      if (index > -1) {
        company.users.splice(index, 1);
      }
    }
    const cp = {
      document: company.document,
      users: company.users
    };
    this.service.updateCompany(cp).subscribe();
  }

  accept(): void {
    this.dialogRef.close();
  }

  hasCompanies(): boolean {
    return this.companies.length === 0;
  }

  hasCompany(company: Company): boolean {
    return company.users.includes(this.user.email);
  }

  public onScroll() {

  }
}
