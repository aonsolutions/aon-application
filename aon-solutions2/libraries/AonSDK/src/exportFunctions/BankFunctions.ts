import { BankFactory } from "../factorys/BankFactory";
import { IBank } from "../interfaces/modelsInterfaces";
import { ICollection, IFilter, IResponse } from "../interfaces/utilitiesInterfaces";

export class BankFunctions {
    bankCollectionCrud = new BankFactory().createMultipleObjectCrud();
    bankCrud = new BankFactory().createSingleObjectCrud();

    async getBankList(filter?: IFilter): Promise<IResponse<ICollection<IBank>>> {
        return (await this.bankCollectionCrud.getCollection(filter));
    }

    async createBank(bank: IBank): Promise<IResponse<IBank>> {
        return (await this.bankCrud.createElement(bank));
    }

    async updateBank(bank: IBank): Promise<IResponse<IBank>> {
        return (await this.bankCrud.updateElement(bank));
    }

    async deleteBank(pkey: any): Promise<IResponse<boolean>> {
        return (await this.bankCrud.deleteElement(pkey));
    }

    async getBank(pkey: any): Promise<IResponse<IBank>> {
        return (await this.bankCrud.getElement(pkey));
    }
}