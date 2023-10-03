import { ISingleObjectCrudFactory, IMultipleObjectCrudFactory } from "../interfaces/factoryInterfaces";
import { IMessage } from "../interfaces/modelsInterfaces";
import { ISingleObjectCrud, IMultipleObjectCrud, IMessageSpecificMethods } from "../interfaces/serviceInterfaces";
import { Message, ApiMessage, StorableMessage } from "../models/Message";
import { GenericSingleObjectCrudRepository, GenericMultipleObjectCrudRepository } from "../repositorys/GenericRepository";
import { APIMessageSingleObjectCrudRepository, APIMessageMultipleObjectCrudRepository, APIMessageSpecificMethodsRepository, LocalMessageSpecificMethodsRepository } from "../repositorys/MessageRepository";
import { GenericSingleObjectCrud, GenericMultipleObjectCrud } from "../services/GenericCrudService";
import { MessageSpecificMethods } from "../services/MessageService";
import { APIEnvironment } from "../utils/Environment";

export class MessageFactory implements ISingleObjectCrudFactory<IMessage>, IMultipleObjectCrudFactory<IMessage> {
    createSingleObjectCrud(): ISingleObjectCrud<IMessage> {
        return new GenericSingleObjectCrud<Message>(
            (APIEnvironment ?
            // new APIGenericSingleObjectCrudRepository<Message>(new ApiMessage(), Message) :
            new APIMessageSingleObjectCrudRepository(new ApiMessage(), Message) :
            new GenericSingleObjectCrudRepository<Message>(new StorableMessage(), Message)
            ),
            Message);
    }
    createMultipleObjectCrud(): IMultipleObjectCrud<IMessage> {
        return new GenericMultipleObjectCrud<Message>(
            (APIEnvironment ?
            new APIMessageMultipleObjectCrudRepository(new ApiMessage(), Message) :
            new GenericMultipleObjectCrudRepository<Message>(new StorableMessage(), Message)
            ),
            Message);
    }
    createMessageSpecificMethods(): IMessageSpecificMethods {
        return new MessageSpecificMethods(
            (APIEnvironment ?
            new APIMessageSpecificMethodsRepository() :
            new LocalMessageSpecificMethodsRepository()
            )
        )
    }
}