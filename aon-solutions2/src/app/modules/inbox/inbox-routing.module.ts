import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { InboxviewComponent } from './pages/inboxview/inboxview.component';

const routes: Routes = [
  {
    path: '',
    component: InboxviewComponent
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class InboxRoutingModule { }
