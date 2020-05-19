import { Component } from '@angular/core';
import { CompanyService } from '../../services/services';
import { MatDialog } from '@angular/material';

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

  advanced(): void {}

}
