import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { HomeRoutingModule } from './home-routing.module';
import { HomeComponent } from './home.component';
import { SetMaterialModule } from 'libraries/setproduct-angular-material';
import { MatGridListModule } from '@angular/material/grid-list';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatMenuModule } from '@angular/material/menu';
import { MatButtonModule } from '@angular/material/button';
import { UploadDashboardComponent } from './components/upload-dashboard/upload-dashboard.component';
import { ShortcutDashboardComponent } from './components/shortcut-dashboard/shortcut-dashboard.component';
import { MenuButtonDashboardComponent } from './components/menu-button-dashboard/menu-button-dashboard.component';
import { MatDividerModule } from '@angular/material/divider';
import { ModelsDashboardComponent } from './components/models-dashboard/models-dashboard.component';
import { BanksDashboardComponent } from './components/banks-dashboard/banks-dashboard.component';
import { ChartDashboardComponent } from './components/chart-dashboard/chart-dashboard.component';
import { InboxDashboardComponent } from './components/inbox-dashboard/inbox-dashboard.component';
import { ListDashboardComponent } from './components/list-dashboard/list-dashboard.component';

@NgModule({
  declarations: [
    HomeComponent,
    UploadDashboardComponent,
    ShortcutDashboardComponent,
    MenuButtonDashboardComponent,
    ModelsDashboardComponent,
    BanksDashboardComponent,
    ChartDashboardComponent,
    InboxDashboardComponent,
    ListDashboardComponent
  ],
  imports: [
    CommonModule,
    HomeRoutingModule,
    SetMaterialModule,
    MatGridListModule,
    MatCardModule,
    MatIconModule,
    MatMenuModule,
    MatButtonModule,
    MatDividerModule
  ]
})
export class HomeModule { }
