import { MessageChat, ApiMessageChat } from "../models/MessageChat";
import { ErrorResponse } from "../utils/Response";
import { BASE_URL, GET_METHOD, POST_METHOD, GET_MULTIPLE } from "../utils/constants";
import { APIGenericSingleObjectCrudRepository } from "./GenericRepository";

export class APIMessageChatSingleObjectCrudRepository extends APIGenericSingleObjectCrudRepository<MessageChat> {
    constructor(apiModel: ApiMessageChat, type: { new (): MessageChat }){
        super(apiModel, type);
    }

    async create(messageChat: MessageChat): Promise<MessageChat> {
        //comment
        //domain
        //email
        //task
        //task_holder
        //type
        let sender = await this.httpRequest.httpRequest(BASE_URL + '/ms/api/taskholder?id=' + localStorage.getItem('registry'), GET_METHOD, {}, {})
        let json = {
            comment: messageChat.Description,
            domain: localStorage.getItem('domainId'),
            email: '',
            task: messageChat.IdMessage,
            task_holder: sender,
            type: 'comment'
        }
        let newMessage: MessageChat = this.apiModel.parseDataToReceive(await this.httpRequest.httpRequest(BASE_URL + '/ms/api/task/workflow', POST_METHOD, {}, json), GET_MULTIPLE);
        return newMessage;
        throw new ErrorResponse('0199')
    }
}