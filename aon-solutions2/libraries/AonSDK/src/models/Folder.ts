import { IFolder, IModel, IApiModel, IStorable } from "../interfaces/modelsInterfaces";
import { IFilter, ICollection } from "../interfaces/utilitiesInterfaces";
import { Collection } from "../utils/Collection";
import { GET_MULTIPLE, GET_METHOD } from "../utils/constants";
import { ErrorResponse } from "../utils/Response";

export class Folder implements IFolder, IModel  {
    private name: string;
    private path: string;
    private parent: string;
    private key: string;
    protected apiObject: any;

    public get ApiObject(): any {
        return this.apiObject;
    }

    public set ApiObject(value: any) {
        this.apiObject = value;
    }

    constructor(name?: string, parent?: string) {
        this.name = name || '';
        this.path = parent?.toLocaleLowerCase().split(' ').join('_') + '/' + name?.toLocaleLowerCase().split(' ').join('_') || '';
        this.parent = parent || '';
        this.key = this.path || '';
    }

    public get Name(): string {
        return this.name;
    }

    public set Name(value: string) {
        this.name = value;
    }

    public get Path(): string {
        return this.path;
    }

    public set Path(value: string) {
        this.path = value;
    }

    public get Parent(): string {
        return this.parent;
    }

    public set Parent(value: string) {
        this.parent = value;
    }

    public get Key(): string {
        return this.key;
    }

    public set Key(value: string) {
        this.key = value;
    }

    getKey(): string {
        return this.path;
    }

    getFilterableFields(): Map<string,any> {
        let map = new Map<string, any>();
        map.set('name', this.Name);
        map.set('parent', this.Parent);
        return map;
    }

    getSortableFields(): Map<string,any> {
        let map = new Map<string, any>();
        map.set('name', this.Name);
        map.set('parent', this.Parent);
        return map;
    }
}

export class ApiFolder extends Folder implements IApiModel {
    getUrl(currentMethod: string, filter: IFilter): string[] {
        let urls: string [] = [];
        if(currentMethod == GET_MULTIPLE){
            return ['/ms/api/contract']
        }
        throw new ErrorResponse('0199')
    }

    getMethod(currentMethod: string, filter?: IFilter | undefined): string {
        if(currentMethod == GET_MULTIPLE)
            return GET_METHOD;
        throw new ErrorResponse('0199')
    }

    localFilter(): boolean {
        return true;
    }

    parseDataToSend(data: any) {
        throw new ErrorResponse('0199')
    }

    parseDataToReceive(data: any) {
        let folder = new Folder();
        folder.ApiObject = data;
        folder.Key = data.ipf ? data.ipf : '';
        folder.Name = data.name ? data.name : '';
        folder.Parent = '/laboral';
        folder.Path = '/laboral/' + folder.Key;
        return folder;
        throw new ErrorResponse('0199')
    }
}

export class StorableFolder extends Folder implements IStorable<Folder> {
    getCollection(): ICollection<Folder> {
        return folders;
    }
    getLocalStorage(): string {
        return 'folders';
    }
}

export let folders: ICollection<Folder> = new Collection<Folder>();
export function setFolders(value: any) { folders = value; };
export let api: ICollection<Folder> = new Collection<Folder>();
export function setApi(value: any) { api = value; };
export let apiFolders = folders.slice(0,5);