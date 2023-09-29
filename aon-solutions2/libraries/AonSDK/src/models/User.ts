import { IUser, IModel, IApiModel, IStorable } from "../interfaces/modelsInterfaces";
import { IFilter, ICollection } from "../interfaces/utilitiesInterfaces";
import { Collection } from "../utils/Collection";
import { ErrorResponse } from "../utils/Response";

export class User implements IUser, IModel  {
    private name: string;
    private lastname: string;
    private document: string;
    private email: string;
    private password: string;
    private phone: string;
    private active: boolean;
    private key: string;
    private enterprises: string [];
    protected apiObject: any;

    public get ApiObject(): any {
        return this.apiObject;
    }

    public set ApiObject(value: any) {
        this.apiObject = value;
    }

    constructor(name?: string, lastname?: string, document?: string, email?: string, password?: string, phone?: string, active?: boolean, enterprises?: string[]) {
        this.name = name || '';
        this.lastname = lastname || '';
        this.document = document || '';
        this.email = email || '';
        this.password = password || '';
        this.phone = phone || '';
        this.active = active || true;
        this.enterprises = enterprises || [];
        this.key = document || '';
    }
    getName(): string {
        return this.name;
    }
    setName(value: string): IUser {
        this.name = value;
        return this
    }
    getLastname(): string {
        return this.lastname;
    }
    setLastname(value: string): IUser {
        this.lastname = value;
        return this
    }
    getDocument(): string {
        return this.document;
    }
    setDocument(value: string): IUser {
        this.document = value;
        return this
    }
    getEmail(): string {
        return this.email;
    }
    setEmail(value: string): IUser {
        this.email = value;
        return this
    }
    getPassword(): string {
        return this.password;
    }
    setPassword(value: string): IUser {
        this.password = value;
        return this
    }
    getPhone(): string {
        return this.phone;
    }
    setPhone(value: string): IUser {
        this.phone = value;
        return this
    }
    getActive(): boolean {
        return this.active
    }
    setActive(value: boolean): IUser {
        this.active = value;
        return this
    }
    getEnterprises(): string[] {
        return this.enterprises
    }
    setEnterprises(value: string[]): IUser {
        this.enterprises = value;
        return this
    }

    public get Enterprises(): string[] {
        return this.enterprises;
    }

    public set Enterprises(value: string[]) {
        this.enterprises = value;
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

    public get Password(): string {
        return this.password;
    }

    public set Password(value: string) {
        this.password = value;
    }

    public get Phone(): string {
        return this.phone;
    }

    public set Phone(value: string) {
        this.phone = value;
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
        map.set('password', this.password);
        map.set('phone', this.phone);
        map.set('active', this.active);
        return map;
    }

    getSortableFields(): Map<string, any> {
        let map = new Map<string, any>();
        map.set('name', this.name);
        map.set('lastname', this.lastname);
        map.set('document', this.document);
        map.set('email', this.email);
        map.set('password', this.password);
        map.set('phone', this.phone);
        map.set('active', this.active);
        return map;
    }
}

export class ApiUser extends User implements IApiModel {
    getUrl(currentMethod: string, filter?: IFilter | undefined): string[] {
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

export class StorableUser extends User implements IStorable<User> {
    getCollection(): ICollection<User> {
        return users;
    }
    getLocalStorage(): string {
        return 'users';
    }
}

export let users: ICollection<User> = new Collection<User>();
export function setUsers(value: any) { users = value; };