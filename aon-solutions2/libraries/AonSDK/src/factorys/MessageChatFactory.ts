import { ISingleObjectCrudFactory, IMultipleObjectCrudFactory } from "../interfaces/factoryInterfaces";
import { IMessageChat } from "../interfaces/modelsInterfaces";
import { ISingleObjectCrud, IMultipleObjectCrud } from "../interfaces/serviceInterfaces";
import { MessageChat, ApiMessageChat, StorableMessageChat } from "../models/MessageChat";
import { GenericSingleObjectCrudRepository, APIGenericMultipleObjectCrudRepository, GenericMultipleObjectCrudRepository } from "../repositorys/GenericRepository";
import { APIMessageChatSingleObjectCrudRepository } from "../repositorys/MessageChatRepository";
import { GenericSingleObjectCrud, GenericMultipleObjectCrud } from "../services/GenericCrudService";
import { APIEnvironment } from "../utils/constants";

export class MessageChatFactory implements ISingleObjectCrudFactory<IMessageChat>, IMultipleObjectCrudFactory<IMessageChat> {
    createSingleObjectCrud(): ISingleObjectCrud<IMessageChat> {
        return new GenericSingleObjectCrud<MessageChat>(
            (APIEnvironment ?
            new APIMessageChatSingleObjectCrudRepository(new ApiMessageChat(), MessageChat) :
            new GenericSingleObjectCrudRepository<MessageChat>(new StorableMessageChat(), MessageChat)
            ),
            MessageChat);
    }
    createMultipleObjectCrud(): IMultipleObjectCrud<IMessageChat> {
        return new GenericMultipleObjectCrud<MessageChat>(
            (APIEnvironment ?
            new APIGenericMultipleObjectCrudRepository<MessageChat>(new ApiMessageChat(), MessageChat) :
            new GenericMultipleObjectCrudRepository<MessageChat>(new StorableMessageChat(), MessageChat)
            ),
            MessageChat);
    }
}