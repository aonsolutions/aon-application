import { MarkFactory } from "../factorys/MarkFactory";
import { IMark } from "../interfaces/modelsInterfaces";
import { ICollection, IFilter, IResponse } from "../interfaces/utilitiesInterfaces";

export class MarkFunctions {
    private static singleObjectCrud = new MarkFactory().createSingleObjectCrud();
    private static multipleObjectCrud = new MarkFactory().createMultipleObjectCrud();

    static async getMarkList(filter?: IFilter): Promise<IResponse<ICollection<IMark>>> {
        return (await this.multipleObjectCrud.getCollection(filter));
    }

    static async getMark(pkey: any): Promise<IResponse<IMark>> {
        return (await this.singleObjectCrud.getElement(pkey));
    }

    static async createMark(mark: IMark): Promise<IResponse<IMark>> {
        return (await this.singleObjectCrud.createElement(mark));
    }

    static async updateMark(mark: IMark): Promise<IResponse<IMark>> {
        return (await this.singleObjectCrud.updateElement(mark));
    }

    static async deleteMark(pkey: any): Promise<IResponse<boolean>> {
        return (await this.singleObjectCrud.deleteElement(pkey));
    }
}