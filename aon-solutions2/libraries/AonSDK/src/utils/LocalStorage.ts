import { ICollectable } from "../interfaces/modelsInterfaces";
import { ILocalStorage, ICollection } from "../interfaces/utilitiesInterfaces";
import { Collection } from "./Collection";

export class LocalStorage<T extends ICollectable> implements ILocalStorage<T> {
    private type: { new (): T };

    constructor(type: { new (): T }) {
        this.type = type;
    }

    read(model:string): ICollection<T> {
        let collection: ICollection<T> = new Collection<T>();
        if(localStorage.getItem(model)){
            let array: string[] = JSON.parse(localStorage.getItem(model) || '')
            let arrayAux: T[] = []
            array.forEach(element => {
                arrayAux.push(Object.assign(new this.type(),JSON.parse(element)))
            });
            collection.copyArrayToCollection(arrayAux);
        }
        return collection;
    }

    write(model:string, collection: ICollection<T>): void {
        let arrayAux: string[] = []
        const array: T[] = collection.toArray();
        array.forEach((element: T) => {
            arrayAux.push(JSON.stringify(element))
        })
        localStorage.setItem(model,JSON.stringify(arrayAux))
    }
}