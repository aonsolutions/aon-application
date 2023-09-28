import { IModel } from "../interfaces/modelsInterfaces";
import { KeyGenerator } from "../utils/KeyGenerator";

export class SimpleValue implements IModel{
    protected key: string;
    protected value: string;
    protected apiObject: any;

    constructor(value?: string) {
        this.key = KeyGenerator.generate(15);
        this.value = value || '';
        this.apiObject = {};
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

    public get Value() {
        return this.value;
    }

    public set Value(value: string) {
        this.value = value;
    }

    getKey(): string {
        return this.key;
    }

    getFilterableFields(): Map<string, any> {
        let map = new Map<string, any>();
        map.set('value', this.value);
        return map;
    }

    getSortableFields(): Map<string, any> {
        let map = new Map<string, any>();
        map.set('value', this.value);
        return map;
    }
}