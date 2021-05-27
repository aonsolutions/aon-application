import { Component, ViewChild, ElementRef } from '@angular/core';
import { MatDialog, MatSnackBar } from '@angular/material';
import { Router } from '@angular/router';
import { Location } from '@angular/common';
import { Observable } from 'rxjs';
import { mergeMap } from 'rxjs/operators';
import { SharedService, ExcelService, AonService } from '../../services/services';
import { Invoice, InvoiceStatus } from '../../models/models';
import { InvoiceService } from '../invoice.service';
import { SureDialogComponent } from '../../components/dialogs/sure-dialog/sure-dialog.component';
import { CommentDialogComponent } from '../../components/dialogs/comment-dialog/comment-dialog.component';
import { SendDialogComponent } from '../../components/dialogs/send-dialog/send-dialog.component';
import { InvoiceNewDialogComponent } from '../invoice-new-dialog/invoice-new-dialog.component';
import { InvoiceOptionsDialogComponent } from '../invoice-options-dialog/invoice-options-dialog.component';
import { NgxImageCompressService } from 'ngx-image-compress';
import { TediUtils } from '../../utils/tedi-utils';
import { RootLoader } from '../../utils/loader';

@Component({
  selector: 'app-invoice-toolbar',
  templateUrl: './invoice-toolbar.component.html',
  styleUrls: ['./invoice-toolbar.component.css'],
})
export class InvoiceToolbarComponent {
  sheet = false;
  multiple = false;
  invoiceSelection: any[];
  acceptedMimeTypes = [
    'image/gif',
    'image/jpeg',
    'image/png',
    'application/pdf'
  ];

  @ViewChild('fileInput', {static: false}) fileInput: ElementRef;
  constructor(public dialog: MatDialog, public snackBar: MatSnackBar,
      private router: Router, public service: SharedService,
      public invoiceService: InvoiceService, public aonService: AonService,
      private imageCompress: NgxImageCompressService,
      private excelService: ExcelService, private location: Location) {

    this.service.isSheet.subscribe(value => {
      this.sheet = value;
    });

    this.service.isMultipleSelection.subscribe(value => {
      this.multiple = value;
    });

    this.invoiceService.invoiceSelection.subscribe(value => {
      this.invoiceSelection = value;
    });
  }

  getCompanyName() : string {
    return this.service.getCompanyName();
  }
  showMoreOptions(): boolean {
    return (this.sheet && this.hasInvoice()) || this.multiple;
  }

  hasInvoice(): boolean {
    return (this.invoiceService.invoice && this.invoiceService.invoice.id) ? true : false;
  }

  isFileExpanded(): boolean {
    return this.invoiceService.isFileExpanded;
  }

  openSidenav(): void {
    this.invoiceService.expandMenu();
  }

  toggleFile(): void {
    this.invoiceService.expandFile();
  }

  invoiceNew(): void {
    const dialogRef = this.dialog.open(InvoiceNewDialogComponent, {
      width: '250px',
      backdropClass: 'tedi-user-dialog-backdrop',
      panelClass: 'aon-user-dialog-panel',
      position: {
        top: '50px',
        right: '20px'
      },
      data: {}
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        const srr = this.router.routeReuseStrategy.shouldReuseRoute;
        if (this.sheet) {
          this.router.routeReuseStrategy.shouldReuseRoute = function() {
            return false;
          };
        }

        this.invoiceService.isNew = true;
        this.invoiceService.newType = result.type;
        RootLoader.angularPanel(this.router, this.location, '/invoice/sheet').then(r => {
          this.router.routeReuseStrategy.shouldReuseRoute = srr;
          return r;
        });
      }
    });
  }

  showOptions(): void {
    const dialogRef = this.dialog.open(InvoiceOptionsDialogComponent, {
      width: '250px',
      backdropClass: 'tedi-user-dialog-backdrop',
      panelClass: 'aon-user-dialog-panel',
      position: {
        top: '50px',
        right: '50px'
      },
      data: {
        company: this.service.getActualCompany(),
        uuid: this.invoiceService.invoice ? this.invoiceService.invoice.id : undefined,
        sheet: this.sheet,
        multiple: this.multiple,
        status: this.invoiceService.invoice.status,
        aon: false
      }
    });

    dialogRef.afterClosed().subscribe((result: any) => {
      if (result) {
        if (result === 'send') {
           this.invoiceSend();
        } else if (result === 'comment') {
          this.invoiceComment();
        } else if (result === 'sendMultiple') {
          this.invoiceMultipleSend();
        } else if (result === 'delete') {
          this.invoiceDelete();
        } else if (result === 'deleteMultiple') {
          this.invoiceMultipleDelete();
        } else if (result === 'refused') {
          this.invoiceRefused();
        } else if (this.isStatus(result)) {
          this.invoiceStatus(result);
        } else if (this.isMultipleStatus(result)) {
          this.invoiceMultipleStatus(result);
        } else if (result === 'print') {
          this.invoiceDownload();
        } else if (result === 'aon') {
          this.exportAon();
        } else if (result === 'excel') {
          this.exportExcel();
        } else if (result.type === 'upload') {
          delete result['type'];
          this.service.loading = true;
          this.aonService.createInvoice(result)
          .subscribe(() => {
            this.invoiceService.invoice.file = {
              content_type: result.content_type
            };
            this.invoiceService.invoiceFile = undefined;
            this.invoiceService.getFile().subscribe();
            this.invoiceService.expandFile(true);
            this.service.loading = false;
          });
        }
      }
    });
  }

  isMobile(): boolean {
    return this.service.isMobile;
  }

  isAccepted(): boolean {
    return  this.invoiceService.invoice && this.invoiceService.invoice.status === InvoiceStatus.SCORED;
  }

  isPending(): boolean {
    return  this.invoiceService.invoice && this.invoiceService.invoice.status === InvoiceStatus.PENDING;
  }

  isRefused(): boolean {
    return  this.invoiceService.invoice && this.invoiceService.invoice.status === InvoiceStatus.REFUSED;
  }

  isTrash(): boolean {
    return  this.invoiceService.invoice && this.invoiceService.invoice.status === InvoiceStatus.TRASH;
  }

  hasFile(): boolean {
    return this.invoiceService.invoiceFile !== undefined
      && this.invoiceService.invoiceFile.url !== undefined;
  }

  isStatus(status: string): boolean {
    return status === 'accepted' || status === 'verified' || status === 'pending'
     || status === 'trash' || status === 'refused' || status === 'inbox';
  }

  invoiceStatus(status: InvoiceStatus): void {
    this.invoiceService.invoice.status = status;
    this.invoiceService.saveInvoice();
    const st = this.getStatusDescription(status);
    this.snackBar.open(`La Factura ${st}.`, '', {
      duration: 2000,
    });
  }

  isMultipleStatus(status: string): boolean {
    return status === 'acceptedMultiple' || status === 'verifiedMultiple' || status === 'pendingMultiple'
     || status === 'trashMultiple' || status === 'refusedMultiple' || status === 'inboxMultiple';
  }

  invoiceMultipleStatus(status: string): void {
    if (status === 'pendingMultiple') {
      this.invoiceSelection.forEach(inv => {
        inv.verified = false;
        inv.oldStatus = inv.status;
        inv.status = 'inbox';
        this.invoiceService.saveInvoice(inv);
        this.invoiceService.setFilter(this.invoiceService.filter);
      });
    }
  }

  getStatusDescription(status: InvoiceStatus): string {
    if (InvoiceStatus.SCORED === status) {
      return 'ha sido Contabilizada';
    } else if (InvoiceStatus.TRASH === status) {
      return 'se ha movido a la Papelera';
    } else if (InvoiceStatus.PENDING === status) {
      return 'se ha movido a Pendientes';
    } else if (InvoiceStatus.REFUSED === status) {
      return 'ha sido Rechazada';
    }
  }

  invoiceRefused(): void {
    const dialogRef = this.dialog.open(CommentDialogComponent, {
      height: '230px',
      width: '300px',
      panelClass: 'aon-user-dialog-panel',
      data: {}
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result !== '') {
        const comment = {
          date: new Date(),
          comment: result,
          user: this.service.getUserEmail()
        };
        if (this.invoiceService.invoice.comments) {
            this.invoiceService.invoice.comments.push(comment);
        } else { this.invoiceService.invoice.comments = [comment]; }
      }
      this.invoiceStatus(InvoiceStatus.REFUSED);
    });
  }

  invoiceComment(): void {
    const dialogRef = this.dialog.open(CommentDialogComponent, {
      height: '230px',
      width: '300px',
      panelClass: 'aon-user-dialog-panel'
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result && result !== '') {
        const comment = {
          date: new Date(),
          comment: result,
          user: this.service.getUserEmail()
        };
        if (this.invoiceService.invoice.comments) {
          this.invoiceService.invoice.comments.push(comment);
        } else { this.invoiceService.invoice.comments = [comment]; }
        this.invoiceService.saveInvoice();
      }
    });
  }

  invoiceSend(): void {
    const dialogRef = this.dialog.open(SendDialogComponent, {
      height: '200px',
      panelClass: 'aon-user-dialog-panel',
      data: {}
    });

    dialogRef.afterClosed().subscribe(result => {
      const o = {
        company: this.invoiceService.invoice.domain,
        to: result,
        invoices: [{
          company: this.invoiceService.invoice.domain,
          uuid: this.invoiceService.invoice.id,
          date: this.invoiceService.invoice.date,
          total: this.invoiceService.invoice.total,
          reference: this.invoiceService.invoice.reference
        }]
      };
      this.invoiceService.sendInvoice(o)
      .subscribe(
        () => {
          this.snackBar.open('La Factura se ha enviado correctamente.', '', {
            duration: 2000,
          });
        },
        error => {
          TediUtils.showError(this.dialog, error.error);
        }
      );
    });
  }

  invoiceMultipleSend(): void {
    const dialogRef = this.dialog.open(SendDialogComponent, {
      height: '200px',
      panelClass: 'aon-user-dialog-panel',
      data: {}
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        const invoices = this.invoiceSelection.map((t) => {
          return {
            company: t.company,
            uuid: t.uuid,
            date: t.date,
            total: t.total,
            reference: t.reference
          };
        });
        const o = {
          company: this.service.company.document,
          to: result,
          invoices: invoices
        };
        this.invoiceService.sendInvoice(o)
        .subscribe(
          () => {
            this.snackBar.open('Las Facturas se han enviado correctamente.', '', {
              duration: 2000,
            });
          },
          error => {
            TediUtils.showError(this.dialog, error.error);
          }
        );
      }
    });
  }

  invoiceDelete(): void {
    const dialogRef = this.dialog.open(SureDialogComponent, {
      height: this.isMobile? '175px':'150px',
      panelClass: 'aon-user-dialog-panel',
      data: {}
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result && this.invoiceService.invoice.status === InvoiceStatus.TRASH) {
        this.invoiceService.deleteInvoice().subscribe(() => {
          this.invoiceService.isDeleting = true;
          this.invoiceService.invoiceList = undefined;
          RootLoader.angularPanel(this.router, this.location, this.isMobile() ? '/invoice/list' : '/invoice/table');
          this.snackBar.open('La Factura se ha borrado correctamente.', '', {
            duration: 2000,
          });
        });
      } else if (result) {
        this.invoiceStatus(InvoiceStatus.TRASH);
      }
    });
  }

  invoiceMultipleDelete(): void {
    if (this.invoiceService.filter.status !== 'trash') {
      let obs: Observable<Invoice>;
      this.invoiceSelection.forEach(inv => {
        inv.oldStatus = inv.status;
        inv.status = 'trash';
        obs = obs ? obs.pipe(mergeMap(() => this.invoiceService.createInvoice(inv))) : this.invoiceService.createInvoice(inv);
      });
      obs.subscribe(() => {
        this.invoiceService.invoiceList = undefined;
        this.invoiceService.setFilter(this.invoiceService.filter);
        this.snackBar.open('Las Facturas seleccionadas se han movido a la papelera.', '', {
          duration: 4000,
        });
      });
    } else {
      const dialogRef = this.dialog.open(SureDialogComponent, {
        height: '150px',
        panelClass: 'aon-user-dialog-panel',
        data: {}
      });
      dialogRef.afterClosed().subscribe(result => {
        if (result) {
          const ids = this.invoiceSelection.map(t => t.uuid);
          this.invoiceService.deleteInvoices(ids).subscribe(() => {
            this.invoiceService.invoiceList = undefined;
            this.invoiceService.setFilter(this.invoiceService.filter);
            this.service.isMultipleSelection.next(false);
            this.snackBar.open('Las Facturas seleccionadas se han borrado correctamente.', '', {
              duration: 2000,
            });
          });
        }
      });
    }
  }

  invoiceDownload(): void {
    if(this.hasFile()){
      window.open(this.invoiceService.invoiceFile.url);
    } else {
      window.open('url', '_blank', null);
    }
  }

  exportAon(): void {
    // this.invoiceService.exportAon(this.user.aon, this.invoiceService.invoice)
    // .subscribe();
  }

  exportExcel(): void {
    this.excelService.exportAsExcelFile(this.invoiceSelection);
  }

  isPrev(): boolean {
    return this.invoiceService.invoiceIndex > 0;
  }

  prev(): void {
    this.invoiceService.invoiceIndex--;
    this.changeInvoice();

  }

  isNext(): boolean {
    return this.invoiceService.invoiceIndex < this.invoiceService.invoiceList.length - 1;
  }

  next(): void {
    this.invoiceService.invoiceIndex++;
    this.changeInvoice();
  }

  preview() {
    const file = this.fileInput.nativeElement.files[0];
    const READER = new FileReader();
    READER.readAsDataURL(file);
    READER.onload = (_event) => {
      if (file.type.match(/image\/*/) == null) {
        this.attach(READER.result as string, file.type)
      } else {
        var img = new Image();
        img.onload = () => {
          let ratio: number;
          if ( img.height > img.width ) {
            ratio = (100 / (img.height / 1024));
          } else {
            ratio = (100 / ( img.width / 1024));
          }

          let orientation: any;
          this.imageCompress.compressFile(img.src, orientation, ratio, 100).then(
            result => this.attach(result as string, file.type)
          );
        };
        img.src = READER.result as string;
      }
    };
  }

  attach(fileDataUri: string,  mimetype: string): void {
    if (fileDataUri.length > 0) {
      const base64File = fileDataUri.split(',')[1];
      const cp = this.service.getActualCompany();
      const data = {
        company: cp ,
        content: base64File,
        contentType: mimetype,
        contentEncoding: 'base64',
        invoice: undefined,
        status: undefined
      };
      if (this.invoiceService.isNew) { data.invoice = this.invoiceService.invoice; }
      this.service.loading = true;
      this.aonService.createInvoice(data)
      .subscribe((r: Invoice) => {
        if (!this.invoiceService.invoice.id) {
          this.invoiceService.setInvoice(r);
        }
        this.invoiceService.invoiceFile = undefined;
        this.invoiceService.getFile().subscribe();
        this.invoiceService.expandFile(true);
        this.service.loading = false;
      });
    }
  }

  validateFile(file: any) {
    return this.acceptedMimeTypes.includes(file.type); // && file.size < 500000;
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
}
