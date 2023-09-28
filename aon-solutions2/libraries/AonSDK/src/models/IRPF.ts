import { IIRPF, IModel, IStorable } from "../interfaces/modelsInterfaces";
import { ICollection } from "../interfaces/utilitiesInterfaces";
import { Collection } from "../utils/Collection";
import { SimpleValue } from "./SimpleValue";

export class IRPF extends SimpleValue implements IIRPF, IModel{
    private type: string;

    constructor(value?: string, type?: string) {
        super(value);
        this.type = type || '';
    }

    public get Type() {
        return this.type;
    }

    public set Type(value: string) {
        this.type = value;
    }

    getFilterableFields(): Map<string, any> {
        let map = new Map<string, any>();
        map.set('value', this.value);
        map.set('type', this.type);
        return map;
    }

    getSortableFields(): Map<string, any> {
        let map = new Map<string, any>();
        map.set('value', this.value);
        map.set('type', this.type);
        return map;
    }
}

export class StorableIRPF implements IStorable<IRPF> {
    getCollection(): ICollection<IRPF> {
        return irpf;
    }
    getLocalStorage(): string {
        return 'irpf';
    }
}

export let irpf: ICollection<IRPF> = new Collection<IRPF>();
export function setIRPF(value: any) { irpf = value; };