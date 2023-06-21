import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { InboxRoutingModule } from './inbox-routing.module';
import { InboxComponent } from './components/inbox/inbox.component';
import { ChartComponent } from 'src/app/shared/components/chart/chart.component';
import { ChartsModule } from 'ng2-charts';


@NgModule({
  declarations: [
    InboxComponent,
    ChartComponent
  ],
  imports: [
    CommonModule,
    InboxRoutingModule,
    ChartsModule
  ]
})
export class InboxModule { }
