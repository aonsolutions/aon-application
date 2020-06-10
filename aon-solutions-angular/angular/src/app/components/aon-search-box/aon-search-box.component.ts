import { Component } from '@angular/core';
import { CompanyService, SharedService } from '../../services/services';
import { MatDialog } from '@angular/material';
import { AdvancedCompanySearchDialogComponent } from '../dialogs/advanced-company-search-dialog/advanced-company-search-dialog.component';

@Component({
  selector: 'aon-search-box',
  templateUrl: './aon-search-box.component.html',
  styleUrls: ['./aon-search-box.component.css']
})
export class AonSearchBoxComponent {

  hasValue = false;
  constructor(private service: SharedService, private companyService: CompanyService, public dialog: MatDialog) {}

  search(value: string): void {
    if (value.length > 0) {
      this.hasValue = true;
    } else {
      this.hasValue = false;
    }

    this.companyService.filter.value = value;
    this.companyService.setFilter(this.companyService.filter);
  }

  clear(): void {
    this.hasValue = false;
    this.companyService.setFilter({});
  }

  advanced(): void {
    const dialogRef = this.dialog.open(AdvancedCompanySearchDialogComponent, {
      width: '260px',
      backdropClass: 'tedi-user-dialog-backdrop',
      panelClass: 'aon-user-dialog-panel',
      position: {
        top: this.service.isMobile ? '64px': '48px',
        right: this.service.isMobile ? '20px' : '100px'
      }
    });
    dialogRef.afterClosed().subscribe(result => {
      this.hasValue = result;
    });
  }

}
