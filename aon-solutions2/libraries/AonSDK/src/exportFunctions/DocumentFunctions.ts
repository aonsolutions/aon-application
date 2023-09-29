import { DocumentFactory } from "../factorys/DocumentFactory";
import { IDocument } from "../interfaces/modelsInterfaces";
import { ICollection, IFilter, IResponse } from "../interfaces/utilitiesInterfaces";

export class DocumentFunctions {
    private singleObjectCrud = new DocumentFactory().createSingleObjectCrud();
    private multipleObjectCrud = new DocumentFactory().createMultipleObjectCrud();

    async getDocumentList(filter?: IFilter): Promise<IResponse<ICollection<IDocument>>> {
        return (await this.multipleObjectCrud.getCollection(filter));
    }

    async getDocument(pkey: any): Promise<IResponse<IDocument>> {
        return (await this.singleObjectCrud.getElement(pkey));
    }

    async updateDocument(documents: IDocument): Promise<IResponse<IDocument>> {
        return (await this.singleObjectCrud.updateElement(documents));
    }

    async deleteDocument(pkey: any): Promise<IResponse<boolean>> {
        return (await this.singleObjectCrud.deleteElement(pkey));
    }

    async createDocument(documents: IDocument): Promise<IResponse<IDocument>> {
        return (await this.singleObjectCrud.createElement(documents));
    }
}