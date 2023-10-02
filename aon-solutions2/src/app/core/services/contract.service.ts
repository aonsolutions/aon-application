import { Injectable } from '@angular/core';
import { ContractFactory, ICollection, IContract, IFilter } from 'libraries/AonSDK/src/aon';
import { ErrorService } from './error.service';
import { CommonService } from './common.service';

@Injectable({
  providedIn: 'root'
})
export class ContractService extends CommonService {

  private singleObjectCrud = new ContractFactory().createSingleObjectCrud();
  private multipleObjectCrud = new ContractFactory().createMultipleObjectCrud();

  constructor(private errorService: ErrorService) {
    super();
  }

  async getContractList(filter?: IFilter): Promise<ICollection<IContract>> {
    return (await this.multipleObjectCrud.getCollection(filter)).result;
  }

  async getContract(pkey: any): Promise<IContract> {
    return (await this.singleObjectCrud.getElement(pkey)).result;
  }

  async createContract(contract: IContract): Promise<IContract> {
    return (await this.singleObjectCrud.createElement(contract)).result;
  }

  async updateContract(contract: IContract): Promise<IContract> {
    return (await this.singleObjectCrud.updateElement(contract)).result;
  }

  async deleteContract(pkey: any): Promise<boolean> {
    return (await this.singleObjectCrud.deleteElement(pkey)).result;
  }

  async extendContract(contractId: string, newEndDate: Date): Promise<IContract> {
    const contract: IContract = (await this.singleObjectCrud.getElement(contractId)).result;

    if (contract) {
      contract.EndDate = newEndDate;
      this.updateContract(contract);
    }

    // Error al tratar de acceder al elemento
    const errorEmployee = this.errorService.getError('0206');
    throw errorEmployee;
  }

  async changeContractType(contractId: string, newType: string): Promise<IContract> {
    const contract: IContract = (await this.singleObjectCrud.getElement(contractId)).result;

    if (contract) {
      contract.Type = newType;
      this.updateContract(contract);
    }

    // Error al tratar de acceder al elemento
    const errorEmployee = this.errorService.getError('0206');
    throw errorEmployee;
  }

}
