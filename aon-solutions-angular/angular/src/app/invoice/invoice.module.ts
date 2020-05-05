import { CUSTOM_ELEMENTS_SCHEMA, NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MaterialModule } from '../material/material.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { InfiniteScrollModule } from 'ngx-infinite-scroll';
import { InvoiceComponent } from './invoice/invoice.component';
import { InvoiceToolbarComponent } from './invoice-toolbar/invoice-toolbar.component';
import { InvoiceTableComponent } from './invoice-table/invoice-table.component';
import { InvoiceMobileListComponent } from './invoice-mobile-list/invoice-mobile-list.component';

import { InvoiceSheetComponent } from './invoice-sheet/invoice-sheet.component';
import { InvoiceService } from './invoice.service';
import { CreateInvoiceDialogComponent } from './create-invoice-dialog/create-invoice-dialog.component';
import { InvoiceDetailDialogComponent } from './invoice-detail-dialog/invoice-detail-dialog.component';
import { InvoiceFinanceDialogComponent } from './invoice-finance-dialog/invoice-finance-dialog.component';
import { InvoiceNewDialogComponent } from './invoice-new-dialog/invoice-new-dialog.component';
import { InvoiceOptionsDialogComponent } from './invoice-options-dialog/invoice-options-dialog.component';
import { InvoiceUploadComponent } from './invoice-upload/invoice-upload.component';
import { AppRoutingModule } from '../app-routing.module';
import { UploadTableComponent } from './invoice-upload/upload.table.component';
import { TediDropZoneDirective } from '../directives/drop-zone.directive';
import { TediRippleDirective } from '../directives/tedi-ripple.directive';

import { NgxExtendedPdfViewerModule } from 'ngx-extended-pdf-viewer';
import { SpeechModule } from 'ngx-speech';

import { TediInvoiceInitValueDirective } from '../directives/tedi-invoice-init-value.directive';

@NgModule({
  imports: [
    NgxExtendedPdfViewerModule,
    SpeechModule,
    CommonModule,
    MaterialModule,
    FormsModule,
    ReactiveFormsModule,
    InfiniteScrollModule,
    AppRoutingModule,
  ],
  declarations: [
    InvoiceComponent,
    InvoiceToolbarComponent,
    InvoiceTableComponent,
    InvoiceMobileListComponent,
    InvoiceSheetComponent,
    CreateInvoiceDialogComponent,
    InvoiceDetailDialogComponent,
    InvoiceFinanceDialogComponent,
    InvoiceNewDialogComponent,
    InvoiceOptionsDialogComponent,
    InvoiceUploadComponent,
    UploadTableComponent,
    TediDropZoneDirective,
    TediRippleDirective,
    TediInvoiceInitValueDirective
  ],
  entryComponents: [
    CreateInvoiceDialogComponent,
    InvoiceDetailDialogComponent,
    InvoiceFinanceDialogComponent,
    InvoiceNewDialogComponent,
    InvoiceOptionsDialogComponent
  ],
  providers: [
    InvoiceService,
    { provide: 'SPEECH_LANG', useValue: 'es-ES' }
  ],
  schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class InvoiceModule { }
