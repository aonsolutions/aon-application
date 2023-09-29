import { ICertificate, IModel, IApiModel, IStorable } from "../interfaces/modelsInterfaces";
import { IFilter, ICollection } from "../interfaces/utilitiesInterfaces";
import { Collection } from "../utils/Collection";

export class Certificate implements ICertificate, IModel {
    private name: string;
    private representationType: string;
    private expeditionDate: Date;
    private expirationDate: Date;
    private alias: string;
    private type: string;
    private tgss: boolean;
    private sepe: boolean;
    private aeat: boolean;
    private documentUser: string;
    private key: string;
    private isValid: boolean;
    protected apiObject: any;

    constructor(name?: string, representationType?: string, expeditionDate?: Date, expirationDate?: Date, alias?: string, type?: string, tgss?: boolean, sepe?: boolean, aeat?: boolean, documentUser?: string, isValid?: boolean) {
        this.name = name || '';
        this.representationType = representationType || '';
        this.expeditionDate = expeditionDate || new Date();
        this.expirationDate = expirationDate || new Date();
        this.alias = alias || '';
        this.type = type || '';
        this.tgss = tgss || false;
        this.sepe = sepe || false;
        this.aeat = aeat || false;
        this.documentUser = documentUser || '';
        this.isValid = isValid || false;
        this.key = documentUser || '';
    }
    getName(): string {
        return this.name
    }
    setName(value: string): ICertificate {
        this.name = value;
        return this
    }
    getRepresentationType(): string {
        return this.representationType
    }
    setRepresentationType(value: string): ICertificate {
        this.representationType = value;
        return this
    }
    getExpeditionDate(): Date {
        return this.expeditionDate
    }
    setExpeditionDate(value: Date): ICertificate {
        this.expeditionDate = value;
        return this
    }
    getExpirationDate(): Date {
        return this.expirationDate
    }
    setExpirationDate(value: Date): ICertificate {
        this.expirationDate = value;
        return this
    }
    getAlias(): string {
        return this.alias
    }
    setAlias(value: string): ICertificate {
        this.alias = value;
        return this
    }
    getType(): string {
        return this.type
    }
    setType(value: string): ICertificate {
        this.type = value;
        return this
    }
    getTgss(): boolean {
        return this.tgss
    }
    setTgss(value: boolean): ICertificate {
        this.tgss = value;
        return this
    }
    getSepe(): boolean {
        return this.sepe
    }
    setSepe(value: boolean): ICertificate {
        this.sepe = value;
        return this
    }
    getAeat(): boolean {
        return this.aeat
    }
    setAeat(value: boolean): ICertificate {
        this.aeat = value;
        return this
    }
    getDocumentUser(): string {
        return this.documentUser
    }
    setDocumentUser(value: string): ICertificate {
        this.documentUser = value;
        return this
    }

    public get ApiObject(): any {
        return this.apiObject;
    }

    public set ApiObject(value: any) {
        this.apiObject = value;
    }

    public get IsValid(): boolean{
        return this.isValid;
    }

    public set IsValid(value: boolean){
        this.isValid = value;
    }

    public get Name(): string {
        return this.name;
    }

    public set Name(value: string) {
        this.name = value;
    }

    public get RepresentationType(): string {
        return this.representationType;
    }

    public get ExpeditionDate(): Date {
        return this.expeditionDate;
    }

    public set ExpeditionDate(value: Date) {
        this.expeditionDate = value;
    }

    public get ExpirationDate(): Date {
        return this.expirationDate;
    }

    public set ExpirationDate(value: Date) {
        this.expirationDate = value;
    }

    public get Alias(): string {
        return this.alias;
    }

    public set Alias(value: string) {
        this.alias = value;
    }

    public get Type(): string {
        return this.type;
    }

    public set Type(value: string) {
        this.type = value;
    }

    public get Tgss(): boolean {
        return this.tgss;
    }

    public set Tgss(value: boolean) {
        this.tgss = value;
    }

    public get Sepe(): boolean {
        return this.sepe;
    }

    public set Sepe(value: boolean) {
        this.sepe = value;
    }

    public get Aeat(): boolean {
        return this.aeat;
    }

    public set Aeat(value: boolean) {
        this.aeat = value;
    }

    public get DocumentUser(): string {
        return this.documentUser;
    }

    public set DocumentUser(value: string) {
        this.documentUser = value;
    }

    public get Key(): string {
        return this.key;
    }

    public set Key(value: string) {
        this.key = value;
    }

    getKey(): string {
        return this.documentUser;
    }

    getFilterableFields(): Map<string,any> {
        let map = new Map<string, any>();
        map.set('name', this.Name);
        map.set('representationType', this.RepresentationType);
        map.set('expeditionDate', this.ExpeditionDate);
        map.set('expirationDate', this.ExpirationDate);
        map.set('alias', this.Alias);
        map.set('type', this.Type);
        map.set('tgss', this.Tgss);
        map.set('sepe', this.Sepe);
        map.set('aeat', this.Aeat);
        map.set('documentUser', this.DocumentUser);
        return map;
    }

    getSortableFields(): Map<string,any> {
        let map = new Map<string, any>();
        map.set('name', this.Name);
        map.set('representationType', this.RepresentationType);
        map.set('expeditionDate', this.ExpeditionDate);
        map.set('expirationDate', this.ExpirationDate);
        map.set('alias', this.Alias);
        map.set('type', this.Type);
        map.set('tgss', this.Tgss);
        map.set('sepe', this.Sepe);
        map.set('aeat', this.Aeat);
        map.set('documentUser', this.DocumentUser);
        return map;
    }

}

export class ApiCertificate extends Certificate implements IApiModel {
    getUrl(currentMethod: string, filter?: IFilter | undefined): string[] {
        throw new Error("Method not implemented.");
    }
    getMethod(currentMethod: string, filter?: IFilter | undefined): string {
        throw new Error("Method not implemented.");
    }
    parseDataToSend(data: any, currentMethod: string, filter?: IFilter | undefined) {
        throw new Error("Method not implemented.");
    }
    parseDataToReceive(data: any, currentMethod: string, filter?: IFilter | undefined) {
        throw new Error("Method not implemented.");
    }
    localFilter(currentMethod?: string | undefined, filter?: IFilter | undefined): boolean {
        throw new Error("Method not implemented.");
    }
}

export class StorableCertificate extends Certificate implements IStorable<Certificate> {
    getCollection(): ICollection<Certificate> {
        return certificates;
    }
    getLocalStorage(): string {
        return 'certificates';
    }
}

export let certificates: ICollection<Certificate> = new Collection<Certificate>();
export function setCertificates(value: any) { certificates = value; };