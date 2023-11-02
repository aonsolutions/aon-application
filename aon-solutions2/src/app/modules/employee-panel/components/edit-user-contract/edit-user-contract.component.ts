import {
  Component,
  EventEmitter,
  Input,
  OnInit,
  Output,
  ViewChild,
} from '@angular/core';
import { IContract, IEmployee } from 'libraries/AonSDK/src/aon';
import { EmployeeService } from 'src/app/core/services/employee.service';
import { EmployeeComponent } from '../../pages/employee.component';

@Component({
  selector: 'app-edit-user-contract',
  templateUrl: './edit-user-contract.component.html',
  styleUrls: ['./edit-user-contract.component.scss'],
})
export class EditUserContractComponent implements OnInit {
  @Output() changeData: EventEmitter<any> = new EventEmitter<any>();
  @Output() changeTabIndex: EventEmitter<number> = new EventEmitter<number>();
  @Input() empleadoDetail: IEmployee | undefined;
  @Input() selectedContract: IContract | undefined;
  // @ViewChild(EmployeeComponent, { static: false })
  // employeeComponent!: EmployeeComponent;

  // @ViewChild(EmployeeComponent) employeeComponent!: EmployeeComponent;

  // Variable para almacenar el tipo de contrato seleccionado

// Agrega un objeto para almacenar los apellidos separados
splitLastname: { surname1: string, surname2: string } = { surname1: '', surname2: '' };



  selectedContractType: string | undefined;

  constructor(private employeeService: EmployeeService) {}

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

  getValueEmployee(newValue: any, empleadoDetail: any, propertyName: string) {
    // Actualizar el valor correspondiente en el objeto empleadoDetail
    empleadoDetail[propertyName] = newValue;
  }

  async onSave() {
    try {
      this.empleadoDetail &&
        (await this.employeeService.updateEmployee(this.empleadoDetail));
      console.log('Empleado actualizado:', this.empleadoDetail);


      this.changeData.emit(this.empleadoDetail); // Emite un evento con los datos actualizados

    } catch (error) {
      console.error('Error al actualizar el empleado:', error);
    }
  }

  onCancel() {}
}
