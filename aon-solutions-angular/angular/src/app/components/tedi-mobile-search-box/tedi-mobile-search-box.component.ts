import { Component } from '@angular/core';
import { CompanyService } from '../../services/services';
import { MatDialog } from '@angular/material';
import { AdvancedCompanySearchDialogComponent } from '../dialogs/advanced-company-search-dialog/advanced-company-search-dialog.component';

@Component({
  selector: 'app-mobile-search-box',
  templateUrl: './tedi-mobile-search-box.component.html',
  styleUrls: ['./tedi-mobile-search-box.component.css']
})
export class TediMobileSearchBoxComponent {

  hasValue = false;
  constructor(private companyService: CompanyService, public dialog: MatDialog) {}

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
        top: '48px',
        right: '100px'
      }
    });
    dialogRef.afterClosed().subscribe(result => {
      this.hasValue = result;
    });
  }

}
