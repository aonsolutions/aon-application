import { MessageChat, ApiMessageChat } from "../models/MessageChat";
import { ApiHttpRequest } from "../utils/Http";
import { ErrorResponse } from "../utils/Response";
import { BASE_URL, GET_METHOD, POST_METHOD, GET_MULTIPLE } from "../utils/Environment";
import { APIGenericMultipleObjectCrudRepository, APIGenericSingleObjectCrudRepository } from "./GenericRepository";
import { IFilter, ICollection } from "../aon";
import { Collection } from "../utils/Collection";

export class APIMessageChatSingleObjectCrudRepository extends APIGenericSingleObjectCrudRepository<MessageChat> {

    constructor(apiModel: ApiMessageChat, type: { new (): MessageChat }){
        super(apiModel, type);
    }

    async create(messageChat: MessageChat): Promise<MessageChat> {
        let sender = await ApiHttpRequest.get(BASE_URL + '/ms/api/taskholder?id=' + localStorage.getItem('registry'), {}, {})
        let json = {
            comment: messageChat.Description,
            domain: localStorage.getItem('domainId'),
            email: '',
            task: messageChat.IdMessage,
            task_holder: sender,
            type: 'comment'
        }
        let newMessage: MessageChat = this.apiModel.parseDataToReceive(await ApiHttpRequest.post(BASE_URL + '/ms/api/task/workflow', {}, json), GET_MULTIPLE);
        return newMessage;
        throw new ErrorResponse('0199')
    }
}

export class APIMessageChatMultipleObjectCrudRepository extends APIGenericMultipleObjectCrudRepository<MessageChat> {
    constructor(apiModel: ApiMessageChat, type: { new (): MessageChat }){
        super(apiModel, type);
    }

    async get(filter?: IFilter | undefined): Promise<ICollection<MessageChat>> {
        let url;
        if(filter && filter.fields)
            url = '/ms/api/task/workflow?task='+filter.fields.get('idMessage')+'&domainId='+localStorage.getItem('domainId')+'&domainName='+localStorage.getItem('domainName');
        else
            throw new Error('No se ha introducido el id')
        let response = await ApiHttpRequest.get(BASE_URL + '/ms/api/fiscal/models', {}, {});
        let collection: ICollection<MessageChat> = new Collection<MessageChat>();
        response.forEach((element: any) => {
            collection.add(this.apiModel.parseDataToReceive(element))
        })
        return collection;
    }
}