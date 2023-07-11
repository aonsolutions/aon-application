import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { TaxPanelComponent } from './page/taxPanel.component';

const routes: Routes = [
  {
    path: '',
    component: TaxPanelComponent
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class TaxPanelRoutingModule { }
