import { ITaxModel, IModel, statusTaxModel, IApiModel, IStorable } from "../interfaces/modelsInterfaces";
import { IFilter, ICollection } from "../interfaces/utilitiesInterfaces";
import { Collection } from "../utils/Collection";
import { ErrorResponse } from "../utils/Response";
import { GET_MULTIPLE, GET_METHOD } from "../utils/Environment";
import { Bank } from "./Bank";

export class TaxModel implements ITaxModel, IModel  {
    private name: string;
    private taxType: string;
    private status: statusTaxModel;
    private paymentMethod: string;
    private result: string;
    private trimester: number;
    private year: number;
    private key: string;
    protected apiObject: any;

    public get ApiObject(): any {
        return this.apiObject;
    }

    public set ApiObject(value: any) {
        this.apiObject = value;
    }

    constructor(name?: string, taxType?: string, status?: statusTaxModel, paymentMethod?: string, result?: string, trimester?: number, year?: number) {
        this.name = name || '';
        this.taxType = taxType || '';
        this.status = status || statusTaxModel.PENDIENTE;
        this.paymentMethod = paymentMethod || '';
        this.result = result || '';
        this.trimester = trimester || 0;
        this.year = year || 0;
        if(name && trimester && year) this.key = name + ';' + trimester.toString() + ';' + year.toString();
        else this.key = ''
    }
    getName(): string {
        return this.name
    }
    setName(value: string): ITaxModel {
        this.name = value;
        return this
    }
    getTaxType(): string {
        return this.taxType
    }
    setTaxType(value: string): ITaxModel {
        this.taxType = value;
        return this
    }
    getStatus(): statusTaxModel {
        return this.status
    }
    setStatus(value: statusTaxModel): ITaxModel {
        this.status = value;
        return this
    }
    getPaymentMethod(): string {
        return this.paymentMethod
    }
    setPaymentMethod(value: string): ITaxModel {
        this.paymentMethod = value;
        return this
    }
    getResult(): string {
        return this.result
    }
    setResult(value: string): ITaxModel {
        this.result = value;
        return this
    }
    getTrimester(): number {
        return this.trimester
    }
    setTrimester(value: number): ITaxModel {
        this.trimester = value;
        return this
    }
    getYear(): number {
        return this.year
    }
    setYear(value: number): ITaxModel {
        this.year = value;
        return this
    }

    public get Name(): string {
        return this.name;
    }

    public set Name(value: string) {
        this.name = value;
    }

    public get TaxType(): string {
        return this.taxType;
    }

    public set TaxType(value: string) {
        this.taxType = value;
    }

    public get Status(): statusTaxModel {
        return this.status;
    }

    public set Status(value: statusTaxModel) {
        this.status = value;
    }

    public get PaymentMethod(): string {
        return this.paymentMethod;
    }

    public set PaymentMethod(value: string) {
        this.paymentMethod = value;
    }

    public get Result(): string {
        return this.result;
    }

    public set Result(value: string) {
        this.result = value;
    }

    public get Trimester(): number {
        return this.trimester;
    }

    public set Trimester(value: number) {
        this.trimester = value;
    }

    public get Year(): number {
        return this.year;
    }

    public set Year(value: number) {
        this.year = value;
    }

    public get Key(){
        return this.key;
    }

    public set Key(value: string) {
        this.key = value;
    }

    getKey(): string {
        return this.key;
    }

    getFilterableFields(): Map<string, any> {
        let map = new Map<string, any>();
        map.set('name', this.Name);
        map.set('taxType', this.TaxType);
        map.set('status', this.Status);
        map.set('paymentMethod', this.PaymentMethod);
        map.set('result', this.Result);
        map.set('trimester', this.Trimester);
        map.set('year', this.Year);
        return map;
    }

    getSortableFields(): Map<string, any> {
        let map = new Map<string, any>();
        map.set('name', this.Name);
        map.set('taxType', this.TaxType);
        map.set('status', this.Status);
        map.set('paymentMethod', this.PaymentMethod);
        map.set('result', this.Result);
        map.set('trimester', this.Trimester);
        map.set('year', this.Year);
        return map;
    }
}

export class ApiTaxModel extends TaxModel implements IApiModel {
    getUrl(currentMethod: string): string[] {
        if(currentMethod == GET_MULTIPLE)
            return ['/ms/api/fiscal/models']
            throw new ErrorResponse('0199')
    }

    getMethod(currentMethod: string, filter?: IFilter | undefined): string {
        if(currentMethod == GET_MULTIPLE)
            return GET_METHOD;
            throw new ErrorResponse('0199')
    }

    localFilter(currentMethod: string, filter?: IFilter): boolean {
        return true;
    }

    parseDataToSend(data: any) {
        throw new ErrorResponse('0199')
    }

    parseDataToReceive(data: any) {
        let tax = new TaxModel();
        tax.ApiObject = data;
        tax.Key = data.id;
        tax.Name = data.model ? (data.model == 'IVA' ? '303' : data.model) : '';
        tax.PaymentMethod = data.type ? data.type : '';
        tax.Result = data.result ? data.result : '';
        tax.Status = data.status ? data.status : '';
        tax.TaxType = '';
        tax.Trimester = data.period ? +data.period.replace('T','') : 0;
        tax.Year = data.year ? data.year : '';
        return tax;
    }
}

export class StorableTaxModel extends TaxModel implements IStorable<TaxModel> {
    getCollection(): ICollection<TaxModel> {
        return taxModels;
    }
    getLocalStorage(): string {
        return 'taxModels';
    }
}

export let taxModels: ICollection<TaxModel> = new Collection<TaxModel>();
export function setTaxModels(value: any) { taxModels = value; };

// PENDING("Pendiente")
// FINISHED("Finalizado")
// BATCHED("En Lote")
// BLOCKED("Bloqueado")
// SENT("Presentado")
// MISSING("Desconocido")
// CUSTOMER_CHECK("Envio a cliente")
// CUSTOMER_ACCEPTED("Aceptado por cliente")
// CUSTOMER_REJECTED("Rechazado por cliente")


export function statusParse(status: string){
    switch(status){
        case 'SENT':
            return statusTaxModel.PENDIENTE;
        case 'CONFIRMED':
            return statusTaxModel.CONFIRMADO;
        case 'CUSTOMER_CHECK':
            return statusTaxModel.PENDIENTE;
        default:
            return statusTaxModel.PENDIENTE;
    }
}