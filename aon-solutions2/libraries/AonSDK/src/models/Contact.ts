import { IContact, IModel, IPaymentMethod, IInvoiceTransactionType, IStorable, IApiModel } from "../interfaces/modelsInterfaces";
import { ICollection, IFilter } from "../interfaces/utilitiesInterfaces";
import { KeyGenerator } from "../utils/KeyGenerator";
import { PaymentMethod } from "./PaymenMethod";
import { InvoiceTransactionType } from "./InvoiceTransactionType";
import { Collection } from "../utils/Collection";

export class Contact implements IContact, IModel {
    private key: string;
    private apiObject: any;
    private name: string;
    private comercialName: string;
    private country: string;
    private document: string;
    private address: string;
    private email: string;
    private phone: string;
    private web: string;
    private bankAccount: string;
    private paymentMethod: IPaymentMethod;
    private transactionType: IInvoiceTransactionType;
    private irpf: boolean;
    private re: boolean;

    constructor(name?: string, comercialName?: string, country?: string, document?: string, address?: string, email?: string, phone?: string, web?: string, bankAccount?: string, paymentMethod?: IPaymentMethod, transactionType?: IInvoiceTransactionType, irpf?: boolean, re?: boolean) {
        this.key = KeyGenerator.generate(15);
        this.apiObject = {};
        this.name = name || '';
        this.comercialName = comercialName || '';
        this.country = country || '';
        this.document = document || '';
        this.address = address || '';
        this.email = email || '';
        this.phone = phone || '';
        this.web = web || '';
        this.bankAccount = bankAccount || '';
        this.paymentMethod = paymentMethod || new PaymentMethod();
        this.transactionType = transactionType || new InvoiceTransactionType();
        this.irpf = irpf || false;
        this.re = re || false;
    }
    getName(): string {
        return this.name
    }
    setName(value: string): IContact {
        this.name = value;
        return this
    }
    getComercialName(): string {
        return this.comercialName
    }
    setComercialName(value: string): IContact {
        this.comercialName = value;
        return this
    }
    getCountry(): string {
        return this.country
    }
    setCountry(value: string): IContact {
        this.country = value;
        return this
    }
    getDocument(): string {
        return this.document
    }
    setDocument(value: string): IContact {
        this.document = value;
        return this
    }
    getAddress(): string {
        return this.address
    }
    setAddress(value: string): IContact {
        this.address = value;
        return this
    }
    getEmail(): string {
        return this.email
    }
    setEmail(value: string): IContact {
        this.email = value;
        return this
    }
    getPhone(): string {
        return this.phone
    }
    setPhone(value: string): IContact {
        this.phone = value;
        return this
    }
    getWeb(): string {
        return this.web
    }
    setWeb(value: string): IContact {
        this.web = value;
        return this
    }
    getBankAccount(): string {
        return this.bankAccount
    }
    setBankAccount(value: string): IContact {
        this.bankAccount = value;
        return this
    }
    getPaymentMethod(): IPaymentMethod {
        return this.paymentMethod
    }
    setPaymentMethod(value: IPaymentMethod): IContact {
        this.paymentMethod = value;
        return this
    }
    getTransactionType(): IInvoiceTransactionType {
        return this.transactionType
    }
    setTransactionType(value: IInvoiceTransactionType): IContact {
        this.transactionType = value;
        return this
    }
    getIRPF(): boolean {
        return this.irpf
    }
    setIRPF(value: boolean): IContact {
        this.irpf = value;
        return this
    }
    getRE(): boolean {
        return this.re
    }
    setRE(value: boolean): IContact {
        this.re = value;
        return this
    }

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

    public get ComercialName(): string {
        return this.comercialName;
    }

    public set ComercialName(value: string) {
        this.comercialName = value;
    }

    public get Country(): string {
        return this.country;
    }

    public set Country(value: string) {
        this.country = value;
    }

    public get Document(): string {
        return this.document;
    }

    public set Document(value: string) {
        this.document = value;
    }

    public get Address(): string {
        return this.address;
    }

    public set Address(value: string) {
        this.address = value;
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

    public get Web(): string {
        return this.web;
    }

    public set Web(value: string) {
        this.web = value;
    }

    public get BankAccount(): string {
        return this.bankAccount;
    }

    public set BankAccount(value: string) {
        this.bankAccount = value;
    }

    public get PaymentMethod(): IPaymentMethod {
        return this.paymentMethod;
    }

    public set PaymentMethod(value: IPaymentMethod) {
        this.paymentMethod = value;
    }

    public get TransactionType(): IInvoiceTransactionType {
        return this.transactionType;
    }

    public set TransactionType(value: IInvoiceTransactionType) {
        this.transactionType = value;
    }

    public get IRPF(): boolean {
        return this.irpf;
    }

    public set IRPF(value: boolean) {
        this.irpf = value;
    }

    public get RE(): boolean {
        return this.re;
    }

    public set RE(value: boolean) {
        this.re = value;
    }

    getKey(): string {
        return this.key;
    }
    getFilterableFields(): Map<string, any> {
        let map = new Map<string, any>();
        map.set('name', this.name);
        map.set('comercialName', this.comercialName);
        map.set('country', this.country);
        map.set('document', this.document);
        map.set('address', this.address);
        map.set('email', this.email);
        map.set('phone', this.phone);
        map.set('web', this.web);
        return map;
    }
    getSortableFields(): Map<string, any> {
        let map = new Map<string, any>();
        map.set('name', this.name);
        map.set('comercialName', this.comercialName);
        map.set('country', this.country);
        map.set('document', this.document);
        map.set('address', this.address);
        map.set('email', this.email);
        map.set('phone', this.phone);
        map.set('web', this.web);
        return map;
    }

}

export class StorableContact implements IStorable<Contact> {
    getCollection(): ICollection<Contact> {
        return contacts;
    }
    getLocalStorage(): string {
        return 'contacts';
    }
}

export class ApiContact implements IApiModel {
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
        throw new Error("Method not implemented.");
    }
}

export let contacts: ICollection<Contact> = new Collection<Contact>();
export function setContacts(value: any) { contacts = value; };