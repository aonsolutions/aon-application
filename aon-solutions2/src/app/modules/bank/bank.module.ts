import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { BankComponent } from './pages/bank.component';
import { FormsModule } from '@angular/forms';
import { SetMaterialModule } from 'libraries/setproduct-angular-material';
import { SharedModule } from 'src/app/shared/shared.module';
import { BankRoutingModule } from './bank-routing.module';
import { MatGridListModule } from '@angular/material/grid-list';
import { ChartDashboardComponent } from './components/chart-dashboard/chart-dashboard.component';
import { SalesTableComponent } from './components/sales-table/sales-table.component';
import { BillsTableComponent } from './components/bills-table/bills-table.component';
import { BankTableComponent } from './components/bank-table/bank-table.component';


@NgModule({
  declarations: [
    BankComponent,
    ChartDashboardComponent,
    SalesTableComponent,
    BillsTableComponent,
    BankTableComponent,
  ],
  imports: [
    CommonModule,
    FormsModule,
    BankRoutingModule,
    SetMaterialModule,
    SharedModule,
    MatGridListModule,
  ]
})
export class BankModule { }
