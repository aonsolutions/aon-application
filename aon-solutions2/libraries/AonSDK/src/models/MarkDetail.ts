import { IMarkDetail, IModel, IStorable, IApiModel } from "../interfaces/modelsInterfaces";
import { ICollection, IFilter } from "../interfaces/utilitiesInterfaces";
import { Collection } from "../utils/Collection";
import { KeyGenerator } from "../utils/KeyGenerator";

export class MarkDetail implements IMarkDetail, IModel {
    private key: string;
    private apiObject: any;
    private id: string;
    private lastDate: Date;
    private lastModification: Date;
    private status: string;
    private location: any;

    public get Key(){
        return this.key;
    }

    public set Key(value: string){
        this.key = value;
    }

    public get ApiObject(){
        return this.apiObject;
    }

    public set ApiObject(value: any){
        this.apiObject = value;
    }

    public get Id(){
        return this.id;
    }

    public set Id(value: string){
        this.id = value;
    }

    public get LastDate(){
        return this.lastDate;
    }

    public set LastDate(value: Date){
        this.lastDate = value;
    }

    public get LastModification(){
        return this.lastModification;
    }

    public set LastModification(value: Date){
        this.lastModification = value;
    }

    public get Status(){
        return this.status;
    }

    public set Status(value: string){
        this.status = value;
    }

    public get Location(){
        return this.location;
    }

    public set Location(value: any){
        this.location = value;
    }

    constructor(id?: string, lastDate?: Date, lastModification?: Date, status?: string, location?: any) {
        this.id = id || '';
        this.lastDate = lastDate || new Date();
        this.lastModification = lastModification || new Date();
        this.status = status || '';
        this.location = location || {};
        this.key = KeyGenerator.generate(15);
    }

    getKey(): string {
        return this.key;
    }

    getFilterableFields(): Map<string, any> {
        let map = new Map<string, any>();
        map.set('id', this.id);
        map.set('lastDate', this.lastDate);
        map.set('lastModification', this.lastModification);
        map.set('status', this.status);
        map.set('location', this.location);
        return map;
    }

    getSortableFields(): Map<string, any> {
        let map = new Map<string, any>();
        map.set('id', this.id);
        map.set('lastDate', this.lastDate);
        map.set('lastModification', this.lastModification);
        map.set('status', this.status);
        map.set('location', this.location);
        return map;
    }

}

export class StorableMarkDetail extends MarkDetail implements IStorable<MarkDetail> {
    getCollection(): ICollection<MarkDetail> {
        return marksDetails;
    }
    getLocalStorage(): string {
        return 'marksDetails';
    }
}

export class ApiMarkDetail extends MarkDetail implements IApiModel {
    getUrl(currentMethod: string, filter?: IFilter | undefined): string[] {
        throw new Error("Method not implemented.");
    }
    getMethod(currentMethod: string, filter?: IFilter | undefined): string {
        throw new Error("Method not implemented.");
    }
    parseDataToSend(data: any, currentMethod?: string | undefined, filter?: IFilter | undefined) {
        throw new Error("Method not implemented.");
    }
    parseDataToReceive(data: any, currentMethod?: string | undefined, filter?: IFilter | undefined) {
        throw new Error("Method not implemented.");
    }
    localFilter(currentMethod?: string | undefined, filter?: IFilter | undefined): boolean {
        return false;
    }
}

export let marksDetails: ICollection<MarkDetail> = new Collection<MarkDetail>();
export function setMarksDetails(value: any) { marksDetails = value; };