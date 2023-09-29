import { ContactFactory } from "../factorys/ContactFactory";
import { IContact } from "../interfaces/modelsInterfaces";
import { ICollection, IFilter, IResponse } from "../interfaces/utilitiesInterfaces";

export class ContactFunctions {
    private contactFactory = new ContactFactory();

    async getContact(key: string): Promise<IResponse<IContact>>{
        return (await this.contactFactory.createSingleObjectCrud().getElement(key));
    }

    async getContactList(filter?: IFilter): Promise<IResponse<ICollection<IContact>>> {
        return (await this.contactFactory.createMultipleObjectCrud().getCollection(filter));
    }

    async createContact(contact: IContact): Promise<IResponse<IContact>> {
        return (await this.contactFactory.createSingleObjectCrud().createElement(contact));
    }

    async updateContact(contact: IContact): Promise<IResponse<IContact>> {
        return (await this.contactFactory.createSingleObjectCrud().updateElement(contact));
    }

    async deleteContact(key: string): Promise<IResponse<boolean>> {
        return (await this.contactFactory.createSingleObjectCrud().deleteElement(key));
    }
}