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
// import { TableTasksComponent } from './components/table-tasks/table-tasks.component';
import { ModalCreateComponent } from './components/modal-create/modal-create.component';
import { MatDialogModule } from '@angular/material/dialog';
import { FormsModule } from '@angular/forms';
import { TablesInboxComponent } from './components/tables-inbox/table-inbox.component';

@NgModule({
  declarations: [
    InboxviewComponent,
    TableComponentComponent,
    ModalCreateComponent,
    TablesInboxComponent,
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
    MatDialogModule,
    FormsModule
  ],
})

export class InboxModule { }
