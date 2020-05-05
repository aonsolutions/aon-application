import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material';
import { MAT_MOMENT_DATE_FORMATS, MomentDateAdapter } from '@angular/material-moment-adapter';
import { ErrorStateMatcher, DateAdapter, MAT_DATE_FORMATS, MAT_DATE_LOCALE } from '@angular/material/core';
import { FormControl, FormGroupDirective, NgForm } from '@angular/forms';
import { Component, Inject } from '@angular/core';
import { InvoiceService } from '../../../invoice/invoice.service';
import { SharedService } from '../../../services/shared.service';
import { Company } from '../../../models/models';
import {Observable} from 'rxjs';

@Component({
  templateUrl: './advanced-search-dialog.component.html',
  styleUrls: ['./advanced-search-dialog.component.css'],
  providers: [
    {provide: MAT_DATE_LOCALE, useValue: 'es-ES'},
    {provide: DateAdapter, useClass: MomentDateAdapter, deps: [MAT_DATE_LOCALE]},
    {provide: MAT_DATE_FORMATS, useValue: MAT_MOMENT_DATE_FORMATS},
  ]
})
export class AdvancedSearchDialogComponent {

  matcher = new MyErrorStateMatcher();
  filteredCompanies: Observable<Company[]>;
  fromControl: FormControl = new FormControl();
  toControl: FormControl = new FormControl();
  f: any;
  isCompany = false;
  constructor(public dialogRef: MatDialogRef<AdvancedSearchDialogComponent>,
              @Inject(MAT_DIALOG_DATA) public data: any, private service: SharedService,
              private invoiceService: InvoiceService) {
                this.isCompany = data.isCompany;
                this.f = {
                  status: this.invoiceService.filter.status || '',
                  type: this.invoiceService.filter.type || '',
                  category: this.invoiceService.filter.category || '',
                  registry: this.invoiceService.filter.registry || '',
                  reference: this.invoiceService.filter.reference || '',
                  rdocument: this.invoiceService.filter.rdocument || '',
                  rname: this.invoiceService.filter.rname || '',
                  fromDate: this.invoiceService.filter.fromDate || '',
                  toDate: this.invoiceService.filter.toDate || ''
                };
                if (this.f.fromDate) {
                  this.fromControl = this.getFormControl(this.f.fromDate);
                }
                if (this.f.toDate) {
                  this.toControl = this.getFormControl(this.f.toDate);
                }
  }


  validKey(key: string): boolean {
    return key !== 'Enter' && !key.includes('Arrow');
  }

  updateRdocument(rdocument: string): void {
    this.f.rdocument = rdocument;
  }

  updateRname(rname: string): void {
    this.f.rname = rname;
  }

  updateReference(reference: string): void {
    this.f.reference = reference;
  }

  updateFromDate(fromDate: string): void {
    const date = new Date(this.getYear(fromDate), this.getMonth(fromDate) - 1, this.getDay(fromDate), 2);
    this.f.fromDate =  date.toISOString();
  }

  updateToDate(toDate: string): void {
    const date = new Date(this.getYear(toDate), this.getMonth(toDate) - 1, this.getDay(toDate), 2);
    this.f.toDate =  date.toISOString();
  }

  search(): void {
    if (this.isCompany) {

    } else {
      this.invoiceService.setFilter(this.f);
      this.dialogRef.close(true);
    }
  }

  getFormControl(str: string): FormControl {
    if (str) {
      return new FormControl(new Date(str));
    } else {
      return new FormControl();
    }
  }

  getYear(str: string): number {
    const arr = str.split('/');
    return + arr[2];
  }
  getMonth(str: string): number {
    const arr = str.split('/');
    return + arr[1];
  }
  getDay(str: string): number {
    const arr = str.split('/');
    return + arr[0];
  }
}

/** Error when invalid control is dirty, touched, or submitted. */
export class MyErrorStateMatcher implements ErrorStateMatcher {
  isErrorState(control: FormControl | null, form: FormGroupDirective | NgForm | null): boolean {
    const isSubmitted = form && form.submitted;
    return !!(control && control.invalid && (control.dirty || control.touched || isSubmitted));
  }
}
