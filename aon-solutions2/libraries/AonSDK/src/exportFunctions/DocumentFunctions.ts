import { DocumentFactory } from "../factorys/DocumentFactory";
import { IDocument } from "../interfaces/modelsInterfaces";
import { ICollection, IFilter, IResponse } from "../interfaces/utilitiesInterfaces";

export class DocumentFunctions {
    private static singleObjectCrud = new DocumentFactory().createSingleObjectCrud();
    private static multipleObjectCrud = new DocumentFactory().createMultipleObjectCrud();

    static async getDocumentList(filter?: IFilter): Promise<IResponse<ICollection<IDocument>>> {
        return (await this.multipleObjectCrud.getCollection(filter));
    }

    static async getDocument(pkey: any): Promise<IResponse<IDocument>> {
        return (await this.singleObjectCrud.getElement(pkey));
    }

    static async updateDocument(documents: IDocument): Promise<IResponse<IDocument>> {
        return (await this.singleObjectCrud.updateElement(documents));
    }

    static async deleteDocument(pkey: any): Promise<IResponse<boolean>> {
        return (await this.singleObjectCrud.deleteElement(pkey));
    }

    static async createDocument(documents: IDocument): Promise<IResponse<IDocument>> {
        return (await this.singleObjectCrud.createElement(documents));
    }
}