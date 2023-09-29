import { MarkFactory } from "../factorys/MarkFactory";
import { IMark } from "../interfaces/modelsInterfaces";
import { ICollection, IFilter, IResponse } from "../interfaces/utilitiesInterfaces";

export class MarkFunctions {
    private singleObjectCrud = new MarkFactory().createSingleObjectCrud();
    private multipleObjectCrud = new MarkFactory().createMultipleObjectCrud();

    async getMarkList(filter?: IFilter): Promise<IResponse<ICollection<IMark>>> {
        return (await this.multipleObjectCrud.getCollection(filter));
    }

    async getMark(pkey: any): Promise<IResponse<IMark>> {
        return (await this.singleObjectCrud.getElement(pkey));
    }

    async createMark(mark: IMark): Promise<IResponse<IMark>> {
        return (await this.singleObjectCrud.createElement(mark));
    }

    async updateMark(mark: IMark): Promise<IResponse<IMark>> {
        return (await this.singleObjectCrud.updateElement(mark));
    }

    async deleteMark(pkey: any): Promise<IResponse<boolean>> {
        return (await this.singleObjectCrud.deleteElement(pkey));
    }
}