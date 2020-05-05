import { Component, OnInit, OnDestroy, ViewChild, HostListener } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { InvoiceService } from '../invoice.service';
import { SharedService } from '../../services/shared.service';
import { FormControl, FormGroupDirective, NgForm, Validators } from '@angular/forms';
import { MAT_MOMENT_DATE_FORMATS, MomentDateAdapter } from '@angular/material-moment-adapter';
import { ErrorStateMatcher, DateAdapter, MAT_DATE_FORMATS, MAT_DATE_LOCALE } from '@angular/material/core';
import { MatDialog } from '@angular/material';
import { AddressDialogComponent } from '../../components/dialogs/address-dialog/address-dialog.component';
import { TypeDialogComponent } from '../../components/dialogs/type-dialog/type-dialog.component';
import { InvoiceDetailDialogComponent } from '../invoice-detail-dialog/invoice-detail-dialog.component';
import { InvoiceFinanceDialogComponent } from '../invoice-finance-dialog/invoice-finance-dialog.component';
import { CommentDialogComponent } from '../../components/dialogs/comment-dialog/comment-dialog.component';
import { TediUtils } from '../../utils/tedi-utils';
import { TediValidators } from '../../utils/TediValidators';
import { AonMaker, Company, Invoice, InvoiceStatus, InvoiceTransaction, TaxType, PayMethod, Finance, Address, InvoiceType } from '../../models/models';
import {Observable} from 'rxjs';

@Component({
  selector: 'app-invoice-sheet',
  templateUrl: './invoice-sheet.component.html',
  styleUrls: ['./invoice-sheet.component.css'],
  providers: [
    {provide: MAT_DATE_LOCALE, useValue: 'es-ES'},
    {provide: DateAdapter, useClass: MomentDateAdapter, deps: [MAT_DATE_LOCALE]},
    {provide: MAT_DATE_FORMATS, useValue: MAT_MOMENT_DATE_FORMATS}
  ]
})
export class InvoiceSheetComponent implements OnInit, OnDestroy {
  object = '';
  matcher = new MyErrorStateMatcher();
  filteredCompanies: Observable<Company[]>;

  transactions: any[] = [
    {code: InvoiceTransaction.NATIONAL, name: 'Nacional'},
    {code: InvoiceTransaction.INTRACOMMUNITY, name: 'Intracomunitaria'},
    {code: InvoiceTransaction.EXTRACOMMUNITY, name: 'Extracomunitaria'},
    {code: InvoiceTransaction.CAN_CEU_MEL, name: 'Canarias, Ceuta y Melilla'},
    {code: InvoiceTransaction.OTHER_ISP, name: 'I.S.P.'},
  ];

  category: string;
  categories: any[];

  showSidenavButton = false;

  showAddDetail = false;
  showAddFinance = false;

  dateControl: FormControl = new FormControl();
  paymethod = PayMethod.CASH_BASIS;
  totalFormControl = new FormControl('', [
    Validators.pattern(/^(\d*\.)?\d+$/)
  ]);

  ibanFormControl = new FormControl('', [
    TediValidators.ibanValidator
  ]);

  liclass = 'tedi-li';
  newFinance = AonMaker.createFinance();
  newDetail = AonMaker.createInvoiceDetail();

  iva_percentages = [{percentage: 21.0, viewPercentage: '21.0%'},
              {percentage: 10.0, viewPercentage: '10.0%'},
              {percentage: 4.0, viewPercentage: '4.0%'},
              {percentage: 0.0, viewPercentage: '0.0%'}];

  irpf_percentages = [
    {percentage: 19.0, viewPercentage: '19.0%'},
    {percentage: 15.0, viewPercentage: '15.0%'},
    {percentage: 7.0, viewPercentage: '7.0%'}
  ];

  isNewTax = false;
  newTax = AonMaker.createInvoiceTax();
  acceptedMimeTypes = [
    'image/gif',
    'image/jpeg',
    'image/png',
    'application/pdf'
  ];

  @ViewChild('newFinanceDate', {static: false}) public newFinanceDate: any;
  @ViewChild('newFinanceAmount', {static: false}) public newFinanceAmount: any;
  @ViewChild('newFinanceIban', {static: false}) public newFinanceIban: any;
  @ViewChild('newTaxBase', {static: false}) public newTaxBase: any;
  @ViewChild('newTaxQuota', {static: false}) public newTaxQuota: any;
  @ViewChild('newDetailDescription', {static: false}) public newDetailDescription: any;
  @ViewChild('newDetailQuantity', {static: false}) public newDetailQuantity: any;
  @ViewChild('newDetailDesc', {static: false}) public newDetailDesc: any;
  @ViewChild('newDetailPrice', {static: false}) public newDetailPrice: any;
  constructor(public httpClient: HttpClient, public invoiceService: InvoiceService,
    public dialog: MatDialog, public service: SharedService) {
      this.invoiceService.fileExpanded.subscribe(() => this.autosize());
      this.invoiceService.changeInvoice.subscribe(() => {
        if (this.hasImgFile() || this.hasPdfFile()) {
          this.invoiceService.expandFile(true);
        }

        this.paymethod = this.invoiceService.invoice.finances && this.invoiceService.invoice.finances.length > 0 ?
          this.invoiceService.invoice.finances[0].pay_method : PayMethod.CASH_BASIS;
        this.dateControl = this.getFormControl(this.invoiceService.invoice.date);
      });
  }

  @HostListener('window:keyup', ['$event'])
  keyEvent(event: KeyboardEvent) {
    if(event.keyCode === 34){
      this.next();
    }
    if(event.keyCode === 33) {
      this.prev();
    }
  }

  isPrev(): boolean {
    return this.invoiceService.invoiceIndex > 0;
  }

  prev(): void {
    if(this.isPrev()){
      this.invoiceService.invoiceIndex--;
      this.changeInvoice();
    }
  }

  isNext(): boolean {
    return this.invoiceService.invoiceIndex < this.invoiceService.invoiceList.length - 1;
  }

  next(): void {
    if(this.isNext()){
      this.invoiceService.invoiceIndex++;
      this.changeInvoice();
    }
  }


  changeInvoice(): void {
    const invoice = this.invoiceService.invoiceList[this.invoiceService.invoiceIndex];
    this.invoiceService.setInvoice(invoice);
    this.invoiceService.invoiceFile = undefined;
    this.invoiceService.expandFile(false);
    if (invoice.file) {
      this.getChangeFile();
    } else {
      this.invoiceService.changeInvoice.next(true);
    }
  }

  getChangeFile(): void {
    this.invoiceService.getFile().subscribe(
      () => this.invoiceService.changeInvoice.next(true),
      () => this.invoiceService.changeInvoice.next(true),
    );
  }


  isMobile(): boolean {
    return this.service.isMobile;
  }
  autosize() {
    let value: number = innerWidth - (this.invoiceService.isMenuExpanded ? 250 : 65);
    value = value - (this.invoiceService.isFileExpanded ? (value / 2) : 0);
    this.liclass = (value < 800) ? 'tedi-li2' : 'tedi-li';
  }

  getFile(): void {
    if (this.hasImgFile() || this.hasPdfFile()) {
      this.invoiceService.invoiceFile = {};
      this.invoiceService.getFile().subscribe(() => {
        this.invoiceService.expandFile(true);
      });
    }
  }

  ngOnInit() {
    this.service.isSheet.next(true);
    if (this.invoiceService.isNew) {
      this.invoiceService.invoice.domain = this.service.getActualCompany();
      this.invoiceService.invoice.type = this.invoiceService.newType;
      this.invoiceService.getInvoiceType(this.invoiceService.invoice.type).subscribe(result => {
        this.categories = result;
      });

      this.dateControl = this.getFormControl(this.invoiceService.invoice.date);
    } else {
      this.getFile();
      this.paymethod = this.invoiceService.invoice.finances.length > 0 ?
        this.invoiceService.invoice.finances[0].pay_method : PayMethod.CASH_BASIS;

      this.invoiceService.getInvoiceType(this.invoiceService.invoice.type).subscribe(result => {
        this.categories = result;
      });
      this.dateControl = this.getFormControl(this.invoiceService.invoice.date);
    }
    this.autosize();
  }

  ngOnDestroy() {
    this.service.isSheet.next(false);
    if (this.invoiceService.isDeleting) {
      this.invoiceService.isDeleting = false;
    }
    this.invoiceService.invoice = AonMaker.createInvoice();
    this.invoiceService.invoiceFile = {};
    this.invoiceService.invoiceList = undefined;
    this.invoiceService.expandFile(false);
  }

  getType() {
    return this.invoiceService.isNew
      ? this.invoiceService.newType
      : this.invoiceService.invoice.type;
  }

  getStatusIcon() : string {
    if(this.invoiceService.invoice.status === InvoiceStatus.REFUSED) {
      return 'report';
    } else if(this.invoiceService.invoice.status === InvoiceStatus.TRASH) {
      return 'delete';
    }
  }

  getFormControl(value: Date): FormControl {
    console.log('get FC  ' + value);
    if (value) {
      return new FormControl(value);
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

  changeType(): void {
    if (this.invoiceService.isNew || this.isPending()) {
      const dialogRef = this.dialog.open(TypeDialogComponent, {
        height: '200px',
        panelClass: 'tedi-user-dialog-panel',
        data: this.invoiceService.invoice.type
      });

      dialogRef.afterClosed().subscribe(result => {
        this.invoiceService.invoice.type = result;
        if (this.invoiceService.isNew) {
          this.invoiceService.newType = result;
        }
        this.invoiceService.getInvoiceType(this.invoiceService.invoice.type)
        .subscribe((categories: any[]) => {
          this.categories = categories;
        });
        this.save();
      });
    }
  }

  editAddress(): void {
    const dialogRef = this.dialog.open(AddressDialogComponent, {
      height: '455px',
      panelClass: 'tedi-user-dialog-panel',
      data: this.isEmitida() ? this.invoiceService.invoice.receiver.address : this.invoiceService.invoice.sender.address
    });

    dialogRef.afterClosed().subscribe(result => {
      if (this.isEmitida()) {
        this.invoiceService.invoice.receiver.address = result;
        this.save();
      } else {
        this.invoiceService.invoice.sender.address = result;
        this.save();
      }
    });
  }

  addDetail(): void {
    this.showAddDetail = true;
  }

  addDetailMobile() {
    const dialogRef = this.dialog.open(InvoiceDetailDialogComponent, {
      height: '325px',
      width: '350px',
      panelClass: 'tedi-user-dialog-panel'
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.newDetail = result;
        this.createDetail();
      }
    });
  }

  editDetailMobile(number: number) {
    const dialogRef = this.dialog.open(InvoiceDetailDialogComponent, {
      height: '325px',
      width: '350px',
      panelClass: 'tedi-user-dialog-panel',
      data: this.invoiceService.invoice.details[number]
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.invoiceService.invoice.details[number] = result;
        this.updateDetailVat();
        this.save();
      }
    });
  }

  addFinance(): void {
    if(this.invoiceService.invoice.finances.length === 0) {
      this.newFinance.amount = this.invoiceService.invoice.total;
    }
    this.newFinance.due_date = this.invoiceService.invoice.date;
    this.showAddFinance = true;
  }

  addFinanceMobile() {
    const dialogRef = this.dialog.open(InvoiceFinanceDialogComponent, {
      height: '450px',
      width: '350px',
      panelClass: 'tedi-user-dialog-panel'
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.newFinance = result;
        this.createFinance();
      }
    });
  }

  editFinanceMobile(number: number) {
    const dialogRef = this.dialog.open(InvoiceFinanceDialogComponent, {
      height: '450px',
      width: '350px',
      panelClass: 'tedi-user-dialog-panel',
      data: this.invoiceService.invoice.finances[number]
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.invoiceService.invoice.finances[number] = result;
        this.save();
      }
    });
  }

  updateSeries(value: string): void {
    this.invoiceService.invoice.series = value;
    this.invoiceService.invoice.reference = this.invoiceService.invoice.series
      + '/' + this.invoiceService.invoice.number;
    this.save();
  }

  updateNumber(value: number): void {
    this.invoiceService.invoice.number = value;
    this.invoiceService.invoice.reference = this.invoiceService.invoice.series
      + '/' + this.invoiceService.invoice.number;
    this.save();
  }

  updateReference(value: string): void {
    this.invoiceService.invoice.reference = value;
    this.save();
  }

  updateDate(value: string): void {
    const date = new Date(this.getYear(value), this.getMonth(value) - 1, this.getDay(value), 2);
    this.invoiceService.invoice.date = date;
    this.save();
  }

  updateReceiverNif(value: string): void {
    this.invoiceService.invoice.receiver.document = value;
    this.save();
  }

  updateSenderNif(value: string): void {
    this.invoiceService.invoice.sender.document = value;
    this.save();
  }

  validKey(key: string): boolean {
    return key !== 'Enter' && !key.includes('Arrow');
  }

  scpsName(event: any, registry: string): void {
    if (!this.invoiceService.invoice.id && this.invoiceService.isNew){
      this.save();
      this.invoiceService.isNew = false;
    }
    if (this.validKey(event.key)) {
      if (registry.length > 3) {
        this.filteredCompanies = this.service.getRegistries({name: registry.toUpperCase()});
      } else { this.filteredCompanies = undefined; }
    }
  }

  scpsDocument(event: any, registry: string): void {
    if (!this.invoiceService.invoice.id && this.invoiceService.isNew){
      this.save();
      this.invoiceService.isNew = false;
    }

    if (this.validKey(event.key)) {
      if (registry.length > 3) {
        this.filteredCompanies = this.service.getRegistries({document: registry.toUpperCase()});
      } else { this.filteredCompanies = undefined; }
    }
  }

  registryDocumentSelection(cp: any): void {
    if (this.isEmitida() && !(cp.document ===  this.invoiceService.invoice.receiver.document
        || cp === this.invoiceService.invoice.receiver.document)) {
      this.invoiceService.invoice.receiver = cp;
      this.save();
    }
    if (!this.isEmitida() && !(cp.document ===   this.invoiceService.invoice.sender.document
        || cp === this.invoiceService.invoice.sender.document)) {
      this.invoiceService.invoice.sender = cp;
      this.save();
    }

  }

  registryNameSelection(cp: any): void {
    console.log('REGISTRY NAME : ' + cp.name);
    if (this.isEmitida() && !(cp.name ===  this.invoiceService.invoice.receiver.name
        || cp === this.invoiceService.invoice.receiver.name)) {
      this.invoiceService.invoice.receiver = cp;
      this.save();
    }
    if (!this.isEmitida() && !(cp.name ===   this.invoiceService.invoice.sender.name
        || cp === this.invoiceService.invoice.sender.name)) {
      this.invoiceService.invoice.sender = cp;
      this.save();
    }
  }

  iscpName(cp: any): boolean {
    return (this.isEmitida() && cp.name ===   this.invoiceService.invoice.receiver.name)
      ||  cp.name === this.invoiceService.invoice.sender.name ;
  }

  iscpDocument(cp: any): boolean {
    return (this.isEmitida() && cp.document ===   this.invoiceService.invoice.receiver.document)
      ||  cp.document === this.invoiceService.invoice.sender.document ;
  }

  updateName(value: string): void {
    console.log('UPDATE NAME : ' + value);
    if (this.isEmitida()) {
      this.invoiceService.invoice.receiver.name = value;
      this.save();
    } else {
      if (this.invoiceService.invoice.sender) {
        this.invoiceService.invoice.sender.name = value;
        this.save();
      }
    }
  }

  updateTotal(value: number): void {
    this.invoiceService.invoice.total = +value;
    if (!this.isTicket() && this.invoiceService.invoice.taxes.length === 0) {
      const total = this.invoiceService.invoice.total;
      const base = Number(total) / 1.21;
      const quota = Number(base) * 0.21;

      this.invoiceService.invoice.taxes.push({
        tax: TaxType.VAT,
        percentage: 21.0,
        base: TediUtils.round(base),
        quota: TediUtils.round(quota)
      });
    } else if (!this.isTicket() && this.invoiceService.invoice.taxes.length === 1) {
      const percentage = this.invoiceService.invoice.taxes[0].percentage;
      const total = this.invoiceService.invoice.total;
      const base = Number(total) / ((100 + Number(percentage)) / 100);
      this.updateTaxBase(TediUtils.round(base), 0);
    }
    this.save();
  }

  updateFinanceDate(value: string, number: number): void {
    const date = new Date(this.getYear(value), this.getMonth(value) - 1, this.getDay(value), 2);
    this.invoiceService.invoice.finances[number].due_date = date;
    this.save();
  }

  updateFinancePay(): void {
    this.save();
  }

  updateFinanceAmount(value: number, number: number): void {
    this.invoiceService.invoice.finances[number].amount = value;
    this.save();
  }

  updateFinanceIban(value: string, number: number): void {
    this.invoiceService.invoice.finances[number].iban = value;
    this.save();
  }

  deleteFinance(number: number): void {
    this.invoiceService.invoice.finances.splice(number, 1);
    this.save();
  }

  createFinanceDate(value: string): void {
    const date = new Date(this.getYear(value), this.getMonth(value) - 1, this.getDay(value), 2);
    this.newFinance.due_date = date;
    this.createFinance();
  }

  isIbanDisabled(finance: Finance) {
    return PayMethod.BANK_TRANSFER !== finance.pay_method;
  }

  createFinanceAmount(value: number): void {
    this.newFinance.amount = value;
    this.createFinance();
  }

  createFinanceIban(value: string): void {
    this.newFinance.iban = value;
    this.createFinance();
  }

  createFinance(): void {
    if (this.newFinance.due_date && this.newFinance.amount && this.newFinance.pay_method ) {
      this.invoiceService.invoice.finances.push(this.newFinance);
      this.newFinance = AonMaker.createFinance();
      this.newFinanceDate.nativeElement.value = '';
      this.newFinanceAmount.nativeElement.value = '';
      this.newFinanceIban.nativeElement.value = '';
      this.showAddFinance = false;
      this.save();
    }
  }

  updateDetailDescription(value: string, number: number): void {
    this.invoiceService.invoice.details[number].description = value;
    this.save();
  }

  updateDetailPrice(value: number, number: number): void {
    this.invoiceService.invoice.details[number].price = this.isNumber(value) ? value : 0.0;
    this.calculateDetailPrice(number);
    this.save();
  }

  updateDetailDesc(value: number, number: number): void {
    this.invoiceService.invoice.details[number].discount = this.isNumber(value) ? value : 0.0;
    this.calculateDetailPrice(number);
    this.save();
  }

  updateDetailQuantity(value: number, number: number): void {
    this.invoiceService.invoice.details[number].quantity = this.isNumber(value) ? value : 0.0;
    this.calculateDetailPrice(number);
    this.save();
  }

  updateDetailVat(): void {
    this.calculateDetailTaxes(21.0);
    this.calculateDetailTaxes(10.0);
    this.calculateDetailTaxes(4.0);
    this.calculateDetailTaxes(0.0);
    this.save();
  }

  deleteDetail(number: number): void {
    this.invoiceService.invoice.details.splice(number, 1);
    this.updateDetailVat();
    this.save();
  }

  calculateDetailPrice(number: number): void {
    if ((this.invoiceService.invoice.details[number].price || this.invoiceService.invoice.details[number].price === 0.0 )
     && (this.invoiceService.invoice.details[number].quantity || this.invoiceService.invoice.details[number].quantity === 0.0)) {
      const price = this.invoiceService.invoice.details[number].price;
      const quantity = this.invoiceService.invoice.details[number].quantity;
      let amount = TediUtils.round(Number(quantity) * Number(price));
      if (this.invoiceService.invoice.details[number].discount) {
        amount = amount - amount * (this.invoiceService.invoice.details[number].discount / 100);
      }
      this.invoiceService.invoice.details[number].amount = TediUtils.round(amount);
      if (this.invoiceService.invoice.details[number].vat) {
        this.calculateDetailTaxes(21.0);
        this.calculateDetailTaxes(10.0);
        this.calculateDetailTaxes(4.0);
        this.calculateDetailTaxes(0.0);
      }
    }
  }

  calculateDetailTaxes(vat: number): void {
    let total = 0;
    for (let i = 0; i < this.invoiceService.invoice.details.length; i++) {
      if (this.invoiceService.invoice.details[i].vat == vat) {
        total = Number(total) + Number(this.invoiceService.invoice.details[i].amount);
      }
    }

    let updated = false;
    for (let i = 0; i < this.invoiceService.invoice.taxes.length; i++) {
      if (this.invoiceService.invoice.taxes[i].percentage === vat) {
        if (total !== 0) { this.updateTaxBase(TediUtils.round(total), i); } else { this.invoiceService.invoice.taxes.splice(i, 1); }
        updated = true;
      }
    }
    if (!updated && total !== 0) {
      this.invoiceService.invoice.taxes.push({
        tax: TaxType.VAT,
        percentage: vat,
        base: total,
        quota:  TediUtils.round(total * vat / 100)
      });
    }
    this.calculateTotal();
  }

  createDetailDescription(value: string): void {
    this.newDetail.description = value;
    this.createDetail();
  }

  createDetailQuantity(value: number): void {
    this.newDetail.quantity = value;
    this.createDetail();
  }

  createDetailDesc(value: number): void {
    this.newDetail.discount = value;
    this.createDetail();
  }

  createDetailPrice(value: number): void {
    this.newDetail.price = value;
    this.createDetail();
  }

  createDetail() {
    if (this.newDetail.price && this.newDetail.quantity) {
      const price = this.newDetail.price;
      const quantity = this.newDetail.quantity;
      let amount = TediUtils.round(Number(quantity) * Number(price));
      if (this.newDetail.discount) {
        amount = amount - amount * (this.newDetail.discount / 100);
      } else { this.newDetail.discount = 0.0; }
      this.newDetail.amount = TediUtils.round(amount);
    }
    if (this.newDetail.description && this.newDetail.price && this.newDetail.quantity
      && this.newDetail.discount && this.newDetail.vat) {
      if (this.invoiceService.invoice.details) {
         this.invoiceService.invoice.details.push(this.newDetail);
      } else {
         this.invoiceService.invoice.details = [this.newDetail];
      }
      this.newDetail = AonMaker.createInvoiceDetail();
      this.newDetailDescription.nativeElement.value = '';
      this.newDetailQuantity.nativeElement.value = '';
      this.newDetailDesc.nativeElement.value = '';
      this.newDetailPrice.nativeElement.value = '';
      this.showAddDetail = false;
      this.calculateDetailTaxes(21.0);
      this.calculateDetailTaxes(10.0);
      this.calculateDetailTaxes(4.0);
      this.calculateDetailTaxes(0.0);
    }
    this.save();
  }

  getPercentages(type: string) {
    if ('IVA' === type) {
       return this.iva_percentages;
    } else if ('IRPF' === type) {
      return this.irpf_percentages;
    }
  }

  calculateTotal() {
    let taxable_base = 0;
    let total = 0;
    for (let i = 0; i < this.invoiceService.invoice.taxes.length; i++) {
      if (this.invoiceService.invoice.taxes[i].tax === TaxType.VAT) {
        const base = this.invoiceService.invoice.taxes[i].base;
        const quota = this.invoiceService.invoice.taxes[i].quota;

        taxable_base = taxable_base + Number(base);
        total = total + Number(base) + Number(quota);
      } else if (this.invoiceService.invoice.taxes[i].tax  === TaxType.RETENTION) {
        const quota = this.invoiceService.invoice.taxes[i].quota;
        total = total - Number(quota);
      }
    }
    this.invoiceService.invoice.total = TediUtils.round(total);
  }

  updateTaxBase(value: number, number: number): void {
    this.invoiceService.invoice.taxes[number].base = value;
    const base = this.invoiceService.invoice.taxes[number].base;
    const percentage = this.invoiceService.invoice.taxes[number].percentage;
    this.invoiceService.invoice.taxes[number].quota = TediUtils.round(base * percentage / 100);
    this.calculateTotal();
    this.save();
  }

  updateTaxPercentage(number: number): void {
    const base = this.invoiceService.invoice.taxes[number].base;
    const percentage = this.invoiceService.invoice.taxes[number].percentage;
    this.invoiceService.invoice.taxes[number].quota = TediUtils.round(base * percentage / 100);
    this.calculateTotal();
    this.save();
  }

  updateTaxQuota(value: number, number: number): void {
    this.invoiceService.invoice.taxes[number].quota = value;

    const quota = this.invoiceService.invoice.taxes[number].quota;
    const percentage = this.invoiceService.invoice.taxes[number].percentage;
    this.invoiceService.invoice.taxes[number].base = TediUtils.round(Number(quota) * 100 / Number(percentage));
    this.calculateTotal();
    this.save();
  }

  addTax(): void {
    this.newTax.tax = TaxType.VAT;
    this.isNewTax = true;
  }

  createTaxBase(value: number): void {
    this.newTax.base = value;
    if (this.newTax.percentage !== undefined && this.newTax.base) {
      const base = this.newTax.base;
      const percentage = this.newTax.percentage;
      this.newTax.quota = TediUtils.round(base * percentage / 100);
    }
    this.createTax();
  }

  createTaxPercentage(): void {
    if (this.newTax.percentage && this.newTax.base) {
      const base = this.newTax.base;
      const percentage = this.newTax.percentage;
      this.newTax.quota = TediUtils.round(Number(base) * Number(percentage) / 100);
    }
    this.createTax();
  }

  createTaxQuota(value: number): void {
    this.newTax.quota = value;
    if (this.newTax.percentage && this.newTax.quota) {
      const quota = this.newTax.quota;
      const percentage = this.newTax.percentage;
      this.newTax.base = TediUtils.round(Number(quota) * 100 / Number(percentage));
    }
    this.createTax();
  }

  createTax() {
    if (this.newTax.base && this.newTax.percentage !== undefined && this.newTax.quota !== undefined && this.newTax.tax) {
      let added = false;
      if (!this.invoiceService.invoice.taxes) {
        this.invoiceService.invoice.taxes = [];
      }
      for (let i = 0; i < this.invoiceService.invoice.taxes.length; i++) {
        if (this.invoiceService.invoice.taxes[i].percentage === this.newTax.percentage &&
          this.invoiceService.invoice.taxes[i].tax === this.newTax.tax) {
            const base1 = this.newTax.base;
            const base2 = this.invoiceService.invoice.taxes[i].base;
            this.updateTaxBase(Number(base1) + Number(base2), i);
            added = true;
        }
      }
      if (!added) { this.invoiceService.invoice.taxes.push(this.newTax); }
      this.newTax = AonMaker.createInvoiceTax();
      this.newTaxBase.nativeElement.value = '';
      this.newTaxQuota.nativeElement.value = '';
      this.isNewTax = false;
      this.calculateTotal();
      this.save();
    }
  }

  hasStatus(): boolean {
    return this.invoiceService.invoice.status !== undefined;
  }

  hasDate(): boolean {
    return this.invoiceService.invoice.date !== undefined;
  }

  hasTotal(): boolean {
    return this.invoiceService.invoice.total !== undefined;
  }

  hasReceiverDocument(): boolean {
    return  this.invoiceService.invoice.receiver.document !== undefined;
  }

  hasReceiverName(): boolean {
    return this.invoiceService.invoice.receiver.name !== undefined;
  }

  hasReceiverAddress(): boolean {
    return this.invoiceService.invoice.receiver.address !== undefined;
  }

  hasSenderDocument(): boolean {
    return this.invoiceService.invoice.sender.document !== undefined;
  }

  hasSenderName(): boolean {
    return this.invoiceService.invoice.sender.name !== undefined;
  }

  hasSenderAddress(): boolean {
    return this.invoiceService.invoice.sender.address !== undefined;
  }

  hasImgFile(): boolean {
    if (this.invoiceService.invoice.file  !== undefined) {
      const contenType = this.invoiceService.invoice.file.content_type || this.invoiceService.invoice.file.content_type;
      return contenType && contenType.includes('image');
    } else {
      return false;
    }
  }

  hasPdfFile(): boolean {
    if (this.invoiceService.invoice.file) {
      const contenType = this.invoiceService.invoice.file.content_type || this.invoiceService.invoice.file.content_type;
      return contenType && contenType.includes('pdf');
    } else {
      return false;
    }
  }

  hasFile(): boolean {
    return this.invoiceService.invoiceFile && this.invoiceService.invoiceFile.url;
  }

  isComment(): boolean {
    return this.invoiceService.invoice.comments !== undefined && this.invoiceService.invoice.comments.length > 0;
  }

  isTicket(): boolean {
    return this.invoiceService.invoice.type === InvoiceType.UNDEDUCTIBLE
      || this.invoiceService.newType === InvoiceType.UNDEDUCTIBLE;
  }

  isEmitida(): boolean {
    return this.invoiceService.invoice.type === InvoiceType.PURCHASE
      || this.invoiceService.newType === InvoiceType.PURCHASE;
  }

  isAccepted(): boolean {
    return this.invoiceService.invoice.status === InvoiceStatus.SCORED;
  }


  isPending(): boolean {
    return this.invoiceService.invoice.status === InvoiceStatus.PENDING;
  }

  isTrash(): boolean {
    return this.invoiceService.invoice.status === InvoiceStatus.TRASH;
  }

  isRefused(): boolean {
    return this.invoiceService.invoice.status === InvoiceStatus.REFUSED;
  }

  isReadOnly(): boolean {
    return !this.isPending() || this.service.loading;
  }

  isFileExpanded(): boolean {
    return this.invoiceService.isFileExpanded;
  }

  save(): void {
    if (this.invoiceService.isNew || !this.invoiceService.invoice.source) {
      this.invoiceService.invoice.source = this.service.isMobile ? 'mobile' : 'web';
    } else if (!this.invoiceService.isNew) {
      this.invoiceService.invoiceList[this.invoiceService.invoiceIndex] = this.invoiceService.invoice;
    }
    this.invoiceService.createInvoice().subscribe(
      (r: Invoice) => {
        this.invoiceService.setInvoice(AonMaker.createInvoice(r));
      },
      (e: any) => TediUtils.showError(this.dialog, e.error)
    );

  }

  ticketPaymethod(): void {
    const finance = AonMaker.createFinance();
    finance.due_date = this.invoiceService.invoice.date;
    finance.amount = this.invoiceService.invoice.total;
    finance.pay_method = this.paymethod;
    finance.iban = '';
    if (this.invoiceService.invoice.finances.length > 0) {
      this.invoiceService.invoice.finances[0] = finance;
    } else { this.invoiceService.invoice.finances.push(finance); }
    this.save();
  }

  isNumber(n: any): boolean {
    return !isNaN(parseFloat(n)) && isFinite(n);
  }


  getUserFormat(user: string) : string {
    return this.isMobile() ? user.split('@')[0] : user;
  }

  getDateFormat(date: Date | string ): string {
    return typeof date === 'string'
      ? date.substring(0,16).replace('T', ' ')
      :  date.toISOString().substring(0,16).replace('T', ' ');
  }

  editComment(number: number) : void {
    const dialogRef = this.dialog.open(CommentDialogComponent, {
      height: '230px',
      width: '300px',
      panelClass: 'tedi-user-dialog-panel',
      data: this.invoiceService.invoice.comments[number]
    });

    dialogRef.afterClosed().subscribe(result => {

      if (result && result !== '') {
        const comment = {
          date: new Date(),
          comment: result,
          user: this.service.getUserEmail()
        };
        this.invoiceService.invoice.comments[number] = comment;
        this.save();
      }
    });
  }

  deleteComment(number: number) : void {
    this.invoiceService.invoice.comments.splice(number, 1);
    this.save();
  }

  isCommentUser(user: string) : boolean {
    return user === this.service.user.email;
  }


  getFullAddress(address: Address) : string{
    return AonMaker.getFullAddress(address);
  }
}

/** Error when invalid control is dirty, touched, or submitted. */
export class MyErrorStateMatcher implements ErrorStateMatcher {
  isErrorState(control: FormControl | null, form: FormGroupDirective | NgForm | null): boolean {
    const isSubmitted = form && form.submitted;
    return !!(control && control.invalid && (control.dirty || control.touched || isSubmitted));
  }
}
