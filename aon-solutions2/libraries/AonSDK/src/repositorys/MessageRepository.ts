import { IMessage, TypeMessage, StatusMessage, ApiTypeMessage, ApiStatusMessage } from "../interfaces/modelsInterfaces";
import { IMessageSpecificMethodsRepository } from "../interfaces/repositoryInterfaces";
import { ICollection, IFilter } from "../interfaces/utilitiesInterfaces";
import { Message, ApiMessage, StorableMessage } from "../models/Message";
import { ApiHttpRequest } from "../utils/Http";
import { ErrorResponse } from "../utils/Response";
import { BASE_URL, GET_SINGLE } from "../utils/Environment";
import { APIGenericSingleObjectCrudRepository, APIGenericMultipleObjectCrudRepository, GenericMultipleObjectCrudRepository } from "./GenericRepository";
import { messages } from "../models/Message";
import { Collection } from "../utils/Collection";
import { MESSAGE_URL } from "../utils/ApiUrls";

export class APIMessageSingleObjectCrudRepository extends APIGenericSingleObjectCrudRepository<Message> {
    constructor(apiModel: ApiMessage, type: { new (): Message }){
        super(apiModel, type);
    }

    async get(key: string): Promise<Message> {
        if(key.split(';')[1] == TypeMessage.NOTIFICACION){
            let params = {
                id: key.split(';')[0]
            }
            return this.apiModel.parseDataToReceive(await ApiHttpRequest.get(BASE_URL + ApiHttpRequest.makeURL(MESSAGE_URL.GET_ONE_NOTIFICATION, params), {}, {}));
        }else {
            let params = {
                id: key.split(';')[0]
            }
            return this.apiModel.parseDataToReceive(await ApiHttpRequest.get(BASE_URL + ApiHttpRequest.makeURL(MESSAGE_URL.GET_ONE_MESSAGE, params), {}, {}));
        }
    }

    async create(message: Message): Promise<Message> {
        let cauInfo = await ApiHttpRequest.get(BASE_URL + MESSAGE_URL.GET_CAU, {}, {})
        let sender = await ApiHttpRequest.get(BASE_URL + MESSAGE_URL.GET_TASK_HOLDER_ONE + '?id=' + localStorage.getItem('registry'), {}, {})
        let taskHolder = await ApiHttpRequest.get(BASE_URL + MESSAGE_URL.GET_TASK_HOLDER_ONE + '?id=' + message.TaskHolder.Id, {}, {})
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
        let newMessage: Message = this.apiModel.parseDataToReceive(await ApiHttpRequest.post(BASE_URL + '/ms/api/task', {}, json), GET_SINGLE);
        return newMessage;
    }

    async update(element: Message): Promise<Message> {
        let jsonData = this.apiModel.parseDataToSend(element)
        return this.apiModel.parseDataToReceive(await ApiHttpRequest.post(BASE_URL + MESSAGE_URL.SAVE_ONE_MESSAGE, {}, jsonData))
    }
}

let generateParams = (filter?: IFilter, pageNum?: number, perPageItems?: number, source?: string, status?: any): any => {
    let params = {
        page: pageNum ? pageNum : 1,
        perPage: perPageItems ? perPageItems : !source ? 10 : 30,
    }
    let statusString = '';
    if(status){
        if(typeof status == 'object'){
            status.forEach((element: any) => {
                statusString += ApiStatusMessage[element as keyof typeof ApiStatusMessage] + ","
            })
            statusString = statusString.substring(0, statusString.length - 1);
        }
        else
            statusString = ApiStatusMessage[status as keyof typeof ApiStatusMessage]
        Object.defineProperties(params, {
            'status': {
                value: statusString,
                enumerable : true,
            },
        })
    }else if(source == TypeMessage.CONSULTA || source == TypeMessage.TAREA){
        Object.defineProperties(params, {
            'status': {
                value: ApiStatusMessage.allTask,
                enumerable : true,
            },
        })
    }
    if(source! != TypeMessage.NOTIFICACION){
        Object.defineProperties(params, {
            'task_holder': {
                value: localStorage.getItem('registry'),
                enumerable : true,
            },
            'sender': {
                value: localStorage.getItem('registry'),
                enumerable : true,
            }
        })
    }
    Object.defineProperty(params, 'source', {
        value: ApiTypeMessage[source as keyof typeof ApiTypeMessage],
        enumerable : true,
    })
    if(filter?.intervalFields?.get('date')){
        Object.defineProperty(params, 'startDate', {
            value: new Date(filter?.intervalFields?.get('date').start).toISOString(),
            enumerable : true,
        })
        Object.defineProperty(params, 'endDate', {
            value: new Date(filter?.intervalFields?.get('date').end).toISOString(),
            enumerable : true,
        })
    }
    return params;
}

export class APIMessageMultipleObjectCrudRepository extends APIGenericMultipleObjectCrudRepository<Message> {
    constructor(apiModel: ApiMessage, type: { new (): Message }){
        super(apiModel, type);
    }

    async get(filter?: IFilter | undefined): Promise<ICollection<Message>> {
        let url: any = [];
        let perPage = filter?.pageItems ? filter?.pageItems : 15;
        let pageNum = filter?.pageNum ? filter?.pageNum : 1;
        let typeMessage = filter?.fields?.get('type')[0];
        let statusMessage = filter?.fields?.get('status');
        if(typeMessage == TypeMessage.NOTIFICACION){
            url = [ApiHttpRequest.makeURL(MESSAGE_URL.GET_NOTIFICATION_LIST, generateParams(filter || {}, pageNum, perPage, typeMessage, statusMessage))]
        }else if(typeMessage == TypeMessage.CONSULTA || typeMessage == TypeMessage.TAREA){
            url = [ApiHttpRequest.makeURL(MESSAGE_URL.GET_TASK_QUERY_LIST, generateParams(filter || {}, pageNum, perPage, typeMessage, statusMessage))]
        }else
            url = [
                ApiHttpRequest.makeURL(MESSAGE_URL.GET_NOTIFICATION_LIST, generateParams(filter || {}, pageNum, perPage, TypeMessage.NOTIFICACION, StatusMessage.NUEVA)),
                ApiHttpRequest.makeURL(MESSAGE_URL.GET_TASK_QUERY_LIST, generateParams(filter || {}, pageNum, perPage, TypeMessage.CONSULTA, StatusMessage.ABIERTA)),
                ApiHttpRequest.makeURL(MESSAGE_URL.GET_TASK_QUERY_LIST, generateParams(filter || {}, pageNum, perPage, TypeMessage.TAREA, StatusMessage.PENDIENTE)),
            ]
        let collection: ICollection<Message> = new Collection<Message>();
        for(let element of url){
            let response = await ApiHttpRequest.get(BASE_URL + element, {}, {});
            response.forEach((element: any) => {
                collection.add(this.apiModel.parseDataToReceive(element))
            })
        }
        return collection;
    }
}

export class APIMessageSpecificMethodsRepository implements IMessageSpecificMethodsRepository {

    repository = new APIMessageSingleObjectCrudRepository(new ApiMessage(), Message);

    async archiveMessage(element: Message): Promise<IMessage> {
        if(element.Type != TypeMessage.NOTIFICACION){
            element.Status = element.Type == TypeMessage.CONSULTA ? StatusMessage.CERRADA : StatusMessage.REALIZADA;
            let updatedMessage: IMessage = await this.repository.update(element);
            return updatedMessage;
        }
        throw new ErrorResponse('0199');
    }

    async reopenMessage(element: Message): Promise<IMessage> {
        if(element.Type != TypeMessage.NOTIFICACION){
            element.Status = element.Type == TypeMessage.CONSULTA ? StatusMessage.ABIERTA : StatusMessage.PENDIENTE;
            let updatedMessage: IMessage = await this.repository.update(element);
            return updatedMessage;
        }
        throw new ErrorResponse('0199');
    }

    async getMessageCount(filter?: IFilter): Promise<number> {
        if(filter?.fields?.get('type') == TypeMessage.NOTIFICACION){
            let result = await ApiHttpRequest.get(BASE_URL + MESSAGE_URL.GET_COUNT_NOTIFICATION, {}, {})
            return result.notification
        } else if(filter?.fields?.get('type') == TypeMessage.TAREA){
            return (await ApiHttpRequest.get(BASE_URL + ApiHttpRequest.makeURL(MESSAGE_URL.GET_COUNT_TASK_QUERY, generateParams(filter || {}, 1, 100, TypeMessage.TAREA, filter?.fields?.get('type'))), {}, {}))
        } else if(filter?.fields?.get('type') == TypeMessage.CONSULTA){
            return (await ApiHttpRequest.get(BASE_URL + ApiHttpRequest.makeURL(MESSAGE_URL.GET_COUNT_TASK_QUERY, generateParams(filter || {}, 1, 100, TypeMessage.CONSULTA, filter?.fields?.get('type'))), {}, {}))
        } else {
            let result1 = await ApiHttpRequest.get(BASE_URL + MESSAGE_URL.GET_COUNT_NOTIFICATION, {}, {})
            let result2 = (await ApiHttpRequest.get(BASE_URL + ApiHttpRequest.makeURL(MESSAGE_URL.GET_COUNT_TASK_QUERY, generateParams(filter || {}, 1, 100, TypeMessage.TAREA, filter?.fields?.get('type'))), {}, {}))
            let result3 = (await ApiHttpRequest.get(BASE_URL + ApiHttpRequest.makeURL(MESSAGE_URL.GET_COUNT_TASK_QUERY, generateParams(filter || {}, 1, 100, TypeMessage.CONSULTA, filter?.fields?.get('type'))), {}, {}))
            return result1.notification + result2 + result3;
        }
    }

    async markAsReadNotification(element: Message): Promise<boolean> {
        if(element.Type == TypeMessage.NOTIFICACION){
            let result = await ApiHttpRequest.post(BASE_URL + MESSAGE_URL.GET_MARK_READ_NOTIFICATION, {}, {source_id: element.Id});
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