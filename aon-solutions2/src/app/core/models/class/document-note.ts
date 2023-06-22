import { Deserializable } from "../interface/deserializable";

export class DocumentNote implements Deserializable{

    private text: string;
    private path: string;
    
    constructor(text?: string,path?: string){
        this.text = text || '';
        this.path = path || '';
    }

    public get Text(): string {
        return this.text;
    }

    public set Text(value: string) {
        this.text = value;
    }

    public get Path(): string {
        return this.path;
    }

    public set Path(value: string) {
        this.path = value;
    }

    deserialize(input: any): this {
        Object.assign(this, input);
        return this;
    }
    
    deserializeArray(input: any): Array<DocumentNote> {
        let array = []
        for(let i = 0; i < input.length; i++){
            let aux = new DocumentNote()
            aux.deserialize(input[i])
            array.push(aux)
        }
        return array;
    }

}
