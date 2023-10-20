import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { EmployeePanelRoutingModule } from './employee-panel-routing.module';
import { EmployeeComponent } from './pages/employee.component';
import { SharedModule } from 'src/app/shared/shared.module';
import { CreateNewContractComponent } from './components/create-new-contract/create-new-contract.component';


@NgModule({
  declarations: [
    EmployeeComponent,
    CreateNewContractComponent
  ],
  imports: [
    CommonModule,
    EmployeePanelRoutingModule,
    SharedModule
  ]
})
export class EmployeePanelModule { }
