import { IMessage, TypeMessage, StatusMessage } from "../interfaces/modelsInterfaces";
import { IMessageSpecificMethodsRepository } from "../interfaces/repositoryInterfaces";
import { IApiHttpRequest, IFilter } from "../interfaces/utilitiesInterfaces";
import { Message, ApiMessage, StorableMessage } from "../models/Message";
import { ApiHttpRequest } from "../utils/Http";
import { ErrorResponse } from "../utils/Response";
import { BASE_URL, GET_METHOD, POST_METHOD, GET_SINGLE } from "../utils/constants";
import { APIGenericSingleObjectCrudRepository, APIGenericMultipleObjectCrudRepository, GenericMultipleObjectCrudRepository } from "./GenericRepository";
import { messages } from "../models/Message";

export class APIMessageSingleObjectCrudRepository extends APIGenericSingleObjectCrudRepository<Message> {
    constructor(apiModel: ApiMessage, type: { new (): Message }){
        super(apiModel, type);
    }

    async create(message: Message): Promise<Message> {
        let cauInfo = await this.httpRequest.httpRequest(BASE_URL + '/ms/api/task/cau', GET_METHOD, {}, {})
        let sender = await this.httpRequest.httpRequest(BASE_URL + '/ms/api/taskholder?id=' + localStorage.getItem('registry'), GET_METHOD, {}, {})
        let taskHolder = await this.httpRequest.httpRequest(BASE_URL + '/ms/api/taskholder?id=' + message.TaskHolder.Id, GET_METHOD, {}, {})
        let domain = {
            id: localStorage.getItem('domainId'),
            name: localStorage.getItem('domainName')
        }
        let workflow = {
            comment: "",
            domain: localStorage.getItem('domainId'),
            task_holder: sender,
            type: "opened",
            email: cauInfo.auth.email || ''
        }
        let description = JSON.stringify({
            observation: message.Description,
            cauinfo: cauInfo,
        })
        let json = {
            domain: domain,
            sender: sender,
            task_holder: taskHolder,
            workgroup: {},
            title: message.Title,
            registry: {},
            workflow: [workflow],
            description: description,
            status: 'pending',
            source: 'query',
            source_id: null,
            files: [],
            project: {},
            tags: [],
            childs: [],
            domaintmp: domain,
            workflowtmp: workflow,
            mytaskholder: sender,
            auth: cauInfo.auth,
            domaincompany: cauInfo.company.domain
        }
        let newMessage: Message = this.apiModel.parseDataToReceive(await this.httpRequest.httpRequest(BASE_URL + '/ms/api/task', POST_METHOD, {}, json), GET_SINGLE);
        return newMessage;
    }
}

export class APIMessageMultipleObjectCrudRepository extends APIGenericMultipleObjectCrudRepository<Message> {
    constructor(apiModel: ApiMessage, type: { new (): Message }){
        super(apiModel, type);
    }
}

export class APIMessageSpecificMethodsRepository implements IMessageSpecificMethodsRepository {

    protected httpRequest: IApiHttpRequest = new ApiHttpRequest();
    repository = new APIGenericSingleObjectCrudRepository<Message>(new ApiMessage(), Message);

    async archiveMessage(element: Message): Promise<IMessage> {
        if(element.Type != 'notificacion'){
            element.Status = element.Type == TypeMessage.CONSULTA ? StatusMessage.CERRADA : StatusMessage.REALIZADA;
            let updatedMessage: IMessage = await this.repository.update(element);
            return updatedMessage;
        }
        throw new ErrorResponse('0199');
    }

    async reopenMessage(element: Message): Promise<IMessage> {
        if(element.Type != 'notificacion'){
            element.Status = element.Type == TypeMessage.CONSULTA ? StatusMessage.ABIERTA : StatusMessage.PENDIENTE;
            let updatedMessage: IMessage = await this.repository.update(element);
            return updatedMessage;
        }
        throw new ErrorResponse('0199');
    }

    async getMessageCount(filter?: IFilter): Promise<number> {
        if(filter && filter.fields && filter.fields.get('type') == 'notificacion'){
            let result = await this.httpRequest.httpRequest(BASE_URL + '/ms/api/notification/total-notification', GET_METHOD, {}, {})
            return result.notification
        } else if(filter && filter.fields && (filter.fields.get('type') == 'tarea' || filter.fields.get('type') == 'consulta')){
            let result = await this.httpRequest.httpRequest(BASE_URL + '/ms/api/task/status/count?task_holder=' + localStorage.getItem('registry'), GET_METHOD, {}, {});
            return result.status.pending;
        } else {
            let result1 = await this.httpRequest.httpRequest(BASE_URL + '/ms/api/notification/total-notification', GET_METHOD, {}, {})
            let result2 = await this.httpRequest.httpRequest(BASE_URL + '/ms/api/task/status/count?task_holder=' + localStorage.getItem('registry'), GET_METHOD, {}, {});
            return result1.notification + result2.status.pending;
        }
    }

    async markAsReadNotification(element: Message): Promise<boolean> {
        if(element.Type == 'notificacion'){
            let result = await this.httpRequest.httpRequest(BASE_URL + '/ms/api/notification/mark-read-notification', POST_METHOD, {}, {source_id: element.Id});
            if(result.success && result.success == true)
                return true;
            else
                return false;
        }
        else
            throw new ErrorResponse('0199');
    }
}

export class LocalMessageSpecificMethodsRepository implements IMessageSpecificMethodsRepository {
    async archiveMessage(element: Message): Promise<IMessage> {
        element.Type == TypeMessage.CONSULTA ? element.Status = StatusMessage.CERRADA : StatusMessage.REALIZADA
        messages.add(element);
        return element;
    }

    async reopenMessage(element: Message): Promise<IMessage> {
        element.Type == TypeMessage.CONSULTA ? element.Status = StatusMessage.ABIERTA : StatusMessage.PENDIENTE
        messages.add(element);
        return element;
    }

    async getMessageCount(filter?: IFilter | undefined): Promise<number> {
        return (await new GenericMultipleObjectCrudRepository<Message>(new StorableMessage(), Message).get(filter)).size();
    }

    async markAsReadNotification(message: Message): Promise<boolean> {
        message.Status = StatusMessage.VISTA;
        messages.add(message);
        return true;
    }
}