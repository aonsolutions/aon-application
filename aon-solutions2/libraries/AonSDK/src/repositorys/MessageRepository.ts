import { IMessage, TypeMessage, StatusMessage } from "../interfaces/modelsInterfaces";
import { IMessageSpecificMethodsRepository } from "../interfaces/repositoryInterfaces";
import { ICollection, IFilter } from "../interfaces/utilitiesInterfaces";
import { Message, ApiMessage, StorableMessage } from "../models/Message";
import { ApiHttpRequest } from "../utils/Http";
import { ErrorResponse } from "../utils/Response";
import { BASE_URL, GET_METHOD, POST_METHOD, GET_SINGLE } from "../utils/Environment";
import { APIGenericSingleObjectCrudRepository, APIGenericMultipleObjectCrudRepository, GenericMultipleObjectCrudRepository } from "./GenericRepository";
import { messages } from "../models/Message";
import { Collection } from "../utils/Collection";

export class APIMessageSingleObjectCrudRepository extends APIGenericSingleObjectCrudRepository<Message> {
    constructor(apiModel: ApiMessage, type: { new (): Message }){
        super(apiModel, type);
    }

    async create(message: Message): Promise<Message> {
        let cauInfo = await ApiHttpRequest.get(BASE_URL + '/ms/api/task/cau', {}, {})
        let sender = await ApiHttpRequest.get(BASE_URL + '/ms/api/taskholder?id=' + localStorage.getItem('registry'), {}, {})
        let taskHolder = await ApiHttpRequest.get(BASE_URL + '/ms/api/taskholder?id=' + message.TaskHolder.Id, {}, {})
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
}

export class APIMessageMultipleObjectCrudRepository extends APIGenericMultipleObjectCrudRepository<Message> {
    constructor(apiModel: ApiMessage, type: { new (): Message }){
        super(apiModel, type);
    }

    async get(filter?: IFilter | undefined): Promise<ICollection<Message>> {
        let url;
        let taskHolderAndSender = /*'&task_holder=' + localStorage.getItem('registry') +*/ '&sender=' + localStorage.getItem('registry') + '&status=pending'
        if(filter && filter.fields?.has('type') && filter.fields.get('type') == TypeMessage.NOTIFICACION)
            url = ['/ms/api/notification?page=1&perPage=100']
        else if(filter && filter.fields?.has('type') && filter.fields.get('type') == TypeMessage.CONSULTA)
            url = ['/ms/api/task?source=query&page=1&perPage=100' + taskHolderAndSender]
        else if(filter && filter.fields?.has('type') && filter.fields.get('type') == TypeMessage.TAREA)
            url = ['/ms/api/task?source=task&page=1&perPage=100' + taskHolderAndSender]
        else
            url = [
                '/ms/api/notification?page=1&perPage=100',
                '/ms/api/task?source=query&page=1&perPage=100' + taskHolderAndSender,
                '/ms/api/task?source=task&page=1&perPage=100' + taskHolderAndSender
            ]
        let collection: ICollection<Message> = new Collection<Message>();
        for(let element of url){
            let response = await ApiHttpRequest.get(BASE_URL + element, {}, {});
            response.forEach((element: any) => {
                collection.add(this.apiModel.parseDataToReceive(element))
            })
        }
        console.log(collection)
        return collection;
    }
}

export class APIMessageSpecificMethodsRepository implements IMessageSpecificMethodsRepository {

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
            let result = await ApiHttpRequest.get(BASE_URL + '/ms/api/notification/total-notification', {}, {})
            return result.notification
        } else if(filter && filter.fields && (filter.fields.get('type') == 'tarea' || filter.fields.get('type') == 'consulta')){
            let result = await ApiHttpRequest.get(BASE_URL + '/ms/api/task/status/count?task_holder=' + localStorage.getItem('registry'), {}, {});
            return result.status.pending;
        } else {
            let result1 = await ApiHttpRequest.get(BASE_URL + '/ms/api/notification/total-notification', {}, {})
            let result2 = await ApiHttpRequest.get(BASE_URL + '/ms/api/task/status/count?task_holder=' + localStorage.getItem('registry'), {}, {});
            return result1.notification + result2.status.pending;
        }
    }

    async markAsReadNotification(element: Message): Promise<boolean> {
        if(element.Type == 'notificacion'){
            let result = await ApiHttpRequest.post(BASE_URL + '/ms/api/notification/mark-read-notification', {}, {source_id: element.Id});
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