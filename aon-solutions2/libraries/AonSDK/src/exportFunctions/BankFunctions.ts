import { BankFactory } from "../factorys/BankFactory";
import { IBank } from "../interfaces/modelsInterfaces";
import { ICollection, IFilter, IResponse } from "../interfaces/utilitiesInterfaces";

export class BankFunctions {
    private static bankCollectionCrud = new BankFactory().createMultipleObjectCrud();
    private static bankCrud = new BankFactory().createSingleObjectCrud();

    static async getBankList(filter?: IFilter): Promise<IResponse<ICollection<IBank>>> {
        return (await this.bankCollectionCrud.getCollection(filter));
    }

    static async createBank(bank: IBank): Promise<IResponse<IBank>> {
        return (await this.bankCrud.createElement(bank));
    }

    static async updateBank(bank: IBank): Promise<IResponse<IBank>> {
        return (await this.bankCrud.updateElement(bank));
    }

    static async deleteBank(pkey: any): Promise<IResponse<boolean>> {
        return (await this.bankCrud.deleteElement(pkey));
    }

    static async getBank(pkey: any): Promise<IResponse<IBank>> {
        return (await this.bankCrud.getElement(pkey));
    }
}