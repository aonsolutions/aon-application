import { IInvoice, IModel, IInvoiceSerie, IContact, IInvoiceCategory, IInvoiceLine, IInvoiceTransactionType, IInvoiceActivity, ITax, IIRPF, IInvoiceExpirationLine, InvoiceType, InvoiceStatus, IStorable, IApiModel } from "../interfaces/modelsInterfaces";
import { ICollection, IFilter } from "../interfaces/utilitiesInterfaces";
import { Collection } from "../utils/Collection";
import { KeyGenerator } from "../utils/KeyGenerator";
import { Contact } from "./Contact";
import { IRPF } from "./IRPF";
import { InvoiceActivity } from "./InvoiceActivity";
import { InvoiceCategory } from "./InvoiceCategory";
import { InvoiceSerie } from "./InvoiceSerie";
import { Tax } from "./Tax";
import { InvoiceTransactionType } from "./InvoiceTransactionType";

export class Invoice implements IInvoice, IModel {
    key: string;
    apiObject: any;
    serie: IInvoiceSerie;
    invoiceNumber: string;
    date: Date;
    totalAmount: number;
    contact: IContact;
    category: IInvoiceCategory;
    lines: ICollection<IInvoiceLine>;
    transactionType: IInvoiceTransactionType;
    activity: IInvoiceActivity;
    criCaja: boolean;
    re: boolean;
    regAgri: boolean;
    tax: ITax;
    taxBase: number;
    taxQuota: number;
    irpf: IIRPF;
    irpfbase: number;
    irpfquota: number;
    invoiceExpirationLines: ICollection<IInvoiceExpirationLine>;
    type: InvoiceType;
    rectified: boolean;
    status: InvoiceStatus;

    constructor(serie?: IInvoiceSerie, invoiceNumber?: string, date?: Date, totalAmount?: number, contact?: IContact, category?: IInvoiceCategory,
    lines?: ICollection<IInvoiceLine>, transactionType?: IInvoiceTransactionType, activity?: IInvoiceActivity, criCaja?: boolean, re?: boolean,
    regAgri?: boolean, tax?: ITax, taxBase?: number, taxQuota?: number, irpf?: IIRPF, irpfbase?: number, irpfquota?: number, type?: InvoiceType,
    rectified?: boolean, status?: InvoiceStatus){
        this.serie = serie || new InvoiceSerie();
        this.invoiceNumber = invoiceNumber || '';
        this.date = date || new Date();
        this.totalAmount = totalAmount || 0;
        this.contact = contact || new Contact();
        this.category = category || new InvoiceCategory();
        this.lines = lines || new Collection<IInvoiceLine>();
        this.transactionType = transactionType || new InvoiceTransactionType();
        this.activity = activity || new InvoiceActivity();
        this.criCaja = criCaja || false;
        this.re = re || false;
        this.regAgri = regAgri || false;
        this.tax = tax || new Tax();
        this.taxBase = taxBase || 0;
        this.taxQuota = taxQuota || 0;
        this.irpf = irpf || new IRPF();
        this.irpfbase = irpfbase || 0;
        this.irpfquota = irpfquota || 0;
        this.invoiceExpirationLines = new Collection<IInvoiceExpirationLine>();
        this.key = KeyGenerator.generate(15);
        this.apiObject = {};
        this.type = type || InvoiceType.GASTO;
        this.rectified = rectified || false;
        this.status = status || InvoiceStatus.A_CONTABILIZAR;
    }

    public get Status(): InvoiceStatus {
        return this.status;
    }

    public set Status(value: InvoiceStatus) {
        this.status = value;
    }

    public get Type(): InvoiceType {
        return this.type;
    }

    public set Type(value: InvoiceType) {
        this.type = value;
    }

    public get Rectified(): boolean {
        return this.rectified;
    }

    public set Rectified(value: boolean) {
        this.rectified = value;
    }

    public get Key(){
        return this.key;
    }

    public set Key(value: string){
        this.key = value;
    }

    public get ApiObject(): any {
        return this.apiObject;
    }

    public set ApiObject(value: any) {
        this.apiObject = value;
    }

    public get Serie(): IInvoiceSerie {
        return this.serie;
    }

    public set Serie(value: IInvoiceSerie) {
        this.serie = value;
    }

    public get InvoiceNumber(): string {
        return this.invoiceNumber;
    }

    public set InvoiceNumber(value: string) {
        this.invoiceNumber = value;
    }

    public get Date(): Date {
        return this.date;
    }

    public set Date(value: Date) {
        this.date = value;
    }

    public get TotalAmount(): number {
        return this.totalAmount;
    }

    public set TotalAmount(value: number) {
        this.totalAmount = value;
    }

    public get Contact(): IContact {
        return this.contact;
    }

    public set Contact(value: IContact) {
        this.contact = value;
    }

    public get Category(): IInvoiceCategory {
        return this.category;
    }

    public set Category(value: IInvoiceCategory) {
        this.category = value;
    }

    public get Lines(): ICollection<IInvoiceLine> {
        return this.lines;
    }

    public set Lines(value: ICollection<IInvoiceLine>) {
        this.lines = value;
    }

    public get TransactionType(): IInvoiceTransactionType {
        return this.transactionType;
    }

    public set TransactionType(value: IInvoiceTransactionType) {
        this.transactionType = value;
    }

    public get Activity(): IInvoiceActivity {
        return this.activity;
    }

    public set Activity(value: IInvoiceActivity) {
        this.activity = value;
    }

    public get CriCaja(): boolean {
        return this.criCaja;
    }

    public set CriCaja(value: boolean) {
        this.criCaja = value;
    }

    public get RE(): boolean {
        return this.re;
    }

    public set RE(value: boolean) {
        this.re = value;
    }

    public get RegAgri(): boolean {
        return this.regAgri;
    }

    public set RegAgri(value: boolean) {
        this.regAgri = value;
    }

    public get Tax(): ITax {
        return this.tax;
    }

    public set Tax(value: ITax) {
        this.tax = value;
    }

    public get TaxBase(): number {
        return this.taxBase;
    }

    public set TaxBase(value: number) {
        this.taxBase = value;
    }

    public get TaxQuota(): number {
        return this.taxQuota;
    }

    public set TaxQuota(value: number) {
        this.taxQuota = value;
    }

    public get IRPF(): IIRPF {
        return this.irpf;
    }

    public set IRPF(value: IIRPF) {
        this.irpf = value;
    }

    public get IRPFBase(): number {
        return this.irpfbase;
    }

    public set IRPFBase(value: number) {
        this.irpfbase = value;
    }

    public get IRPFQuota(): number {
        return this.irpfquota;
    }

    public set IRPFQuota(value: number) {
        this.irpfquota = value;
    }

    public get InvoiceExpirationLines(): ICollection<IInvoiceExpirationLine> {
        return this.invoiceExpirationLines;
    }

    public set InvoiceExpirationLines(value: ICollection<IInvoiceExpirationLine>) {
        this.invoiceExpirationLines = value;
    }

    getKey(): string {
        return this.key;
    }

    getFilterableFields(): Map<string, any> {
        let map = new Map<string,any>();
        map.set('serie', this.serie.Value);
        map.set('invoiceNumber', this.invoiceNumber);
        map.set('date', this.date);
        map.set('totalAmount', this.totalAmount);
        map.set('contact', this.contact.Name);
        map.set('category', this.category.Value);
        map.set('transactionType', this.transactionType.Value);
        map.set('activity', this.activity.Value);
        map.set('criCaja', this.criCaja);
        map.set('re', this.re);
        map.set('regAgri', this.regAgri);
        map.set('tax', this.tax.Value);
        map.set('taxBase', this.taxBase);
        map.set('taxQuota', this.taxQuota);
        map.set('irpf', this.irpf.Value);
        map.set('irpfbase', this.irpfbase);
        map.set('irpfquota', this.irpfquota);
        map.set('type', this.type);
        map.set('rectified', this.rectified);
        return map;
    }

    getSortableFields(): Map<string, any> {
        let map = new Map<string,any>();
        map.set('serie', this.serie.Value);
        map.set('invoiceNumber', this.invoiceNumber);
        map.set('date', this.date);
        map.set('totalAmount', this.totalAmount);
        map.set('contact', this.contact.Name);
        map.set('category', this.category.Value);
        map.set('transactionType', this.transactionType.Value);
        map.set('activity', this.activity.Value);
        map.set('criCaja', this.criCaja);
        map.set('re', this.re);
        map.set('regAgri', this.regAgri);
        map.set('tax', this.tax.Value);
        map.set('taxBase', this.taxBase);
        map.set('taxQuota', this.taxQuota);
        map.set('irpf', this.irpf.Value);
        map.set('irpfbase', this.irpfbase);
        map.set('irpfquota', this.irpfquota);
        map.set('type', this.type);
        map.set('rectified', this.rectified);
        return map;
    }
}

export class StorableInvoice implements IStorable<Invoice> {
    getCollection(): ICollection<Invoice> {
        return invoices;
    }
    getLocalStorage(): string {
        return 'invoices';
    }
}

export class ApiInvoice implements IApiModel {
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

export let invoices: ICollection<Invoice> = new Collection<Invoice>();
export function setInvoices(value: any) { invoices = value; };