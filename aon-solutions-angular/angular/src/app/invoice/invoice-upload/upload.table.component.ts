import { Component, OnInit, ViewChild, OnDestroy, ElementRef } from '@angular/core';
import { IImportTypeAccepter, IImportTypeVisitor, IImportType, ImportTypeUtils } from '../../lib/import-types';
import { MatTableDataSource, MatTable, MatDialog } from '@angular/material';
import { SharedService, AonService } from '../../services/services';
import { InvoiceService } from '../invoice.service';
import { Observable } from 'rxjs';
import { concatMap } from 'rxjs/operators';
import { Router } from '@angular/router';
import { Location } from '@angular/common';
import { TediUtils } from '../../utils/tedi-utils';
import { RootLoader } from '../../utils/loader';

@Component({
  selector: 'app-upload-table',
  templateUrl: './upload.table.component.html',
  styleUrls: ['./upload.table.component.css']
})
export class UploadTableComponent implements OnInit, OnDestroy {
  datasource = new MatTableDataSource<FileWrapper>();
  files = new Array<FileWrapper>();
  displayedColumns: String[];

  @ViewChild(ElementRef, {static: false}) targetDiv: ElementRef;
  @ViewChild(MatTable, {static: false}) public matTable: MatTable<FileWrapper>;

  constructor(private service: SharedService, public invoiceService: InvoiceService,
              public dialog: MatDialog, private router: Router, public aonService: AonService,
              private location: Location) {
    this.service.filesToUpload.subscribe(result => {
      this.displayedColumns = ['iconButton', 'name', 'icon', 'type', 'size'];
      this.files = new Array<FileWrapper>();
      const accepter = new ImportTypeAccepter();
      for (let i = 0; i < result.length; i++) {
        this.files.push(new FileWrapper(result[i]
          , ImportTypeUtils.get(result[i].type).accept(accepter)
            ? FileWrapperState.PENDING
            : FileWrapperState.UNSUPPORTED));
      }
      this.refreshTable();
    }
      , error => { TediUtils.showError(this.dialog, error); }
    );
  }

  ngOnInit() {
  }
  ngOnDestroy() { }

  refreshTable() {
    this.datasource = new MatTableDataSource<FileWrapper>(this.files);
    this.matTable.renderRows();
  }

  onClick(event: Event, fw: FileWrapper) : void {
    if ( fw.state === FileWrapperState.DONE) {
      this.onInvoice(event, fw);
    } else if ( fw.state === FileWrapperState.UNSUPPORTED) {
      TediUtils.showError(this.dialog, 'De momento, no importamos los archivos de tipo [' + fw.type.friendlyType + '].');
    } else {
      this.onImportFile(event, fw);
    }
  }

  onClickAll(event: Event, fw: FileWrapper) : void {
    for(let i: number = 0; i < this.files.length; i++) {
      this.onClick(event, this.files[i]);
    }
  }

  onInvoice(event: Event, fw: FileWrapper) {
    this.invoiceService.setInvoice(fw.invoice);
    RootLoader.angularPanel(this.router, this.location, '/invoice/sheet');
  }

  onImportFile(event: Event, fw: FileWrapper) {
    fw.isLoading  = true;
    const visitor: ImportVisitor = new ImportVisitor(this.service, this.aonService);
    Observable.create(observer => {
      if (!fw || !(fw.file instanceof Blob)) {
        observer.error(new Error('`blob` must be an instance of File or Blob.'));
        return;
      }


      const reader = new FileReader();
      reader.onload = () => observer.next(reader.result);
      reader.onerror = error => observer.error(error);
      reader.onabort = error => observer.error(error);
      reader.onloadend = () => observer.complete();
      reader.readAsDataURL(fw.file);
    })
      .pipe(concatMap(
        result => fw.type.visit(visitor, result)
      ))
      .subscribe(
        result => {
          fw.state = FileWrapperState.DONE;
          console.log( 'Result ...: ' + JSON.stringify(result) );
          fw.invoice = result;
          fw.isLoading  = false;
        },
        error => {
          event.stopPropagation();
          console.log(error);
          fw.state = FileWrapperState.ERROR;
          fw.isLoading  = false;
          TediUtils.showError(this.dialog, error);
        },
        () => {
          console.log('Complete!');
          fw.isLoading  = false;
        }
      );
  }
}


class ImportTypeAccepter implements IImportTypeAccepter {
  acceptCSV(item: IImportType) { return true; }
  acceptXLSX(item: IImportType) { return true; }
  acceptEXCEL(item: IImportType) { return true; }
  acceptMS_EXCEL(item: IImportType) { return true; }
  acceptODS(item: IImportType) { return true; }
  acceptPDF(item: IImportType) { return true; }
  acceptHTML(item: IImportType) { return true; }
  acceptTEXT(item: IImportType) { return true; }
  acceptGIF(item: IImportType) { return true; }
  acceptJPEG(item: IImportType) { return true; }
  acceptPNG(item: IImportType) { return true; }
  acceptOGG(item: IImportType) { return false; }
  acceptMP4(item: IImportType) { return false; }
  acceptMPEG(item: IImportType) { return false; }
  acceptOther(item: IImportType) { return false; }
}

export enum FileWrapperState {
  UNSUPPORTED = 'No soportado',
  PENDING = 'Pendiente',
  DONE = 'Hecho',
  ERROR = 'Con errrores',
}

export class FileWrapper {
  type: IImportType;
  file: File;
  state: FileWrapperState;
  invoice: any;
  isLoading = false;

  constructor(public pFile: File, state?: FileWrapperState) {
    this.file = pFile;
    this.type = ImportTypeUtils.get(this.file.type);
    this.state = (state) ? state : FileWrapperState.PENDING;
  }

  decoratedSize(): string {
    const units = ['bytes', 'kB', 'MB', 'GB'];
    let s = this.file.size;
    for (let i = 0; i < units.length; i++) {
      if (s < 1000) {
        return s.toLocaleString(undefined, { maximumFractionDigits: 1 }) + ' ' + units[i];
      }
      s = (s / 1000);
    }
    return this.file.size + ' bytes';
  }

  isDone(): boolean {
    return (this.state === FileWrapperState.DONE);
  }
  isPending(): boolean {
    return (this.state === FileWrapperState.PENDING);
  }
  isError(): boolean {
    return (this.state === FileWrapperState.ERROR);
  }

  stateIcon(): string {
    if (this.state === FileWrapperState.PENDING) { return 'cloud_upload'; }
    if (this.state === FileWrapperState.DONE) { return 'done'; }
    if (this.state === FileWrapperState.ERROR) { return 'error'; }
    return 'not_interested';
  }
}

class ImportVisitor implements IImportTypeVisitor {
  constructor(private service: SharedService, private aonService: AonService) {
  }

  visitCSV(item: IImportType, data: any): Observable<any> {
    return this.visitBatchFile(item, data);
  }
  visitXLSX(item: IImportType, data: any): Observable<any> {
    return this.visitBatchFile(item, data);
  }
  visitEXCEL(item: IImportType, data: any): Observable<any> {
    return this.visitBatchFile(item, data);
  }
  visitMS_EXCEL(item: IImportType, data: any): Observable<any> {
    return this.visitBatchFile(item, data);
  }
  visitODS(item: IImportType, data: any): Observable<any> {
    return this.visitBatchFile(item, data);
  }
  visitHTML(item: IImportType, data: any): Observable<any> {
    return this.visitBatchFile(item, data);
  }
  visitTEXT(item: IImportType, data: any): Observable<any> {
    return this.visitBatchFile(item, data);
  }
  visitOGG(item: IImportType, data: any): Observable<any> {
    return this.visitNotSupported(item, data);
  }
  visitMP4(item: IImportType, data: any): Observable<any> {
    return this.visitNotSupported(item, data);
  }
  visitMPEG(item: IImportType, data: any): Observable<any> {
    return this.visitNotSupported(item, data);
  }
  visitOther(item: IImportType, data: any): Observable<any> {
    return this.visitNotSupported(item, data);
  }
  visitPDF(item: IImportType, data: any): Observable<any> {
    return this.visitImage(item, data);
  }
  visitGIF(item: IImportType, data: any): Observable<any> {
    return this.visitImage(item, data);
  }
  visitJPEG(item: IImportType, data: any): Observable<any> {
    return this.visitImage(item, data);
  }
  visitPNG(item: IImportType, data: any): Observable<any> {
    return this.visitImage(item, data);
  }

  visitBatchFile(item: IImportType, data: any): Observable<any> {
    console.log('visitBatchFile -->' + JSON.stringify(data));
    const base64File: string = data.split(',')[1];
    const company: string = this.service.getActualCompany();
    const invoiceData = {
      company: company,
      content: base64File,
      contentType: item.mimeType,
      source: this.service.isMobile ? 'mobile' : 'web',
      contentEncoding: 'base64',
      status: undefined
    };
    console.log('visitBatchFile -->' + JSON.stringify(invoiceData));
    return this.aonService.createInvoice(invoiceData);
  }

  visitImage(item: IImportType, data: any): Observable<any> {
    console.log('visitImage -->' + JSON.stringify(data));
    const base64File: string = data.split(',')[1];
    const company: string = this.service.getActualCompany();
    const invoiceData = {
      company: company,
      content: base64File,
      contentType: item.mimeType,
      contentEncoding: 'base64',
      status: undefined
    };
    console.log('createInvoice -->' + JSON.stringify(invoiceData));
    return this.aonService.createInvoice(invoiceData);
  }

  visitNotSupported(item: IImportType, data: any): Observable<any> {
    return Observable.create(function (observer) {
      observer.error('De momento, no importamos los archivos de tipo [' + item.friendlyType + '].');
    });
  }

}
