import { Component } from '@angular/core';
import { InvoiceService } from '../../invoice/invoice.service';
import { CompanyService, UserService, SharedService } from '../../services/services';
import { MatDialog } from '@angular/material';
import { AdvancedSearchDialogComponent } from '../dialogs/advanced-search-dialog/advanced-search-dialog.component';
import { AdvancedUserSearchDialogComponent } from '../dialogs/advanced-user-search-dialog/advanced-user-search-dialog.component';
import { AdvancedCompanySearchDialogComponent } from '../dialogs/advanced-company-search-dialog/advanced-company-search-dialog.component';

@Component({
  selector: 'app-search-box',
  templateUrl: './tedi-search-box.component.html',
  styleUrls: ['./tedi-search-box.component.css']
})
export class TediSearchBoxComponent {

  hasValue = false;
  constructor(private invoiceService: InvoiceService,
    private service: SharedService,
    private companyService: CompanyService,
    private userService: UserService,
    public dialog: MatDialog) {}

  search(value: string): void {
    if (value.length > 0) {
      this.hasValue = true;
    } else {
      this.hasValue = false;
    }

    if (this.isCompany()) {
      this.companyService.filter.value = value;
      this.companyService.setFilter(this.companyService.filter);
    } else if (this.isUser()) {
      this.userService.filter.value = value;
      this.userService.setFilter(this.userService.filter);
    } else {
      this.invoiceService.filter.value = value;
      this.invoiceService.setFilter(this.invoiceService.filter);
    }
  }

  clear(): void {
    this.hasValue = false;
    if (this.isCompany()) {
      this.companyService.setFilter({});
    } else if (this.isUser()) {
      this.userService.setFilter({gestor: true});
    } else {
      this.invoiceService.setFilter({
        status: this.invoiceService.filter.status || '',
        type: this.invoiceService.filter.status === 'accepted' && this.invoiceService.filter.type
            ? this.invoiceService.filter.type  : ''
      });
    }
  }

  isCompany(): boolean {
    return this.invoiceService.isCompany || this.service.selection === 'companies';
  }

  isUser(): boolean {
    return this.service.selection === 'users';
  }

  advanced(): void {
    if (this.isCompany()) {
      const dialogRef = this.dialog.open(AdvancedCompanySearchDialogComponent, {
        width: '640px',
        backdropClass: 'tedi-user-dialog-backdrop',
        panelClass: 'tedi-user-dialog-panel',
        position: {
          top: '50px',
          left: '260px'
        }
      });
      dialogRef.afterClosed().subscribe(result => {
        this.hasValue = result;
      });
    }
    else if (this.isUser()) {
      const dialogRef = this.dialog.open(AdvancedUserSearchDialogComponent, {
        width: '640px',
        backdropClass: 'tedi-user-dialog-backdrop',
        panelClass: 'tedi-user-dialog-panel',
        position: {
          top: '50px',
          left: '260px'
        }
      });
      dialogRef.afterClosed().subscribe(result => {
        this.hasValue = result;
      });
    } else if (!this.isCompany()) {
      const dialogRef = this.dialog.open(AdvancedSearchDialogComponent, {
        width: '640px',
        backdropClass: 'tedi-user-dialog-backdrop',
        panelClass: 'tedi-user-dialog-panel',
        position: {
          top: '50px',
          left: '260px'
        },
        data: {
          isCompany: this.isCompany()
        }
      });
      dialogRef.afterClosed().subscribe(result => {
        this.hasValue = result;
      });
    }
  }

}
