import { IBank, IModel, IApiModel, IStorable } from "../interfaces/modelsInterfaces";
import { IFilter, ICollection } from "../interfaces/utilitiesInterfaces";
import { Collection } from "../utils/Collection";
import { KeyGenerator } from "../utils/KeyGenerator";
import { ErrorResponse } from "../utils/Response";
import { GET_MULTIPLE, GET_METHOD } from "../utils/Environment";

export class Bank implements IBank, IModel  {
    private active: boolean;
    private swiftBic: string;
    private iban: string;
    private lastUpdate: Date;
    private syncStatus: string;
    private name: string;
    private key: string;
    private total: number;
    private logo: string;
    protected apiObject: any;

    constructor(name?: string, total?: number, logo?: string, swift?: string, iban?: string, lastUpdate?: Date, syncStatus?: string, active?: boolean) {
        this.name = name || '';
        this.total = total || 0;
        this.logo = logo || '';
        this.key = KeyGenerator.generate(15);
        this.swiftBic = swift || '';
        this.iban = iban || '';
        this.lastUpdate = lastUpdate || new Date();
        this.syncStatus = syncStatus || '';
        this.active = false;
    }
    public get Active(): boolean {
        return this.active;
    }

    public set Active(value: boolean) {
        this.active = value;
    }

    public getActive(): boolean {
        return this.active;
    }

    public setActive(value: boolean): Bank {
        this.active = value;
        return this;
    }

    public get Iban(): string {
        return this.iban;
    }

    public set Iban(value: string) {
        this.iban = value;
    }

    public getIban(): string {
        return this.iban;
    }

    public setIban(value: string): Bank {
        this.iban = value;
        return this;
    }

    public get LastUpdate(): Date {
        return this.lastUpdate;
    }

    public set LastUpdate(value: Date) {
        this.lastUpdate = value;
    }

    public getLastUpdate(): Date {
        return this.lastUpdate;
    }

    public setLastUpdate(value: Date): Bank {
        this.lastUpdate = value;
        return this;
    }

    public get SyncStatus(): string {
        return this.syncStatus;
    }

    public set SyncStatus(value: string) {
        this.syncStatus = value;
    }

    public getSyncStatus(): string {
        return this.syncStatus;
    }

    public setSyncStatus(value: string): Bank {
        this.syncStatus = value;
        return this;
    }

    public get SwiftBic(): string {
        return this.swiftBic;
    }

    public set SwiftBic(value: string) {
        this.swiftBic = value;
    }

    public getSwiftBic(): string {
        return this.swiftBic;
    }

    public setSwiftBic(value: string): Bank {
        this.swiftBic = value;
        return this;
    }

    public get ApiObject(): any {
        return this.apiObject;
    }

    public set ApiObject(value: any) {
        this.apiObject = value;
    }

    public get Name(): string {
        return this.name;
    }

    public set Name(value: string) {
        this.name = value;
    }

    public getName(): string {
        return this.name;
    }

    public setName(value: string): Bank {
        this.name = value;
        return this;
    }

    public get Total(): number {
        return this.total;
    }

    public set Total(value: number) {
        this.total = value;
    }

    public getTotal(): number {
        return this.total;
    }

    public setTotal(value: number): Bank {
        this.total = value;
        return this;
    }

    public get Logo(): string {
        return this.logo;
    }

    public set Logo(value: string) {
        this.logo = value;
    }

    public getLogo(): string {
        return this.logo;
    }

    public setLogo(value: string): Bank {
        this.logo = value;
        return this;
    }

    public get Key(): string {
        return this.key;
    }

    public set Key(value: string) {
        this.key = value;
    }

    getKey(): string {
        return this.key;
    }

    getFilterableFields(): Map<string,any> {
        let map = new Map<string, any>();
        map.set('name', this.Name);
        map.set('active', this.Active);
        map.set('total', this.Total);
        map.set('swift', this.SwiftBic);
        map.set('iban', this.Iban);
        map.set('lastUpdate', this.LastUpdate);
        map.set('syncStatus', this.SyncStatus);
        return map;
    }

    getSortableFields(): Map<string,any> {
        let map = new Map<string, any>();
        map.set('name', this.Name);
        map.set('active', this.Active);
        map.set('total', this.Total);
        map.set('swift', this.SwiftBic);
        map.set('iban', this.Iban);
        map.set('lastUpdate', this.LastUpdate);
        map.set('syncStatus', this.SyncStatus);
        return map;
    }
}

export class ApiBank extends Bank implements IApiModel {
    getUrl(currentMethod: string): string[] {
        if(currentMethod == GET_MULTIPLE)
            return ['/ms/api/company/banks'];
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
        let bank = new Bank();
        bank.ApiObject = data;
        bank.Key = data.id
        bank.Iban = data.bank_account
        bank.Logo = ''
        bank.Name = data.alias ? data.alias : ''
        bank.Total = data.balance ? data.balance : 'N/D'
        bank.Active = data.active;
        return bank;
    }
}

export class StorableBank extends Bank implements IStorable<Bank> {
    getCollection(): ICollection<Bank> {
        return banks;
    }
    getLocalStorage(): string {
        return 'banks';
    }
}

export let banks: ICollection<Bank> = new Collection<Bank>();
export function setBanks(value: any) { banks = value; };