import { Deserializable } from '../interface/deserializable';

export class Enterprise implements Deserializable {

  private name: string;
  private document: string;
  
  constructor(name?: string, document?: string){
    this.name = name || '';
    this.document = document || '';
  }
  
  public get Name(): string {
    return this.name;
  }

  public set Name(value: string) {
    this.name = value;
  }

  public get Document(): string {
    return this.document;
  }

  public set Document(value: string) {
    this.document = value;
  }

  deserialize(input: any): this {
    Object.assign(this, input);
    return this;
  }

  deserializeArray(input: any): Array<Enterprise> {
    let enterprises = []
    for(let i = 0; i < input.length; i++){
      let aux = new Enterprise()
      aux.deserialize(input[i])
      enterprises.push(aux)
    }
    return enterprises;
  }

}
