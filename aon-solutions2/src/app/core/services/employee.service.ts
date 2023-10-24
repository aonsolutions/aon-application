import { Injectable } from '@angular/core';

import { CommonService } from './common.service';
import { EmployeeFactory, ICollection, IContract, IEmployee, IFilter } from 'libraries/AonSDK/src/aon';
import { ErrorService } from './error.service';

@Injectable({
  providedIn: 'root'
})
export class EmployeeService extends CommonService {

  private singleObjectCrud = new EmployeeFactory().createSingleObjectCrud();
  private employeeCollectionCrud = new EmployeeFactory().createMultipleObjectCrud();

  constructor(private errorService: ErrorService) {
    super();
  }

    /**
     * Obtiene una lista de empleados.
     *
     * @param {IFilter} filter - filtro opcional para aplicar a la lista de empleados
     * @return {Promise<ICollection<IEmployee>>} una promesa que se resuelve a una colección de empleados
     */
  async getEmployeeList(filter?: IFilter): Promise<ICollection<IEmployee>> {
    // return (await this.multipleObjectCrud.getCollection(filter)).result;
    return (await this.employeeCollectionCrud.getCollection(filter)).result;
  }

  /**
   * Obtiene un empleado utilizando la clave primaria proporcionada.
   *
   * @param {any} pkey - La clave primaria del empleado (documento).
   * @return {Promise<IEmployee>} Una promesa que se resuelve con el objeto del empleado.
   */
  async getEmployee(pkey: any): Promise<IEmployee> {
    return (await this.singleObjectCrud.getElement(pkey)).result;
  }

  /**
   * Actualiza un empleado
   *
   * @param {IEmployee} employee - El objeto de empleado a actualizar.
   * @return {Promise<IEmployee>} El objeto de empleado actualizado.
   */
  async updateEmployee(employee: IEmployee): Promise<IEmployee> {
    return (await this.singleObjectCrud.updateElement(employee)).result;
  }

  /**
  * Crea un nuevo empleado.
  *
  * @param {IEmployee} employee - El empleado que se va a crear.
  * @return {Promise<IEmployee>} - El empleado creado.
  */
  async createEmployee(employee: IEmployee): Promise<IEmployee> {
    return (await this.singleObjectCrud.createElement(employee)).result;
  }

  /**
  * Elimina un empleado.
  *
  * @param {any} pkey - la clave primaria del empleado a eliminar
  * @return {Promise<boolean>} una promesa que se resuelve a true si el empleado se elimina correctamente, de lo contrario false
  */
  async deleteEmployee(pkey: any): Promise<boolean> {
    return (await this.singleObjectCrud.deleteElement(pkey)).result;
  }
}
