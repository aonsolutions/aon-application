import { IDocument, IModel, IDocumentTag, IApiModel, MainFolders, IStorable } from "../interfaces/modelsInterfaces";
import { ICollection, IFilter } from "../interfaces/utilitiesInterfaces";
import { Collection } from "../utils/Collection";
import { KeyGenerator } from "../utils/KeyGenerator";
import { ErrorResponse } from "../utils/Response";
import { GET_MULTIPLE, GET_METHOD } from "../utils/Environment";
import { DocumentTag } from "./DocumentTag";

export class Document implements IDocument, IModel {
    private file: string;
    private fileName: string;
    private fileSize: number;
    private fileType: string;
    private path: string;
    private date: Date;
    private key: string;
    private id: string;
    private tag: ICollection<IDocumentTag>;
    protected apiObject: any;

    public get ApiObject(): any {
        return this.apiObject;
    }

    public set ApiObject(value: any) {
        this.apiObject = value;
    }

    constructor(file?: string, fileName?: string, fileSize?: number, fileType?: string, date?: Date, path?: string, tag?: Collection<DocumentTag>, id?: string) {
        this.file = file || '';
        this.fileName = fileName || '';
        this.fileSize = fileSize || 0;
        this.fileType = fileType || '';
        this.path =  path || '';
        this.date = date || new Date();
        this.key = path && fileName ? path + '/' + fileName : '';
        this.id = KeyGenerator.generate(15);
        this.apiObject = '';
        this.tag = tag || new Collection<IDocumentTag>();
    }
    getFile(): string {
        return this.file
    }
    setFile(value: string): IDocument {
        this.file = value;
        return this
    }
    getFileName(): string {
        return this.fileName
    }
    setFileName(value: string): IDocument {
        this.fileName = value;
        return this
    }
    getFileSize(): number {
        return this.fileSize
    }
    setFileSize(value: number): IDocument {
        this.fileSize = value;
        return this
    }
    getFileType(): string {
        return this.fileType
    }
    setFileType(value: string): IDocument {
        this.fileType = value;
        return this
    }
    getPath(): string {
        return this.path
    }
    setPath(value: string): IDocument {
        this.path = value;
        return this
    }
    getDate(): Date {
        return this.date
    }
    setDate(value: Date): IDocument {
        this.date = value;
        return this
    }
    getTag(): ICollection<IDocumentTag> {
        return this.tag
    }
    setTag(value: ICollection<IDocumentTag>): IDocument {
        this.tag = value;
        return this
    }

    public get Tag(): ICollection<IDocumentTag> {
        return this.tag;
    }

    public set Tag(value: ICollection<IDocumentTag>) {
        this.tag = value;
    }

    public get FileName(): string {
        return this.fileName;
    }

    public get FileSize(): number {
        return this.fileSize;
    }

    public get FileType(): string {
        return this.fileType;
    }

    public get Date(): Date {
        return this.date;
    }

    public get File(): string {
        return this.file;
    }

    public get Path(): string {
        return this.path;
    }

    public set File(file: string) {
        this.file = file;
    }

    public set FileName(fileName: string) {
        this.fileName = fileName;
    }

    public set FileSize(fileSize: number) {
        this.fileSize = fileSize;
    }

    public set FileType(fileType: string) {
        this.fileType = fileType;
    }

    public set Date(date: Date) {
        this.date = date;
    }

    public set Path(path: string) {
        this.path = path;
    }

    public get Key(): string {
        return this.key;
    }

    public set Key(key: string) {
        this.key = key;
    }

    public get Id(): string {
        return this.id;
    }

    public set Id(id: string) {
        this.id = id;
    }

    getKey(): string {
        return this.path + '/' + this.id;
    }

    getFilterableFields(): Map<string,any> {
        let map = new Map<string, any>();
        map.set('filename', this.FileName);
        map.set('filesize', this.FileSize);
        map.set('filetype', this.FileType);
        map.set('path', this.Path);
        // if(this.tag.size() > 0)
        //     this.tag.forEach(tag => {
        //         map.set('tag', tag.Name);
        //     })
        return map;
    }

    getSortableFields(): Map<string,any> {
        let map = new Map<string, any>();
        map.set('filename', this.FileName);
        map.set('filesize', this.FileSize);
        map.set('filetype', this.FileType);
        map.set('path', this.Path);
        return map;
    }
}

export class ApiDocument extends Document implements IApiModel {

    getUrl(currentMethod: string, filter: IFilter): string[] {
        // a contabilizar, contabilizado, papelera - inbox, rejected, draft
        if(currentMethod == GET_MULTIPLE){
            if(filter && filter.fields && filter.fields?.has('path') && filter.fields.get('path')[0].toLowerCase() == MainFolders.ACONTABILIZAR)
                return ['/ms/api/invoice?status=inbox', '/ms/api/documental/files?type=all&page=' + (filter && filter.pageNum ? filter.pageNum : '1') + '&per_page=' + (filter && filter.pageItems ? filter.pageItems : '30') + '&domain=' + localStorage.getItem('domainId')];
            if(filter && filter.fields && filter.fields?.has('path') && filter.fields.get('path')[0].toLowerCase() == MainFolders.CONTABILIZADO)
                return ['/ms/api/invoice?status=inbox'];
                // return ['/ms/api/invoice?status=rejected'];
            if(filter && filter.fields && filter.fields?.has('path') && filter.fields.get('path')[0].toLowerCase() == MainFolders.FISCAL)
                return ['/ms/api/fiscal/models'];
            if(filter && filter.fields && filter.fields?.has('path') && filter.fields.get('path')[0].toLowerCase() == MainFolders.PAPELERA)
                return ['/ms/api/invoice?status=draft'];
            if(filter && filter.fields && filter.fields?.has('path') && filter.fields.get('path')[0].toLowerCase().includes(MainFolders.LABORAL))
                throw new ErrorResponse('0199')
                // return ['/ms/api/contract/enterprise/salaries?document=' + filter.fields.get('path')[0].split('/')[2]]
        }
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

    parseDataToReceive(data: any, currentMethod:string, filter: IFilter) {
        let document = new Document()
        let path = filter.fields?.get('path')[0];
        if(path.indexOf(MainFolders.LABORAL) !== -1){
            document.ApiObject = data;
            document.File = '/ms/api/contract/salary/pdf?salaryId='+data.id+'&enterpriseId='+localStorage.getItem('enterpriseId')+'&type=SALARY'
            document.FileName = data.employeeName;
            document.FileSize = 0
            document.FileType = 'application/pdf'
            document.Date = data.startDate
            document.Path = filter.fields?.get('path')
            document.Key = data.id ? data.id : ''
            document.Id = data.id ? data.id : ''
        }else if(path == MainFolders.ACONTABILIZAR || path == MainFolders.PAPELERA){
            document.ApiObject = data;
            document.File = data.file && data.file.path ? '/' + data.file.path : '';
            document.FileName = data.name ? data.name : '';
            document.FileSize = 0;
            document.FileType = data.file && data.file.content_type ? data.file.content_type : '';
            document.Date = data.date ? data.date : new Date();
            document.Path = filter.fields?.get('path')
            document.Key = data.id ? data.id : '';
            document.Id = data.id ? data.id : '';
        }else if(path == MainFolders.CONTABILIZADO){
            document.ApiObject = data;
            document.FileType = 'application/pdf'
            document.File = data.id;
            document.Date = data.date;
            document.Path = filter.fields?.get('path');
        }else if (path == MainFolders.FISCAL){
            document.ApiObject = data;
            // TO DO => que es source? de donde lo obtiene?
            document.File = '/ms/api/attach?attachType=data&file=true&source_id='+data.id+'&source=9'
            document.FileName = data.model + '-' + data.year + '-' + data.period
            document.FileSize = 0
            document.FileType = 'application/pdf'
            document.Date = new Date(data.year + "-" + data.period.replace('T','')*3 + "-1");
            document.Path = filter.fields?.get('path')
            document.Key = data.id ? data.id : '';
            document.Id = data.id ? data.id : '';
        }
        return document;
    }

}

export class StorableDocument extends Document implements IStorable<Document> {
    getCollection(): ICollection<Document> {
        return documents;
    }
    getLocalStorage(): string {
        return 'documents';
    }
}

export let documents: ICollection<Document> = new Collection<Document>();
export function setDocuments(value: any) { documents = value; };