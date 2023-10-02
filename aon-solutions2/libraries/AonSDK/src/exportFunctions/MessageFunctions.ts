import { MessageFactory } from "../factorys/MessageFactory";
import { TaskHolderFactory } from "../factorys/TaskHolderFactory";
import { IMessage, ITaskHolder } from "../interfaces/modelsInterfaces";
import { ICollection, IFilter, IResponse } from "../interfaces/utilitiesInterfaces";

export class MessageFunctions {
    private static singleObjectCrud = new MessageFactory().createSingleObjectCrud();
    private static multipleObjectCrud = new MessageFactory().createMultipleObjectCrud();
    private static specificMethods = new MessageFactory().createMessageSpecificMethods();
    private static multipleObjectCrudTaskHolders = new TaskHolderFactory().createMultipleObjectCrud();
    private static singleObjectCrudTaskHolders = new TaskHolderFactory().createSingleObjectCrud();

    static async getMessageList(filter?: IFilter): Promise<IResponse<ICollection<IMessage>>> {
        return (await this.multipleObjectCrud.getCollection(filter));
    }

    static async getMessageCount(filter?: IFilter): Promise<IResponse<number>> {
        return (await this.specificMethods.getMessageCount(filter));
    }

    static async getMessage(pkey: any): Promise<IResponse<IMessage>> {
        return (await this.singleObjectCrud.getElement(pkey));
    }

    static async updateMessage(messages: IMessage): Promise<IResponse<IMessage>> {
        return (await this.singleObjectCrud.updateElement(messages));
    }

    static async createMessage(message: IMessage): Promise<IResponse<IMessage>> {
        return (await this.singleObjectCrud.createElement(message));
    }

    static async archiveMessage(message: IMessage): Promise<IResponse<IMessage>> {
        return (await this.specificMethods.archiveMessage(message));
    }

    static async reopenMessage(message: IMessage): Promise<IResponse<IMessage>> {
        return (await this.specificMethods.reopenMessage(message));
    }

    static async getTaskHoldersList(filter?: IFilter): Promise<IResponse<ICollection<ITaskHolder>>> {
        return (await this.multipleObjectCrudTaskHolders.getCollection(filter));
    }

    static async getTaskHolder(pkey: any): Promise<IResponse<ITaskHolder>> {
        return (await this.singleObjectCrudTaskHolders.getElement(pkey));
    }

    static async markAsReadNotification(message: IMessage): Promise<IResponse<boolean>> {
        return (await this.specificMethods.markAsReadNotification(message));
    }
}