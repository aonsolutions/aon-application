export interface Deserializable {
    deserialize(input: any): this;
    deserializeArray(input: any): Array<any>;
}