import { Injectable } from '@angular/core';
import { EmployeeFactory, ICollection, IEmployee, IFilter } from 'libraries/AonSDK/aon';
import { ErrorService } from './error.service';
import { CommonService } from './common.service';

@Injectable({
  providedIn: 'root'
})
export class EmployeeService extends CommonService {

  private singleObjectCrud = new EmployeeFactory().createSingleObjectCrud();
  private multipleObjectCrud = new EmployeeFactory().createMultipleObjectCrud();

  constructor(private errorService: ErrorService) {
    super();
  }

  async getEmployeeList(filter?: IFilter): Promise<ICollection<IEmployee>> {
    return (await this.multipleObjectCrud.getCollection(filter)).result;
  }

  async updateEmployee(employee: IEmployee): Promise<IEmployee> {
    return (await this.singleObjectCrud.updateElement(employee)).result;
  }

  async createEmployee(employee: IEmployee): Promise<IEmployee> {
    return (await this.singleObjectCrud.createElement(employee)).result;
  }

  async deleteEmployee(pkey: any): Promise<boolean> {
    return (await this.singleObjectCrud.deleteElement(pkey)).result;
  }

  async toggleEmployeeStatus(employeeId: string):Promise<IEmployee> {
    const employeeToUpdate: IEmployee = (await this.singleObjectCrud.getElement(employeeId)).result;

    if (employeeToUpdate) {
      employeeToUpdate.Active = !employeeToUpdate.Active;

      return this.updateEmployee(employeeToUpdate);
    }

    // Error al tratar de acceder al elemento
    const errorEmployee = this.errorService.getError('0206');
    throw errorEmployee;

  }
}
