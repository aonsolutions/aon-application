import { MessageChatFactory } from "../factorys/MessageChatFactory";
import { IMessageChat } from "../interfaces/modelsInterfaces";
import { ICollection, IFilter, IResponse } from "../interfaces/utilitiesInterfaces";
import { Response } from "../utils/Response";

export class MessageChatFunctions {
    private singleObjectCrud = new MessageChatFactory().createSingleObjectCrud();
    private multipleObjectCrud = new MessageChatFactory().createMultipleObjectCrud();

    async getMessageChatCount(filter?: IFilter): Promise<IResponse<number>> {
        return new Response((await this.multipleObjectCrud.getCollection(filter)).result.size());
    }

    async getMessageChatList(filter: IFilter): Promise<IResponse<ICollection<IMessageChat>>> {
        return (await this.multipleObjectCrud.getCollection(filter));
    }

    async createMessageChat(messageChats: IMessageChat): Promise<IResponse<IMessageChat>> {
        return (await this.singleObjectCrud.createElement(messageChats));
    }
}