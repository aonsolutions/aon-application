import { IDocumentTag, IModel, IStorable, IApiModel } from "../interfaces/modelsInterfaces";
import { ICollection, IFilter } from "../interfaces/utilitiesInterfaces";
import { Collection } from "../utils/Collection";
import { KeyGenerator } from "../utils/KeyGenerator";
import { ErrorResponse } from "../utils/Response";
import { GET_MULTIPLE, GET_METHOD, CREATE_SINGLE, POST_METHOD, UPDATE_SINGLE } from "../utils/Environment";

export class DocumentTag implements IDocumentTag, IModel {
    private key: string;
    private name: string;
    private apiObject: any;

    public get ApiObject(): any {
        return this.apiObject;
    }

    public set ApiObject(value: any) {
        this.apiObject = value;
    }

    public get Key() {
        return this.key;
    }

    public set Key(value: string) {
        this.key = value;
    }

    public get Name(): string {
        return this.name;
    }

    public set Name(value: string) {
        this.name = value;
    }

    constructor(name?: string) {
        this.name = name || '';
        this.key = KeyGenerator.generate(15);
        this.apiObject = {};
    }
    getName(): string {
        return this.name;
    }
    setName(value: string): IDocumentTag {
        this.name = value;
        return this
    }

    getKey(): string {
        return this.key;
    }

    getFilterableFields(): Map<string, any> {
        let map = new Map<string, any>();
        map.set('name', this.name);
        return map;
    }

    getSortableFields(): Map<string, any> {
        let map = new Map<string, any>();
        map.set('name', this.name);
        return map;
    }
}

export class StorableDocumentTag extends DocumentTag implements IStorable<DocumentTag> {
    getCollection(): ICollection<DocumentTag> {
        return documentTags;
    }
    getLocalStorage(): string {
        return 'documentTags';
    }
}

export class ApiDocumentTag extends DocumentTag implements IApiModel {

    getMethod(currentMethod: string, filter?: IFilter | undefined): string {
        if(currentMethod == GET_MULTIPLE) return GET_METHOD;
        if(currentMethod == CREATE_SINGLE) return POST_METHOD;
        if(currentMethod == UPDATE_SINGLE) return POST_METHOD;
        throw new Error("Method not implemented.");
    }

    parseDataToSend(data: any, currentMethod: string, filter?: IFilter | undefined) {
        if(currentMethod == CREATE_SINGLE)
            return {'name': data.Name};
        if(currentMethod == UPDATE_SINGLE){
            data.ApiObject.name = data.name;
            return data.ApiObject;
        }
        throw new Error("Method not implemented.");
    }

    parseDataToReceive(data: any, currentMethod: string, filter?: IFilter | undefined) {
        let tag = new DocumentTag();
        tag.Name = data.name;
        tag.ApiObject = data;
        tag.Key = data.id;
        return tag;
        throw new Error("Method not implemented.");
    }

    localFilter(currentMethod?: string | undefined, filter?: IFilter | undefined): boolean {
        return true;
    }

    getUrl(currentMethod: string, filter?: IFilter | undefined): string[] {
        if(currentMethod == GET_MULTIPLE){
            return ['/ms/api/attachment/' + localStorage.getItem('domainName') + '/' + localStorage.getItem('login') + '/tag?domain=' + localStorage.getItem('domainId')];
        }
        if(currentMethod == CREATE_SINGLE){
            return ['/ms/api/attachment/' + localStorage.getItem('domainName') + '/' + localStorage.getItem('login') + '/tag/create'];
        }
        if(currentMethod == UPDATE_SINGLE){
            return ['/ms/api/attachment/' + localStorage.getItem('domainName') + '/' + localStorage.getItem('login') + '/tag/edit/' + filter?.fields?.get('id')];
        }
        throw new ErrorResponse('0199')
    }

}

export let documentTags: ICollection<DocumentTag> = new Collection<DocumentTag>();
export function setDocumentTags(value: any) { documentTags = value; };