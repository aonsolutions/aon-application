import { FolderFactory } from "../factorys/FolderFactory";
import { IFolder } from "../interfaces/modelsInterfaces";
import { ICollection, IFilter, IResponse } from "../interfaces/utilitiesInterfaces";

export class FolderFunctions {
    private static singleObjectCrud = new FolderFactory().createSingleObjectCrud();
    private static multipleObjectCrud = new FolderFactory().createMultipleObjectCrud();

    static async getFolderList(filter?: IFilter): Promise<IResponse<ICollection<IFolder>>> {
        return (await this.multipleObjectCrud.getCollection(filter));
    }

    static async getFolder(pkey: any): Promise<IResponse<IFolder>> {
        return (await this.singleObjectCrud.getElement(pkey));
    }

    static async createFolder(folder: IFolder): Promise<IResponse<IFolder>> {
        return (await this.singleObjectCrud.createElement(folder));
    }

    static async deleteFolder(pkey: any): Promise<IResponse<boolean>> {
        return (await this.singleObjectCrud.deleteElement(pkey));
    }
}