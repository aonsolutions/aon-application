import { IInvoiceExpirationLine, IModel, IPaymentMethod } from "../interfaces/modelsInterfaces";
import { KeyGenerator } from "../utils/KeyGenerator";
import { PaymentMethod } from "./PaymenMethod";

export class InvoiceExpirationLine implements IInvoiceExpirationLine, IModel {
    date: Date;
    paymentMethod: IPaymentMethod;
    bankAccount: string;
    amount: number;
    key: string;
    apiObject: any;

    constructor(date?: Date, paymentMethod?: IPaymentMethod, bankAccount?: string, amount?: number){
        this.apiObject = {};
        this.key = KeyGenerator.generate(15);
        this.date = date || new Date();
        this.paymentMethod = paymentMethod || new PaymentMethod();
        this.bankAccount = bankAccount || '';
        this.amount = amount || 0;
    }
    getDate(): Date {
        return this.date
    }
    setDate(value: Date): IInvoiceExpirationLine {
        this.date = value;
        return this
    }
    getPaymentMethod(): IPaymentMethod {
        return this.paymentMethod
    }
    setPaymentMethod(value: IPaymentMethod): IInvoiceExpirationLine {
        this.paymentMethod = value;
        return this
    }
    getBankAccount(): string {
        return this.bankAccount
    }
    setBankAccount(value: string): IInvoiceExpirationLine {
        this.bankAccount = value;
        return this
    }
    getAmount(): number {
        return this.amount
    }
    setAmount(value: number): IInvoiceExpirationLine {
        this.amount = value;
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

    public get Date() {
        return this.date;
    }

    public set Date(value: Date) {
        this.date = value;
    }

    public get PaymentMethod(): IPaymentMethod {
        return this.paymentMethod;
    }

    public set PaymentMethod(value: IPaymentMethod) {
        this.paymentMethod = value;
    }

    public get BankAccount(): string {
        return this.bankAccount;
    }

    public set BankAccount(value: string) {
        this.bankAccount = value;
    }

    public get Amount(): number {
        return this.amount;
    }

    public set Amount(value: number) {
        this.amount = value;
    }

    getKey(): string {
        return this.key;
    }

    getFilterableFields(): Map<string, any> {
        let map = new Map<string,any>();
        map.set('date', this.date);
        map.set('paymentMethod', this.paymentMethod.Value);
        map.set('bankAccount', this.bankAccount);
        map.set('amount', this.amount);
        return map;
    }

    getSortableFields(): Map<string, any> {
        let map = new Map<string,any>();
        map.set('date', this.date);
        map.set('paymentMethod', this.paymentMethod.Value);
        map.set('bankAccount', this.bankAccount);
        map.set('amount', this.amount);
        return map;
    }
}