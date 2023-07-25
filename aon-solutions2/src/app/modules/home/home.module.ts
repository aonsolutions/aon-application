import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatDividerModule } from '@angular/material/divider';
import { MatGridListModule } from '@angular/material/grid-list';
import { MatIconModule } from '@angular/material/icon';
import { MatMenuModule } from '@angular/material/menu';
import { NgModule } from '@angular/core';

import { BanksDashboardComponent } from './components/banks-dashboard/banks-dashboard.component';
import { ChartDashboardComponent } from './components/chart-dashboard/chart-dashboard.component';
import { HomeComponent } from './pages/home.component';
import { HomeRoutingModule } from './home-routing.module';
import { InboxDashboardComponent } from './components/inbox-dashboard/inbox-dashboard.component';
import { ListDashboardComponent } from './components/list-dashboard/list-dashboard.component';
import { MenuButtonDashboardComponent } from './components/menu-button-dashboard/menu-button-dashboard.component';
import { ModelsDashboardComponent } from './components/models-dashboard/models-dashboard.component';
import { SetMaterialModule } from 'libraries/setproduct-angular-material';
import { SharedModule } from 'src/app/shared/shared.module';


@NgModule({
  declarations: [
    BanksDashboardComponent,
    ChartDashboardComponent,
    HomeComponent,
    InboxDashboardComponent,
    ListDashboardComponent,
    MenuButtonDashboardComponent,
    ModelsDashboardComponent
  ],
  imports: [
    CommonModule,
    FormsModule,
    HomeRoutingModule,
    MatButtonModule,
    MatDividerModule,
    MatGridListModule,
    MatIconModule,
    MatMenuModule,
    SetMaterialModule,
    SharedModule
  ]
})
export class HomeModule {


 }
