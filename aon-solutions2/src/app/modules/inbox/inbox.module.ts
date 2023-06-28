import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { InboxRoutingModule } from './inbox-routing.module';
import { InboxComponent } from './components/inbox/inbox.component';
import { SharedModule } from 'src/app/shared/shared.module';


@NgModule({
  declarations: [
    InboxComponent,
  ],
  imports: [
    CommonModule,
    InboxRoutingModule,
    SharedModule
  ]
})
export class InboxModule { }
