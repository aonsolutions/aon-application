import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SetMaterialModule } from 'libraries/setproduct-angular-material';
import { MatIconModule } from '@angular/material/icon';
import { MatDividerModule } from '@angular/material/divider';

import { HomeRoutingModule } from './home-routing.module';
import { HomeComponent } from './pages/home.component';
import { SharedModule } from 'src/app/shared/shared.module';
import { MatButtonModule } from '@angular/material/button';
import { MatMenuModule } from '@angular/material/menu';
import { MatGridListModule } from '@angular/material/grid-list';
import { FormsModule } from '@angular/forms';
import { ChartDashboardComponent } from './components/chart-dashboard/chart-dashboard.component';
import { ModelsDashboardComponent } from './components/models-dashboard/models-dashboard.component';
import { BanksDashboardComponent } from './components/banks-dashboard/banks-dashboard.component';
import { MenuButtonDashboardComponent } from './components/menu-button-dashboard/menu-button-dashboard.component';
import { InboxDashboardComponent } from './components/inbox-dashboard/inbox-dashboard.component';
import { ListDashboardComponent } from './components/list-dashboard/list-dashboard.component';


@NgModule({
  declarations: [
    HomeComponent,
    ChartDashboardComponent,
    ModelsDashboardComponent,
    BanksDashboardComponent,
    MenuButtonDashboardComponent,
    InboxDashboardComponent,
    ListDashboardComponent
  ],
  imports: [
    CommonModule,
    HomeRoutingModule,
    SharedModule,
    MatButtonModule,
    MatMenuModule,
    MatGridListModule,
    FormsModule,
    SetMaterialModule,
    MatIconModule,
    MatDividerModule


  ]
})
export class HomeModule {


 }
