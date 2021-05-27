import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material';
import { Component, Inject, ViewChild, ElementRef } from '@angular/core';
import { MatIconRegistry } from '@angular/material/icon';
import { DomSanitizer } from '@angular/platform-browser';
import { InvoiceService } from '../invoice.service';
import { NgxImageCompressService } from 'ngx-image-compress';
import { environment } from '../../../environments/environment';

@Component({
  templateUrl: './invoice-options-dialog.component.html',
  styleUrls: ['./invoice-options-dialog.component.css'],
})
export class InvoiceOptionsDialogComponent {

  company: string;
  uuid: number;
  sheet: boolean;
  multiple: boolean;
  status: string;
  aon = false;
  acceptedMimeTypes = [
    'image/gif',
    'image/jpeg',
    'image/png',
    'application/pdf'
  ];

  @ViewChild('fileInput', {static: false}) fileInput: ElementRef;
  constructor(public dialogRef: MatDialogRef<InvoiceOptionsDialogComponent>,
          @Inject(MAT_DIALOG_DATA) public data: any,
          private matIconRegistry: MatIconRegistry,
          private domSanitizer: DomSanitizer,
          private invoiceService: InvoiceService,
          private imageCompress: NgxImageCompressService
        ) {
    this.sheet = data.sheet;
    this.multiple = data.multiple;
    this.status = data.status;
    this.aon = data.aon;
    this.company = data.company;
    this.uuid = data.uuid;
    this.matIconRegistry.addSvgIcon(
      'aon',
      this.domSanitizer.bypassSecurityTrustResourceUrl('../../../assets/icons/aon.svg')
    );
    this.matIconRegistry.addSvgIcon(
      'excel',
      this.domSanitizer.bypassSecurityTrustResourceUrl('../../../assets/icons/excel.svg')
    );
  }

  accept(): void {
    this.dialogRef.close('verified');
  }

  refuse(): void {
    this.dialogRef.close('refused');
  }

  revise(): void {
    this.dialogRef.close('pending');
  }

  reviseMultiple(): void {
    this.dialogRef.close('pendingMultiple');
  }

  print(): void {
    this.dialogRef.close('print');
  }

  send(): void {
    this.dialogRef.close(this.multiple ? 'sendMultiple' : 'send');
  }

  comment(): void {
    this.dialogRef.close('comment');
  }

  delete(): void {
    if (this.multiple || this.isTrash()) {
      this.dialogRef.close(this.multiple ? 'deleteMultiple' : 'delete');
    } else {
      this.dialogRef.close('trash');
    }
  }

  excel(): void {
    this.dialogRef.close('excel');
  }

  aonc(): void {
    this.dialogRef.close('aon');
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
      const data = {
        type: 'upload',
        update: this.invoiceService.invoice.total && this.invoiceService.invoice.total !== 0,
        company: this.company,
        uuid: this.uuid,
        status: this.status,
        content: base64File,
        contentType: mimetype,
        contentEncoding: 'base64'
      };
      this.dialogRef.close(data);
    }
  }

  validateFile(file: any) {
    return this.acceptedMimeTypes.includes(file.type); // && file.size < 500000;
  }

  isSnapshot(): boolean {
    return !environment.production;
  }

  isAccepted(): boolean {
    return this.status === 'accepted';
  }

  isVerified(): boolean {
    return this.status === 'inbox' && this.invoiceService.invoice.verified;
  }

  isPending(): boolean {
    return this.status === 'inbox' && !this.invoiceService.invoice.verified;
  }

  isTrash(): boolean {
    return this.status === 'trash';
  }

  isMultipleTrash(): boolean {
    return this.multiple && this.invoiceService.filter.status === 'trash';
  }

  isRefused(): boolean {
    return this.status === 'refused';
  }

  isMultipleRefused(): boolean {
    return this.multiple && this.invoiceService.filter.status === 'refused';
  }
}
