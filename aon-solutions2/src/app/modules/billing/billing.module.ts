import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';

import { BillingComponent } from './pages/billing.component';
import { BillingRoutingModule } from './billing-routing.module';
import { SetMaterialModule } from 'libraries/setproduct-angular-material';
import { SharedModule } from 'src/app/shared/shared.module';
import { FormsModule } from '@angular/forms';
import { ModelsDashboardComponent } from './components/models-dashboard/models-dashboard.component';
import { ChartDashboardComponent } from './components/chart-dashboard/chart-dashboard.component';
import { BanksDashboardComponent } from './components/banks-dashboard/banks-dashboard.component';
import { SalesTableComponent } from './components/sales-table/sales-table.component';
import { BillsTableComponent } from './components/bills-table/bills-table.component';
import { BankTableComponent } from './components/bank-table/bank-table.component';
import { ChartDashboardReportsComponent } from './components/chart-dashboard-reports/chart-dashboard-reports.component';
import { DuplicateFacturaComponent } from './components/modal-duplicate-factura/duplicate-factura.component';
import { SendFacturaComponent } from './components/modal-send-factura/send-factura.component';
import { DeleteFacturaComponent } from './components/modal-delete-factura/delete-factura.component';
import { MatIconModule } from '@angular/material/icon';

@NgModule({
  declarations: [
    BillingComponent,
    ModelsDashboardComponent,
    ChartDashboardComponent,
    BanksDashboardComponent,
    SalesTableComponent,
    BillsTableComponent,
    BankTableComponent,
    ChartDashboardReportsComponent,
    DuplicateFacturaComponent,
    SendFacturaComponent,
    DeleteFacturaComponent
  ],
  imports: [
    CommonModule,
    FormsModule,
    BillingRoutingModule,
    SetMaterialModule,
    SharedModule,
    MatIconModule
  ]
})
export class BillingModule { }
