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

@NgModule({
  declarations: [
    BillingComponent,
    ModelsDashboardComponent,
    ChartDashboardComponent,
    BanksDashboardComponent,
  ],
  imports: [
    CommonModule,
    FormsModule,
    BillingRoutingModule,
    SetMaterialModule,
    SharedModule,
  ]
})
export class BillingModule { }
