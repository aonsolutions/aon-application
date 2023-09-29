import { FolderFactory } from "../factorys/FolderFactory";
import { IFolder } from "../interfaces/modelsInterfaces";
import { ICollection, IFilter, IResponse } from "../interfaces/utilitiesInterfaces";

export class FolderFunctions {
    private singleObjectCrud = new FolderFactory().createSingleObjectCrud();
    private multipleObjectCrud = new FolderFactory().createMultipleObjectCrud();

    async getFolderList(filter?: IFilter): Promise<IResponse<ICollection<IFolder>>> {
        return (await this.multipleObjectCrud.getCollection(filter));
    }

    async getFolder(pkey: any): Promise<IResponse<IFolder>> {
        return (await this.singleObjectCrud.getElement(pkey));
    }

    async createFolder(folder: IFolder): Promise<IResponse<IFolder>> {
        return (await this.singleObjectCrud.createElement(folder));
    }

    async deleteFolder(pkey: any): Promise<IResponse<boolean>> {
        return (await this.singleObjectCrud.deleteElement(pkey));
    }
}