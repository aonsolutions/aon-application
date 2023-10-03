import { ITaskHolder, IModel, IApiModel, IStorable } from "../interfaces/modelsInterfaces";
import { IFilter, ICollection } from "../interfaces/utilitiesInterfaces";
import { Collection } from "../utils/Collection";
import { KeyGenerator } from "../utils/KeyGenerator";
import { GET_MULTIPLE, GET_SINGLE, GET_METHOD } from "../utils/Environment";

export class TaskHolder implements ITaskHolder, IModel {
    protected id: string;
    protected name: string;
    protected key: string;
    protected apiObject: any;

    constructor(name?: string) {
        this.name = name || '';
        this.id = KeyGenerator.generate(15);
        this.key = this.id;
    }
    getName(): string {
        return this.name;
    }
    setName(value: string): ITaskHolder {
        this.name = value;
        return this
    }

    public get Id(): string {
        return this.id;
    }

    public set Id(value: string) {
        this.id = value;
    }

    public get Name(): string {
        return this.name;
    }

    public set Name(value: string) {
        this.name = value;
    }

    public get Key(): string {
        return this.key;
    }

    public set Key(value: string) {
        this.key = value;
    }

    public get ApiObject(): any {
        return this.apiObject;
    }

    public set ApiObject(value: any) {
        this.apiObject = value;
    }

    getKey(): string {
        return this.id;
    }
    getFilterableFields(): Map<string, any> {
        throw new Error("Method not implemented.");
    }
    getSortableFields(): Map<string, any> {
        throw new Error("Method not implemented.");
    }

}

export class ApiTaskHolder extends TaskHolder implements IApiModel {
    getUrl(currentMethod: string, filter?: IFilter | undefined): string[] {
        if(currentMethod == GET_MULTIPLE)
            return ['/ms/api/taskholder/list'];
        if(currentMethod == GET_SINGLE)
            return ['/ms/api/taskholder?id=' + filter?.fields?.get('id')];
        throw new Error("Method not implemented.");
    }
    getMethod(currentMethod: string, filter?: IFilter | undefined): string {
        if(currentMethod == GET_MULTIPLE || currentMethod == GET_SINGLE)
            return GET_METHOD;
        throw new Error("Method not implemented.");
    }
    parseDataToSend(data: any, currentMethod: string, filter?: IFilter | undefined) {
        throw new Error("Method not implemented.");
    }
    parseDataToReceive(data: any, currentMethod: string, filter?: IFilter | undefined) {
        let taskHolder = new TaskHolder();
        taskHolder.ApiObject = data;
        taskHolder.Id = data.id;
        taskHolder.Key = data.id;
        taskHolder.Name = data.name;
        return taskHolder;
    }
    localFilter(currentMethod?: string | undefined, filter?: IFilter | undefined): boolean {
        return true;
    }

}

export class StorableTaskHolder extends TaskHolder implements IStorable<TaskHolder> {
    getCollection(): ICollection<TaskHolder> {
        return taskHolders;
        throw new Error("Method not implemented.");
    }
    getLocalStorage(): string {
        return 'taskHolders';
        throw new Error("Method not implemented.");
    }
}

export let taskHolders: ICollection<TaskHolder> = new Collection<TaskHolder>();
export function setTaskHolders(value: any) { taskHolders = value; };