import { Deserializable } from "../interface/deserializable";

export class Message implements Deserializable{

    private id: number;
    private name: string;
    private title: string;
    private description: string;
    private date: Date;
    private type: string;
    private status: string;
    private endDate: Date;
    
    constructor(id?:number, name?: string, title?: string,description?: string,date?: Date,type?: string,status?: string,endDate?: Date){
        this.id = id || 0;
        this.name = name || '';
        this.title = title || '';
        this.description = description || '';
        this.date = date || new Date();
        this.type = type || '';
        this.status = status || '';
        this.endDate = endDate || new Date();
    }

    public get Id(): number {
        return this.id;
    }

    public set Id(value: number) {
        this.id = value;
    }
    
    public get Name(): string {
        return this.name;
    }

    public set Name(value: string) {
        this.name = value;
    }
    
    public get Title(): string {
        return this.title;
    }

    public set Title(value: string) {
        this.title = value;
    }
    
    public get Description(): string {
        return this.description;
    }

    public set Description(value: string) {
        this.description = value;
    }
    
    public get Date(): Date {
        return this.date;
    }

    public set Date(value: Date) {
        this.date = value;
    }
    
    public get Type(): string {
        return this.type;
    }

    public set Type(value: string) {
        this.type = value;
    }
    
    public get Status(): string {
        return this.status;
    }

    public set Status(value: string) {
        this.status = value;
    }
    
    public get EndDate(): Date {
        return this.endDate;
    }

    public set EndDate(value: Date) {
        this.endDate = value;
    }

    deserialize(input: any): this {
        Object.assign(this, input);
        return this;
    }
    
    deserializeArray(input: any): Array<Message> {
        let array = []
        for(let i = 0; i < input.length; i++){
            let aux = new Message()
            aux.deserialize(input[i])
            array.push(aux)
        }
        return array;
    }

}
