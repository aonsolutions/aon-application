import { IMessage } from "../interfaces/modelsInterfaces";
import { IMessageSpecificMethodsRepository } from "../interfaces/repositoryInterfaces";
import { IMessageSpecificMethods } from "../interfaces/serviceInterfaces";
import { IResponse, IFilter } from "../interfaces/utilitiesInterfaces";
import { Message } from "../models/Message";
import { Response } from "../utils/Response";

export class MessageSpecificMethods implements IMessageSpecificMethods {

    repository: IMessageSpecificMethodsRepository;

    constructor(repository: IMessageSpecificMethodsRepository) {
        this.repository = repository;
    }

    async archiveMessage(element: Message): Promise<IResponse<IMessage>> {
        return new Response<IMessage>(await this.repository.archiveMessage(element));
    }

    async reopenMessage(element: Message): Promise<IResponse<IMessage>> {
        return new Response<IMessage>(await this.repository.reopenMessage(element));
    }

    async getMessageCount(filter?: IFilter): Promise<IResponse<number>> {
        return new Response<number>(await this.repository.getMessageCount(filter));
    }

    async markAsReadNotification(message: Message): Promise<IResponse<boolean>> {
        return new Response<boolean>(await this.repository.markAsReadNotification(message));
    }

}