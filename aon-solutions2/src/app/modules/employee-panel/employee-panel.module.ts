import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { EmployeePanelRoutingModule } from './employee-panel-routing.module';
import { EmployeeComponent } from './pages/employee.component';
import { SharedModule } from 'src/app/shared/shared.module';
import { ModalCreateContractComponent } from './components/modal-create-contract/modal-create-contract.component';


@NgModule({
  declarations: [
    EmployeeComponent,
    ModalCreateContractComponent
  ],
  imports: [
    CommonModule,
    EmployeePanelRoutingModule,
    SharedModule
  ]
})
export class EmployeePanelModule { }
