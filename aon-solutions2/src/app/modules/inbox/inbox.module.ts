import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { InboxRoutingModule } from './inbox-routing.module';
import { InboxviewComponent } from './pages/inboxview.component';
import { SharedModule } from "../../shared/shared.module";

import { SetMaterialModule } from 'libraries/setproduct-angular-material';
import { MatMenuModule } from '@angular/material/menu';
import { MatIconModule } from '@angular/material/icon';
import { MatGridListModule } from '@angular/material/grid-list';
import { MatTableModule } from '@angular/material/table';
import { TableComponentComponent } from './components/table-component/table-component.component';
import { TableQueriesComponent } from './components/table-queries/table-queries.component';
import { TableTasksComponent } from './components/table-tasks/table-tasks.component';
import { TableNotificationsComponent } from './components/table-notifications/table-notifications.component';
import { ModalCreateComponent } from './components/modal-create/modal-create.component';
import { MatDialogModule } from '@angular/material/dialog';
@NgModule({
  declarations: [
    InboxviewComponent,
    TableComponentComponent,
    TableQueriesComponent,
    TableTasksComponent,
    TableNotificationsComponent,
    ModalCreateComponent,
  ],
  imports: [
    CommonModule,
    InboxRoutingModule,
    SharedModule,
    SetMaterialModule,
    MatMenuModule,
    MatIconModule,
    MatGridListModule,
    MatTableModule,
    MatDialogModule
  ],
})

export class InboxModule { }
