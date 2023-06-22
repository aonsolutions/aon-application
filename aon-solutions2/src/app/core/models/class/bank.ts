import { Deserializable } from "../interface/deserializable";

export class Bank implements Deserializable{

    private name: string;
    private total: number;
    private logo: string;
    
    constructor(name?: string,total?: number,logo?: string){
        this.name = name || '';
        this.total = total || 0;
        this.logo = logo || '';
    }
    
    public get Name(): string {
        return this.name;
    }

    public set Name(value: string) {
        this.name = value;
    }

    public get Total(): number {
        return this.total;
    }

    public set Total(value: number) {
        this.total = value;
    }

    public get Logo(): string {
        return this.logo;
    }

    public set Logo(value: string) {
        this.logo = value;
    }

    deserialize(input: any): this {
        Object.assign(this, input);
        return this;
    }
    
    deserializeArray(input: any): Array<Bank> {
        let array = []
        for(let i = 0; i < input.length; i++){
            let aux = new Bank()
            aux.deserialize(input[i])
            array.push(aux)
        }
        return array;
    }

}