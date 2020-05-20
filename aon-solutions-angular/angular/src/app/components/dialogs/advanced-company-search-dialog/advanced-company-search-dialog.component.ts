import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material';
import { Component, Inject } from '@angular/core';
import { CompanyService } from '../../../services/services';

@Component({
  templateUrl: './advanced-company-search-dialog.component.html',
  styleUrls: ['./advanced-company-search-dialog.component.css']
})
export class AdvancedCompanySearchDialogComponent {
  f: any = {
    active: true,
    shared: true
  };
  constructor(public dialogRef: MatDialogRef<AdvancedCompanySearchDialogComponent>,
              @Inject(MAT_DIALOG_DATA) public data: any, private companyService: CompanyService ) {
    if(this.companyService.filter){
      this.f = {
        active: this.companyService.filter.active != undefined ? this.companyService.filter.active : true,
        shared: this.companyService.filter.shared != undefined ? this.companyService.filter.shared : true
      };
    }
  }

  updateActive(value: boolean): void {
    this.f.active = value;
  }

  updateShared(value: boolean): void {
    this.f.shared = value;
  }

  search(): void {
    if(!this.companyService.filter){
      this.companyService.filter = {};
    }

    this.companyService.filter.active = this.f.active;
    this.companyService.filter.shared = this.f.shared;

    this.companyService.setFilter(this.companyService.filter);
    this.dialogRef.close(true);
  }
}
