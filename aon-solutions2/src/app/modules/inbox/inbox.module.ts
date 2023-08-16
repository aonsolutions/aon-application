import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { InboxRoutingModule } from './inbox-routing.module';
import { InboxviewComponent } from './pages/inboxview/inboxview.component';
import { SharedModule } from "../../shared/shared.module";
import { TableComponentComponent } from './components/inbox/table-component/table-component.component';

import { SetMaterialModule } from 'libraries/setproduct-angular-material';
import { MatMenuModule } from '@angular/material/menu';
import { MatIconModule } from '@angular/material/icon';
import { MatGridListModule } from '@angular/material/grid-list';
import { MatTableModule } from '@angular/material/table';
import { TableQueriesComponent } from './components/inbox/table-queries/table-queries.component';
import { TableTasksComponent } from './components/inbox/table-tasks/table-tasks.component';
import { TableNotificationsComponent } from './components/inbox/table-notifications/table-notifications.component';
import { CreateQueryComponent } from './components/inbox/create-query/create-query.component';


@NgModule({
    declarations: [
        InboxviewComponent,
        TableComponentComponent,
        TableQueriesComponent,
        TableTasksComponent,
        TableNotificationsComponent,
        CreateQueryComponent,
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
    ],
})
export class InboxModule { }
