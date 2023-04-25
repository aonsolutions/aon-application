import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { InboxRoutingModule } from './inbox-routing.module';
import { SetMaterialModule } from 'libraries/setproduct-angular-material';
import { InboxComponent } from './inbox.component';
import { MatSidenavModule } from '@angular/material/sidenav';
import { ContentInboxComponent } from './components/content-inbox/content-inbox.component';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatTabsModule } from '@angular/material/tabs';
import { MessageInboxComponent } from './components/message-inbox/message-inbox.component';
import { MatGridListModule } from '@angular/material/grid-list';
import { MatMenuModule } from '@angular/material/menu';
import { DetailNotificationTaskComponent } from './components/detail-notification-task/detail-notification-task.component';
import { MatDividerModule } from '@angular/material/divider';
import { ScrollingModule } from '@angular/cdk/scrolling';
import { DetailConsultComponent } from './components/detail-consult/detail-consult.component';
import { RateInboxComponent } from './components/rate-inbox/rate-inbox.component';
import { MatDialogModule } from '@angular/material/dialog';
import { ModalCreateConsultComponent } from './components/modal-create-consult/modal-create-consult.component';
import { MatSelectModule } from '@angular/material/select';
import { FormsModule } from '@angular/forms';
import { SharedModule } from 'src/app/shared/shared.module';


@NgModule({
  declarations: [
    InboxComponent,
    ContentInboxComponent,
    MessageInboxComponent,
    DetailNotificationTaskComponent,
    DetailConsultComponent,
    RateInboxComponent,
    ModalCreateConsultComponent,
  ],
  imports: [
    CommonModule,
    InboxRoutingModule,
    MatSidenavModule,
    MatButtonModule,
    MatIconModule,
    SetMaterialModule,
    MatFormFieldModule,
    MatInputModule,
    MatTabsModule,
    MatGridListModule,
    MatMenuModule,
    MatDividerModule,
    ScrollingModule,
    MatDialogModule,
    MatSelectModule,
    FormsModule,
    SharedModule
  ],
})
export class InboxModule {}
