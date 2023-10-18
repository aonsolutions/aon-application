import { IMessage, IModel, TypeMessage, StatusMessage, IApiModel, IStorable } from "../interfaces/modelsInterfaces";
import { IFilter, ICollection } from "../interfaces/utilitiesInterfaces";
import { Collection } from "../utils/Collection";
import { KeyGenerator } from "../utils/KeyGenerator";
import { ErrorResponse } from "../utils/Response";
import { GET_MULTIPLE, GET_SINGLE, UPDATE_SINGLE, GET_METHOD, POST_METHOD } from "../utils/Environment";
import { TaskHolder } from "./TaskHolder";

export class Message implements IMessage, IModel  {
    private id: string;
    private name: string;
    private title: string;
    private description: string;
    private date: Date;
    private type: TypeMessage;
    private status: StatusMessage;
    private endDate: Date;
    private lastMessageChatOrigin: boolean;
    private key: string;
    private taskHolder : TaskHolder;
    protected apiObject: any;

    public get ApiObject(): any {
        return this.apiObject;
    }

    public set ApiObject(value: any) {
        this.apiObject = value;
    }

    constructor(name?: string, title?: string, description?: string, date?: Date, type?: TypeMessage, status?: StatusMessage, endDate?: Date, lastMessageChatOrigin?: boolean) {
        this.id = KeyGenerator.generate(15);
        this.name = name || '';
        this.title = title || '';
        this.description = description || '';
        this.date = date || new Date();
        this.type = type || TypeMessage.NULL;
        this.status = status || (type == 'consulta' ? StatusMessage.ABIERTA : type == 'tarea' ? StatusMessage.PENDIENTE : StatusMessage.NUEVA);
        this.endDate = endDate || new Date();
        this.key = this.id || '';
        this.taskHolder = new TaskHolder();
        this.lastMessageChatOrigin = false;
    }
    getName(): string {
        return this.name
    }
    setName(value: string): IMessage {
        this.name = value;
        return this
    }
    getTitle(): string {
        return this.title
    }
    setTitle(value: string): IMessage {
        this.title = value;
        return this
    }
    getDescription(): string {
        return this.description
    }
    setDescription(value: string): IMessage {
        this.description = value;
        return this
    }
    getDate(): Date {
        return this.date
    }
    setDate(value: Date): IMessage {
        this.date = value;
        return this
    }
    getType(): TypeMessage {
        return this.type
    }
    setType(value: TypeMessage): IMessage {
        this.type = value;
        return this
    }
    getStatus(): StatusMessage {
        return this.status
    }
    setStatus(value: StatusMessage): IMessage {
        this.status = value;
        return this
    }
    getEndDate(): Date {
        return this.endDate
    }
    setEndDate(value: Date): IMessage {
        this.endDate = value;
        return this
    }
    getLastMessageChatOrigin(): boolean {
        return this.lastMessageChatOrigin
    }
    setLastMessageChatOrigin(value: boolean): IMessage {
        this.lastMessageChatOrigin = value;
        return this
    }

    public get LastMessageChatOrigin() {
        return this.lastMessageChatOrigin;
    }

    public set LastMessageChatOrigin(value: boolean) {
        this.lastMessageChatOrigin = value;
    }

    public get Id(): string {
        return this.id;
    }

    public set Id(value: string) {
        this.id = value;
    }

    public get TaskHolder(): TaskHolder {
        return this.taskHolder;
    }

    public set TaskHolder(value: TaskHolder) {
        this.taskHolder = value;
    }

    public get Name(): string {
        return this.name;
    }

    public set Name(value: string) {
        this.name = value;
    }

    public get Title(): string {
        return this.title;
    }

    public set Title(value: string) {
        this.title = value;
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

    public get Type(): TypeMessage {
        return this.type;
    }

    public set Type(value: TypeMessage) {
        this.type = value;
    }

    public get Status(): StatusMessage {
        return this.status;
    }

    public set Status(value: StatusMessage) {
        this.status = value;
    }

    public get EndDate(): Date {
        return this.endDate;
    }

    public set EndDate(value: Date) {
        this.endDate = value;
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
        map.set('mame', this.name);
        map.set('title', this.title);
        map.set('description', this.description);
        map.set('date', this.date);
        map.set('type', this.type);
        map.set('status', this.status);
        map.set('endDate', this.endDate);
        return map;
    }

    getSortableFields(): Map<string, any> {
        let map = new Map<string, any>();
        map.set('mame', this.name);
        map.set('title', this.title);
        map.set('description', this.description);
        map.set('date', this.date);
        map.set('type', this.type);
        map.set('status', this.status);
        map.set('endDate', this.endDate);
        return map;
    }
}

export class ApiMessage extends Message implements IApiModel {
    getUrl(currentMethod: string, filter: IFilter): string[] {
        throw new Error("Method not implemented.");
    }

    getMethod(currentMethod: string, filter?: IFilter | undefined): string {
        throw new Error("Method not implemented.");
    }

    localFilter(): boolean {
        return true;
    }

    parseDataToSend(data: Message) {
        let object = data.ApiObject;
        let status;
        if(data.Type == TypeMessage.NOTIFICACION)
            status = data.Status == StatusMessage.VISTA ? 1 : 0;
        else
            status = data.Status == StatusMessage.ABIERTA || data.Status == StatusMessage.PENDIENTE ? 'pending' : 'deleted';
        object.status = status;
        return object;
    }

    parseDataToReceive(data: any) {
        let message = new Message();
        if(data.source && (data.source == 'task' || data.source == 'query')){
            let description = JSON.parse(data.description);
            message.ApiObject = data;
            message.Id = data.id + (data.source == 'query' ? ';' + TypeMessage.CONSULTA : ';' + TypeMessage.TAREA);
            message.Name = data.sender.id == localStorage.getItem('registry') ? data.task_holder.name : data.sender.name;
            message.Title = data.title ? data.title : '';
            message.Description = description.observation ? description.observation : '';
            message.Date = data.start_date ? new Date(data.start_date) : new Date();
            message.Type = data.source == 'query' ? TypeMessage.CONSULTA : TypeMessage.TAREA;
            if(data.source == 'task')
                message.Status = data.status && data.status == 'pending' ? StatusMessage.PENDIENTE : StatusMessage.REALIZADA;
            else
                message.Status = data.status && data.status == 'pending' ? StatusMessage.ABIERTA : StatusMessage.CERRADA;
            message.EndDate = new Date();
            message.Key = message.Id;
            return message;
        }else {
            message.ApiObject = data;
            message.Id = data.id + ';' + TypeMessage.NOTIFICACION;
            message.Name = data.source ? data.source : '';
            message.Title = data.title ? data.title : '';
            message.Description = data.body ? data.body : '';
            message.Date = data.date ? new Date(data.date) : new Date();
            message.Type = TypeMessage.NOTIFICACION;
            message.Status = data.status && data.status == 1 ? StatusMessage.VISTA : StatusMessage.NUEVA;
            message.EndDate = new Date();
            message.Key = message.Id;
            return message;
        }
        throw new ErrorResponse('0199')
    }
}

export class StorableMessage extends Message implements IStorable<Message> {
    getCollection(): ICollection<Message> {
        return messages;
    }
    getLocalStorage(): string {
        return 'messages';
    }
}

export let messages: ICollection<Message> = new Collection<Message>();
export function setMessages(value: any) { messages = value; };