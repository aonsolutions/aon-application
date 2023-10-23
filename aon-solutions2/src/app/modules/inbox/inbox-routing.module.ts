import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { InboxviewComponent } from './pages/inboxview.component';

const routes: Routes = [
  {
    path: '',
    component: InboxviewComponent
  },
  {
    path: ':id',
    component: InboxviewComponent
  },
  {
    path: 'create',
    component: InboxviewComponent
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class InboxRoutingModule { }
