import { Component } from '@angular/core';
import { CompanyService } from '../../services/services';
import { MatDialog } from '@angular/material';

@Component({
  selector: 'app-search-box',
  templateUrl: './tedi-search-box.component.html',
  styleUrls: ['./tedi-search-box.component.css']
})
export class TediSearchBoxComponent {

  hasValue = false;
  constructor(private companyService: CompanyService,
    public dialog: MatDialog) {}

  search(value: string): void {
    if (value.length > 0) {
      this.hasValue = true;
    } else {
      this.hasValue = false;
    }
    if(this.companyService.filter)
      this.companyService.filter.value = value;
    else this.companyService.filter = { value };
    this.companyService.setFilter(this.companyService.filter);
  }

  clear(): void {
    this.hasValue = false;
    this.companyService.setFilter({});
  }


  advanced(): void {

  }

}
