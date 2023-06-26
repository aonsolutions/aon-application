import { Deserializable } from "../interface/deserializable";

export class MessageChat implements Deserializable{

    private id: number;
    private idMessage: number;
    private name: string;
    private description: string;
    private date: Date;
    private type: string;
    
    public get Id(): number {
        return this.id;
    }

    public set Id(value: number) {
        this.id = value;
    }
    
    public get IdMessage(): number {
        return this.idMessage;
    }

    public set IdMessage(value: number) {
        this.idMessage = value;
    }
    
    public get Name(): string {
        return this.name;
    }

    public set Name(value: string) {
        this.name = value;
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

    constructor(idMessage?:number, name?: string,description?: string,date?: Date,type?: string){
        this.id = 0;
        this.idMessage = idMessage || 0;
        this.name = name || '';
        this.description = description || '';
        this.date = date || new Date();
        this.type = type || '';
    }

    deserialize(input: any): this {
        Object.assign(this, input);
        return this;
    }
    
    deserializeArray(input: any): Array<MessageChat> {
        let array = []
        for(let i = 0; i < input.length; i++){
            let aux = new MessageChat()
            aux.deserialize(input[i])
            array.push(aux)
        }
        return array;
    }

}
