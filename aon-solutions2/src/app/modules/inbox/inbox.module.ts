import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { InboxRoutingModule } from './inbox-routing.module';
import { InboxviewComponent } from './pages/inboxview/inboxview.component';
import { SharedModule } from "../../shared/shared.module";
import { TableComponentComponent } from './components/inbox/table-component/table-component.component';
import { ModalCreateComponent } from './components/inbox/modal-create/modal-create.component';


@NgModule({
    declarations: [
        InboxviewComponent,
        TableComponentComponent,
        ModalCreateComponent,
    ],
    imports: [
        CommonModule,
        InboxRoutingModule,
        SharedModule,
    ]
})
export class InboxModule { }
