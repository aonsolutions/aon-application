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
import { TabsTaxModelComponent } from './components/tabs-tax-model/tabs-tax-model.component';
import { MatTab, MatTabsModule } from '@angular/material/tabs';


@NgModule({
  declarations: [
    TaxPanelComponent,
    TabsTaxModelComponent
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
    MatTabsModule

  ]
})
export class TaxPanelModule { }
