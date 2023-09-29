import { MessageFactory } from "../factorys/MessageFactory";
import { TaskHolderFactory } from "../factorys/TaskHolderFactory";
import { IMessage, ITaskHolder } from "../interfaces/modelsInterfaces";
import { ICollection, IFilter, IResponse } from "../interfaces/utilitiesInterfaces";

export class MessageFunctions {
    private singleObjectCrud = new MessageFactory().createSingleObjectCrud();
    private multipleObjectCrud = new MessageFactory().createMultipleObjectCrud();
    private specificMethods = new MessageFactory().createMessageSpecificMethods();
    private multipleObjectCrudTaskHolders = new TaskHolderFactory().createMultipleObjectCrud();
    private singleObjectCrudTaskHolders = new TaskHolderFactory().createSingleObjectCrud();

    async getMessageList(filter?: IFilter): Promise<IResponse<ICollection<IMessage>>> {
        return (await this.multipleObjectCrud.getCollection(filter));
    }

    async getMessageCount(filter?: IFilter): Promise<IResponse<number>> {
        return (await this.specificMethods.getMessageCount(filter));
    }

    async getMessage(pkey: any): Promise<IResponse<IMessage>> {
        return (await this.singleObjectCrud.getElement(pkey));
    }

    async updateMessage(messages: IMessage): Promise<IResponse<IMessage>> {
        return (await this.singleObjectCrud.updateElement(messages));
    }

    async createMessage(message: IMessage): Promise<IResponse<IMessage>> {
        return (await this.singleObjectCrud.createElement(message));
    }

    async archiveMessage(message: IMessage): Promise<IResponse<IMessage>> {
        return (await this.specificMethods.archiveMessage(message));
    }

    async reopenMessage(message: IMessage): Promise<IResponse<IMessage>> {
        return (await this.specificMethods.reopenMessage(message));
    }

    async getTaskHoldersList(filter?: IFilter): Promise<IResponse<ICollection<ITaskHolder>>> {
        return (await this.multipleObjectCrudTaskHolders.getCollection(filter));
    }

    async getTaskHolder(pkey: any): Promise<IResponse<ITaskHolder>> {
        return (await this.singleObjectCrudTaskHolders.getElement(pkey));
    }

    async markAsReadNotification(message: IMessage): Promise<IResponse<boolean>> {
        return (await this.specificMethods.markAsReadNotification(message));
    }
}