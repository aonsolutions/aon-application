import { IDocumentNote, IModel, IApiModel, IStorable } from "../interfaces/modelsInterfaces";
import { IFilter, ICollection } from "../interfaces/utilitiesInterfaces";
import { ErrorResponse } from "../utils/Response";
import { Collection } from "../utils/Collection";

export class DocumentNote implements IDocumentNote, IModel  {
    private text: string;
    private path: string;
    private key: string;
    protected apiObject: any;

    public get ApiObject(): any {
        return this.apiObject;
    }

    public set ApiObject(value: any) {
        this.apiObject = value;
    }

    constructor(text?: string, path?: string) {
        this.text = text || '';
        this.path = path || '';
        this.key = path || '';
    }

    public get Text(): string {
        return this.text;
    }

    public set Text(value: string) {
        this.text = value;
    }

    public get Path(): string {
        return this.path;
    }

    public set Path(value: string) {
        this.path = value;
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
        map.set('text', this.Text);
        return map;
    }

    getSortableFields(): Map<string,any> {
        let map = new Map<string, any>();
        map.set('text', this.Text);
        return map;
    }
}

export class ApiDocumentNote extends DocumentNote implements IApiModel {
    getUrl(currentMethod: string): string[] {
        throw new ErrorResponse('0199')
    }
    getMethod(currentMethod: string, filter?: IFilter | undefined): string {
        throw new ErrorResponse('0199')
    }
    localFilter(): boolean {
        return false;
    }
    parseDataToSend(data: any) {
        throw new ErrorResponse('0199')
    }
    parseDataToReceive(data: any) {
        throw new ErrorResponse('0199')
    }
}

export class StorableDocumentNote extends DocumentNote implements IStorable<DocumentNote> {
    getCollection(): ICollection<DocumentNote> {
        return documentNotes;
    }
    getLocalStorage(): string {
        return 'documentNotes';
    }
}

export let documentNotes: ICollection<DocumentNote> = new Collection<DocumentNote>();
export function setDocumentNotes(value: any) { documentNotes = value; };