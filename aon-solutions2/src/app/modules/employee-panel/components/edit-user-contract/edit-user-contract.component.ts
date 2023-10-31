import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import {  IContract, IEmployee } from 'libraries/AonSDK/src/aon';
import { EmployeeService } from 'src/app/core/services/employee.service';


@Component({
  selector: 'app-edit-user-contract',
  templateUrl: './edit-user-contract.component.html',
  styleUrls: ['./edit-user-contract.component.scss']
})
export class EditUserContractComponent implements OnInit {
  @Output() changeTabIndex: EventEmitter<number> = new EventEmitter<number>();
  @Input() empleadoDetail: IEmployee | undefined;
  @Input() selectedContract: IContract | undefined;

   // Variable para almacenar el tipo de contrato seleccionado
  selectedContractType: string | undefined;



  // Agrega un objeto para almacenar los apellidos separados
  splitLastname: { surname1: string, surname2: string } = { surname1: '', surname2: '' };


  constructor(
    private employeeService: EmployeeService
     ) {}

  ngOnInit() {

   // Divide el campo Lastname en apellidos separados
   if (this.empleadoDetail && this.empleadoDetail.Lastname) {
    const surname = this.empleadoDetail.Lastname.split(' ');
    this.splitLastname.surname1 = surname[0] || '';
    this.splitLastname.surname2 = surname[1] || '';
  }

  }


  goBack() {
    const tabIndex = 0;
    this.changeTabIndex.emit(tabIndex);
    console.log(tabIndex);
  }

  getValue(newValue: any) {
    // Actualiza el valor correspondiente en el objeto user
    this.empleadoDetail = newValue;
     // Emite el evento con el valor actualizado

  }

  // getValue(newValue: any, enterprise: any, propertyName: string) {
  //   enterprise[propertyName] = newValue;
  // }

  // async onSave() {
  //   if (this.empleadoDetail) {
  //     // Llama a la función updateEmployee del servicio employeeService para guardar los cambios
  //     await this.employeeService.updateEmployee(this.empleadoDetail);
  //   }
  // }



  async onSave() {
    if (this.empleadoDetail) {
      try {
        // Call the service to update the employee details
        const updatedEmployee = await this.employeeService.updateEmployee(this.empleadoDetail);
        console.log('Employee updated:', updatedEmployee);
        // Optionally, you can emit an event or perform other actions on success.
      } catch (error) {
        console.error('Error updating employee:', error);
        // Handle the error as needed (e.g., display an error message).
      }
    }
  }


onCancel() {

}



}
