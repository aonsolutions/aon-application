import { IMessageChat, IModel, IApiModel, IStorable } from "../interfaces/modelsInterfaces";
import { IFilter, ICollection } from "../interfaces/utilitiesInterfaces";
import { Collection } from "../utils/Collection";
import { KeyGenerator } from "../utils/KeyGenerator";
import { ErrorResponse } from "../utils/Response";
import { GET_MULTIPLE, GET_METHOD } from "../utils/Environment";

export class MessageChat implements IMessageChat, IModel  {
    private id: string;
    private idMessage: string;
    private name: string;
    private description: string;
    private date: Date;
    private type: string;
    private key: string;
    protected apiObject: any;

    public get ApiObject(): any {
        return this.apiObject;
    }

    public set ApiObject(value: any) {
        this.apiObject = value;
    }

    constructor(idMessage?: string, name?: string, description?: string, date?: Date, type?: string) {
        this.id = KeyGenerator.generate(15);
        this.idMessage = idMessage || '';
        this.name = name || '';
        this.description = description || '';
        this.date = date || new Date();
        this.type = type || '';
        this.key = this.id || '';
    }
    getIdMessage(): string {
        return this.idMessage;
    }
    setIdMessage(value: string): IMessageChat {
        this.idMessage = value;
        return this
    }
    getName(): string {
        return this.name;
    }
    setName(value: string): IMessageChat {
        this.name = value;
        return this
    }
    getDescription(): string {
        return this.description;
    }
    setDescription(value: string): IMessageChat {
        this.description = value;
        return this
    }
    getDate(): Date {
        return this.date;
    }
    setDate(value: Date): IMessageChat {
        this.date = value;
        return this
    }
    getType(): string {
        return this.type;
    }
    setType(value: string): IMessageChat {
        this.type = value;
        return this
    }

    public get Id(): string {
        return this.id;
    }

    public set Id(value: string) {
        this.id = value;
    }

    public get IdMessage(): string {
        return this.idMessage;
    }

    public set IdMessage(value: string) {
        this.idMessage = value;
    }

    public get Name(): string {
        return this.name;
    }

    public set Name(value: string) {
        this.name = value;
    }

    public get Description(): string {
        return this.description;
    }

    public set Description(value: string) {
        this.description = value;
    }

    public get Date(): Date {
        return this.date;
    }

    public set Date(value: Date) {
        this.date = value;
    }

    public get Type(): string {
        return this.type;
    }

    public set Type(value: string) {
        this.type = value;
    }

    public get Key() {
        return this.key;
    }

    public set Key(value: string){
        this.key = value;
    }

    getKey(): string {
        return this.id.toString();
    }

    getFilterableFields(): Map<string, any> {
        let map = new Map<string, any>();
        map.set('idmessage', this.idMessage);
        map.set('name', this.name);
        map.set('description', this.description);
        map.set('date', this.date);
        map.set('type', this.type);
        return map;
    }

    getSortableFields(): Map<string, any> {
        let map = new Map<string, any>();
        map.set('idmessage', this.idMessage);
        map.set('name', this.name);
        map.set('description', this.description);
        map.set('date', this.date);
        map.set('type', this.type);
        return map;
    }
}

export class ApiMessageChat extends MessageChat implements IApiModel {
    getUrl(currentMethod: string, filter: IFilter): string[] {
        if(currentMethod == GET_MULTIPLE && filter && filter.fields && filter.fields.has('idMessage'))
            return ['/ms/api/task/workflow?task='+filter.fields.get('idMessage')+'&domainId='+localStorage.getItem('domainId')+'&domainName='+localStorage.getItem('domainName')]
        throw new ErrorResponse('0199')
    }

    getMethod(currentMethod: string, filter?: IFilter | undefined): string {
        if(currentMethod == GET_MULTIPLE)
            return GET_METHOD;
        throw new ErrorResponse('0199')
    }

    localFilter(): boolean {
        return false;
    }

    parseDataToSend(data: any) {
        throw new ErrorResponse('0199')
    }

    parseDataToReceive(data: any) {
        let messageChat = new MessageChat();
        messageChat.ApiObject = data ? data : {};
        messageChat.Id = data.id ? data.id : ''
        messageChat.IdMessage = data.task ? data.task : ''
        messageChat.Key = data.id ? data.id : ''
        messageChat.Name = data.task_holder && data.task_holder.name ? data.task_holder.name : ''
        messageChat.Type =  data.task_holder &&  data.task_holder.id &&data.task_holder.id == localStorage.getItem('registry') ? 'send' : 'received';
        messageChat.Description = data.comment ? data.comment : ''
        messageChat.Date = data.modification_date ? data.modification_date : new Date();
        return messageChat;
        throw new ErrorResponse('0199')
    }
}

export class StorableMessageChat extends MessageChat implements IStorable<MessageChat> {
    getCollection(): ICollection<MessageChat> {
        return messageChats;
    }
    getLocalStorage(): string {
        return 'messageChats';
    }
}

export let messageChats: ICollection<MessageChat> = new Collection<MessageChat>();
export function setMessageChats(value: any) { messageChats = value; };