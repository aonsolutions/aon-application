import { ContactFactory } from "../factorys/ContactFactory";
import { IContact } from "../interfaces/modelsInterfaces";
import { ICollection, IFilter, IResponse } from "../interfaces/utilitiesInterfaces";

export class ContactFunctions {
    private static contactFactory = new ContactFactory();

    static async getContact(key: string): Promise<IResponse<IContact>>{
        return (await this.contactFactory.createSingleObjectCrud().getElement(key));
    }

    static async getContactList(filter?: IFilter): Promise<IResponse<ICollection<IContact>>> {
        return (await this.contactFactory.createMultipleObjectCrud().getCollection(filter));
    }

    static async createContact(contact: IContact): Promise<IResponse<IContact>> {
        return (await this.contactFactory.createSingleObjectCrud().createElement(contact));
    }

    static async updateContact(contact: IContact): Promise<IResponse<IContact>> {
        return (await this.contactFactory.createSingleObjectCrud().updateElement(contact));
    }

    static async deleteContact(key: string): Promise<IResponse<boolean>> {
        return (await this.contactFactory.createSingleObjectCrud().deleteElement(key));
    }
}