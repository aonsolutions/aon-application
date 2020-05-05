import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material';
import { Component, Inject } from '@angular/core';
import { CompanyService } from '../../../services/services';

@Component({
  templateUrl: './advanced-company-search-dialog.component.html',
  styleUrls: ['./advanced-company-search-dialog.component.css']
})
export class AdvancedCompanySearchDialogComponent {
  f: any;
  constructor(public dialogRef: MatDialogRef<AdvancedCompanySearchDialogComponent>,
              @Inject(MAT_DIALOG_DATA) public data: any, private companyService: CompanyService ) {
    this.f = {
      parent: this.companyService.filter && this.companyService.filter.parent || false
    };
  }

  updateParent(value: boolean): void {
    this.f.parent = value;
  }

  search(): void {
    if(!this.companyService.filter){
      this.companyService.filter = {};
    }

    this.companyService.filter.parent = this.f.parent;
    this.companyService.setFilter(this.companyService.filter);
    this.dialogRef.close(true);
  }
}
