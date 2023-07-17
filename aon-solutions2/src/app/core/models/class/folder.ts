import { Deserializable } from "../interface/deserializable";

export class Folder implements Deserializable {

    private name: string;
    private path: string;
    private parent: string;

    constructor(name?: string, path?: string, parent?: string) {
        this.name = name || '';
        this.path = path || '';
        this.parent = parent || '';
    }

    public get Name(): string {
        return this.name;
    }

    public get Parent(): string {
      return this.parent;
    }

    public set Name(value: string) {
        this.name = value;
    }

    public set Parent(value: string) {
      this.parent = value;
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

    deserializeArray(input: any): Array<Folder> {
        let array = []
        for(let i = 0; i < input.length; i++){
            let aux = new Folder()
            aux.deserialize(input[i])
            array.push(aux)
        }
        return array;
    }
}
