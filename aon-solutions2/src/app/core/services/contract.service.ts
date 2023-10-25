import { Injectable } from '@angular/core';

import { CommonService } from './common.service';
import { ContractFactory, ICollection, IContract, IFilter } from 'libraries/AonSDK/src/aon';
import { ErrorService } from './error.service';

@Injectable({
  providedIn: 'root'
})
export class ContractService extends CommonService {

  private singleObjectCrud = new ContractFactory().createSingleObjectCrud();
  private contractCollectionCrud = new ContractFactory().createMultipleObjectCrud();

  constructor(private errorService: ErrorService) {
    super();
  }

  /**
   * Recupera una lista de contratos
   *
   * @param {IFilter} filter - Filtro opcional para aplicar a la lista de contratos.
   * @return {Promise<ICollection<IContract>>} - Una promesa que se resuelve a una colección de contratos.
   */
  async getContractList(filter?: IFilter): Promise<ICollection<IContract>> {
    return (await this.contractCollectionCrud.getCollection(filter)).result;
  }

  /**
  * Recupera un contrato basado en la clave primaria proporcionada.
  *
  * @param {any} pkey - La clave primaria utilizada para identificar el contrato.
  * @return {Promise<IContract>} Una promesa que se resuelve con el contrato recuperado.
  */
  async getContract(pkey: any): Promise<IContract> {
    return (await this.singleObjectCrud.getElement(pkey)).result;
  }

  /**
   * Crea un nuevo contrato
   *
   * @param {IContract} contract - El objeto de contrato a crear
   * @return {Promise<IContract>} - El objeto de contrato creado
   */
  async createContract(contract: IContract): Promise<IContract> {
    return (await this.singleObjectCrud.createElement(contract)).result;
  }

  /**
   * Actualiza un contrato
   *
   * @param {IContract} contract - El objeto de contrato a actualizar
   * @return {Promise<IContract>} - El objeto de contrato actualizado
   *
   */
  async updateContract(contract: IContract): Promise<IContract> {
    return (await this.singleObjectCrud.updateElement(contract)).result;
  }

  /**
   * Elimina un contrato
   *
   * @param {any} pkey - La clave primaria del contrato a eliminar
   * @return {Promise<boolean>} Una promesa que se resuelve a true si el contrato se elimina correctamente, de lo contrario false
   */
  async deleteContract(pkey: any): Promise<boolean> {
    return (await this.singleObjectCrud.deleteElement(pkey)).result;
  }
}
