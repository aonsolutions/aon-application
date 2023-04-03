import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { EmployeePanelComponent } from './employee-panel.component';

const routes: Routes = [{ path: '', component: EmployeePanelComponent }];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class EmployeePanelRoutingModule { }
