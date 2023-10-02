import { IEmployee, IModel, IApiModel, IStorable } from "../interfaces/modelsInterfaces";
import { IFilter, ICollection } from "../interfaces/utilitiesInterfaces";
import { Collection } from "../utils/Collection";
import { ErrorResponse } from "../utils/Response";

export class Employee implements IEmployee, IModel  {
    private name: string;
    private lastname: string;
    private document: string;
    private email: string;
    private phone: string;
    private naf: string;
    private active: boolean;
    private key: string;
    protected apiObject: any;

    public get ApiObject(): any {
        return this.apiObject;
    }

    public set ApiObject(value: any) {
        this.apiObject = value;
    }

    constructor(name?: string, lastname?: string, document?: string, email?: string, phone?: string, naf?: string, active?: boolean) {
        this.key = document || '';
        this.name = name || '';
        this.lastname = lastname || '';
        this.document = document || '';
        this.email = email || '';
        this.phone = phone || '';
        this.naf = naf || '';
        this.active = active || false;
    }
    getName(): string {
        return this.name
    }
    setName(value: unknown): IEmployee {
        this.name = value as string;
        return this
    }
    getLastname(): string {
        return this.lastname
    }
    setLastname(value: string): IEmployee {
        this.lastname = value;
        return this
    }
    getDocument(): string {
        return this.document
    }
    setDocument(value: string): IEmployee {
        this.document = value;
        return this
    }
    getEmail(): string {
        return this.email
    }
    setEmail(value: string): IEmployee {
        this.email = value;
        return this
    }
    getPhone(): string {
        return this.phone
    }
    setPhone(value: string): IEmployee {
        this.phone = value;
        return this
    }
    getNaf(): string {
        return this.naf
    }
    setNaf(value: string): IEmployee {
        this.naf = value;
        return this
    }
    getActive(): boolean {
        return this.active
    }
    setActive(value: boolean): IEmployee {
        this.active = value;
        return this
    }

    public get Name(): string {
        return this.name;
    }

    public set Name(value: string) {
        this.name = value;
    }

    public get Lastname(): string {
        return this.lastname;
    }

    public set Lastname(value: string) {
        this.lastname = value;
    }

    public get Document(): string {
        return this.document;
    }

    public set Document(value: string) {
        this.document = value;
    }

    public get Email(): string {
        return this.email;
    }

    public set Email(value: string) {
        this.email = value;
    }

    public get Phone(): string {
        return this.phone;
    }

    public set Phone(value: string) {
        this.phone = value;
    }

    public get Naf(): string {
        return this.naf;
    }

    public set Naf(value: string) {
        this.naf = value;
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
        map.set('lastname', this.lastname);
        map.set('document', this.document);
        map.set('email', this.email);
        map.set('phone', this.phone);
        map.set('naf', this.naf);
        map.set('active', this.active);
        return map;
    }

    getSortableFields(): Map<string, any> {
        let map = new Map<string, any>();
        map.set('name', this.name);
        map.set('lastname', this.lastname);
        map.set('document', this.document);
        map.set('email', this.email);
        map.set('phone', this.phone);
        map.set('naf', this.naf);
        map.set('active', this.active);
        return map;
    }
}

export class ApiEmployee extends Employee implements IApiModel {
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

export class StorableEmployee extends Employee implements IStorable<Employee> {
    getCollection(): ICollection<Employee> {
        return employees;
    }
    getLocalStorage(): string {
        return 'employees';
    }
}

export let employees: ICollection<Employee> = new Collection<Employee>();
export function setEmployees(value: any) { employees = value; };