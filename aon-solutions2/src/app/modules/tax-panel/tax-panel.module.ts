import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { TaxPanelComponent } from './page/taxPanel.component';
import { TaxPanelRoutingModule } from './tax-panel-routing.module';
import { SharedModule } from 'src/app/shared/shared.module';

import { MatMenuModule } from '@angular/material/menu';
import { MatGridListModule } from '@angular/material/grid-list';
import { FormsModule } from '@angular/forms';
import { SetMaterialModule } from 'libraries/setproduct-angular-material';
import { MatIconModule } from '@angular/material/icon';
import { MatDividerModule } from '@angular/material/divider';
import { MatTab, MatTabsModule } from '@angular/material/tabs';
import { TableTaxModelComponent } from './components/table-tax-model/table-tax-model.component';
import { MatTableModule } from '@angular/material/table';
import { MatSortModule } from '@angular/material/sort';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatFormFieldModule } from '@angular/material/form-field';
import { ModalEditTaxModelComponent } from './components/modal-edit-tax-model/modal-edit-tax-model.component';
import { MatDialogModule } from '@angular/material/dialog';
import { ModalPaymentComponent } from './components/modal-payment/modal-payment.component';
import { MatRadioModule } from '@angular/material/radio';
import { ModalTaxesDetailsComponent } from './components/modal-taxes-details/modal-taxes-details.component';


@NgModule({
  declarations: [
    TaxPanelComponent,
    TableTaxModelComponent,
    ModalEditTaxModelComponent,
    ModalPaymentComponent,
    ModalTaxesDetailsComponent
  ],

  imports: [
    CommonModule,
    TaxPanelRoutingModule,
    SharedModule,


    MatMenuModule,
    MatGridListModule,
    FormsModule,
    SetMaterialModule,
    MatIconModule,
    MatDividerModule,
    MatTabsModule,
    MatTableModule,
    MatSortModule,
    MatPaginatorModule,
    MatFormFieldModule,
    MatDialogModule,
    MatRadioModule

  ]
})
export class TaxPanelModule { }
