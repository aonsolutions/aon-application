import { IContract, IModel, IStorable } from "../interfaces/modelsInterfaces";
import { ICollection } from "../interfaces/utilitiesInterfaces";
import { Collection } from "../utils/Collection";

export class Contract implements IContract, IModel  {
    private name: string;
    private lastName: string;
    private type: string;
    private grossCost: number;
    private startDate: Date;
    private endDate?: Date;
    private workCenter: string;
    private active: boolean;
    private key: string;
    protected apiObject: any;

    public set ApiObject(value: any) {
        this.apiObject = value;
    }

    public get ApiObject(): any {
        return this.apiObject;
    }

    constructor(name?: string, lastName?: string, type?: string, grossCost?: number, startDate?: Date, endDate?: Date, workCenter?: string, active?: boolean) {
        this.key = name || '';
        this.name = name || '';
        this.lastName = lastName || '';
        this.type = type || '';
        this.grossCost = grossCost || 0;
        this.startDate = startDate || new Date();
        this.endDate = endDate || new Date();
        this.workCenter = workCenter || '';
        this.active = active || false;
    }
    getName(): string {
        return this.name
    }
    setName(value: string): IContract {
        this.name = value;
        return this
    }
    getLastName(): string {
        return this.lastName
    }
    setLastName(value: string): IContract {
        this.lastName = value;
        return this
    }
    getType(): string {
        return this.type
    }
    setType(value: string): IContract {
        this.type = value;
        return this
    }
    getGrossCost(): number {
        return this.grossCost
    }
    setGrossCost(value: number): IContract {
        this.grossCost = value;
        return this
    }
    getStartDate(): Date {
        return this.startDate
    }
    setStartDate(value: Date): IContract {
        this.startDate = value;
        return this
    }
    getEndDate(): Date {
        return this.endDate ? this.endDate : new Date()
    }
    setEndDate(value: Date): IContract {
        this.endDate = value;
        return this
    }
    getWorkCenter(): string {
        return this.workCenter
    }
    setWorkCenter(value: string): IContract {
        this.workCenter = value;
        return this
    }
    getActive(): boolean {
        return this.active
    }
    setActive(value: boolean): IContract {
        this.active = value;
        return this
    }


    public get Name(): string {
      return this.name;
    }

    public set Name(value: string) {
      this.name = value;
    }

    public get LastName(): string {
      return this.lastName;
    }

    public set LastName(value: string) {
      this.lastName = value;
    }

    public get Type(): string {
        return this.type;
    }

    public set Type(value: string) {
        this.type = value;
    }

    public get GrossCost(): number {
        return this.grossCost;
    }

    public set GrossCost(value: number) {
        this.grossCost = value;
    }

    public get StartDate(): Date {
        return this.startDate;
    }

    public set StartDate(value: Date) {
        this.startDate = value;
    }

    public get EndDate(): any {
        return this.endDate;
    }

    public set EndDate(value: any) {
        this.endDate = value;
    }

    public get WorkCenter(): string {
        return this.workCenter;
    }

    public set WorkCenter(value: string) {
        this.workCenter = value;
    }

    public get Active(): boolean {
        return this.active;
    }

    public set Active(value: boolean) {
        this.active = value;
    }

    public get Key() {
        return this.key;
    }

    public set Key(value: string){
        this.key = value;
    }

    getKey(): string {
        return this.key;
    }

    getFilterableFields(): Map<string, any> {
        let map = new Map<string, any>();
        map.set('name', this.name);
        map.set('lastName', this.lastName);
        map.set('type', this.type);
        map.set('grossCost', this.grossCost);
        map.set('startDate', this.startDate);
        map.set('endDate', this.endDate);
        map.set('workCenter', this.workCenter);
        map.set('active', this.active);
        return map;
    }

    getSortableFields(): Map<string, any> {
        let map = new Map<string, any>();
        map.set('name', this.name);
        map.set('lastName', this.lastName);
        map.set('type', this.type);
        map.set('grossCost', this.grossCost);
        map.set('startDate', this.startDate);
        map.set('endDate', this.endDate);
        map.set('workCenter', this.workCenter);
        map.set('active', this.active);
        return map;
    }

}

export class StorableContract extends Contract implements IStorable<Contract> {
    getCollection(): ICollection<Contract> {
        return contracts;
    }
    getLocalStorage(): string {
        return 'contracts';
    }
}

export let contracts: ICollection<Contract> = new Collection<Contract>();
export function setContracts(value: any) { contracts = value; };