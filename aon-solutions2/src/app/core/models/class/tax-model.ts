import { Deserializable } from "../interface/deserializable";

export class TaxModel implements Deserializable{
    
    private name: number;
    private status: string;
    private taxType: string;
    private paymentMethod: string;
    private result: string;
    private trimester: number;
    private year: number;

    public get Name(): number {
        return this.name;
    }

    public set Name(value: number) {
        this.name = value;
    }

    public get TaxType(): string {
        return this.taxType;
    }

    public set TaxType(value: string) {
        this.taxType = value;
    }

    public get Status(): string {
        return this.status;
    }

    public set Status(value: string) {
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

    constructor(name?: number, taxType?: string, status?: string, paymentMethod?: string, result?: string, trimester?: number, year?: number){
        this.name = name || 0;
        this.taxType = taxType || '';
        this.status = status || '';
        this.paymentMethod = paymentMethod || '';
        this.result = result || '';
        this.trimester = trimester || 0;
        this.year = year || 0;
    }

    deserialize(input: any): this {
        Object.assign(this, input);
        return this;
    }
    
    deserializeArray(input: any): Array<TaxModel> {
        let taxModels = []
        for(let i = 0; i < input.length; i++){
            let aux = new TaxModel()
            aux.deserialize(input[i])
            taxModels.push(aux)
        }
        return taxModels;
    }

}
