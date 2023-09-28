import { ICollectable } from "../interfaces/modelsInterfaces";
import { ICollection, IFilter } from "../interfaces/utilitiesInterfaces";

export class Collection<T extends ICollectable> implements ICollection<T> {

    // The data of the collection is saved in a Map
    private data: Map<string,T>;

    constructor() {
        this.data = new Map<string, T>();
    }

    // Iterator for collection to work on loops of type => for(const element of collection)
    [Symbol.iterator](): Iterator<T, any, undefined> {
        return this.data.values();
    }

    // Returns the size of the collection
    size(): number {
        return this.data.size;
    }

    // Check if collection is empty
    isEmpty(): boolean {
        return this.data.size == 0 ? true : false;
    }

    // Check if one element exists on the collection
    exists(key: string): boolean {
        return this.data.has(key);
    }

    // Get one element of collection by key
    get(key: string): T {
        const element = this.data.get(key);
        if(element) return element; else throw new Error("Element not found in collection");
    }

    // Add new element to collection
    add(element: T): void {
        this.data.set(element.getKey(), element);
    }

    // Remove element from collection
    remove(key: string): void {
        this.data.delete(key);
    }

    // Sort collection using IFilter interface, can be ordered by many fields
    sort(filter: IFilter): void {
        // Check if collection is empty and throw an error
        if(this.isEmpty()) throw new Error('SORT ERROR: No hay elementos para ordenar');
        // Check if filter is defined
        if(filter.orderBy == undefined) throw new Error('SORT ERROR: No esta definido el filtro para ordenar');
        // Check if filter is valid, the sort order must be asc or desc and the field must exist on getSortableFields() method of object
        filter.orderBy.forEach((key, value) => {
            if(this.toArray()[0].getSortableFields().get(value.toLowerCase()) == undefined) throw new Error('SORT ERROR: No campo a ordenar no existe o no se puede ordenar');
            if(key.toLowerCase() != 'asc' && key.toLowerCase() != 'desc') throw new Error('SORT ERROR: Orden invalido');
        });
        let orders = Array.from(filter.orderBy?.entries())
        // Recursive function to sort the collection, return 1 or -1 if the value is less or greater than the other
        // If not call sorFunction again for the next field
        let sortFuction = (left: T, right: T, n: number): number => {
            if(left.getSortableFields().get(orders[n][0]) < right.getSortableFields().get(orders[n][0])) return orders[n][1] == 'desc' ? 1 : -1;
            if(left.getSortableFields().get(orders[n][0]) > right.getSortableFields().get(orders[n][0])) return orders[n][1] == 'desc' ? -1 : 1;
            if(orders[n+1]) return sortFuction(left, right, n+1);
            else return 0;
        }
        // The call to sortFuction from inside .sort of primitive array
        let array = this.toArray().sort((left, right) => {
            let n = 0;
            return sortFuction(left, right, n);
        })
        // Remove the actual collection
        this.data.clear();
        // Copy the sorted array to the collection
        this.copyArrayToCollection(array);
    }

    // ForEach function to loop over the collection
    forEach(callbackfn: (value: T, index: string) => void): void {
        this.data.forEach((element, key) => {
            callbackfn(element, key);
        })
    }

    // Function to filter the collection, return new collection dont modify the original
    filter(filter: IFilter): ICollection<T> {
        // Check if collection is empty and throw an error
        if(this.isEmpty()) throw new Error('FILTER ERROR: No hay elementos para filtrar');
        // Check if filter is defined
        if(filter.fields?.size == 0 && filter.intervalFields?.size == 0) throw new Error('FILTER ERROR: No esta definido el filtro para filtrar');
        let filteredArray = this.toArray();
        // Apply the filter usings fields of filter and == operator
        if(filter.fields?.size != 0)
            filter.fields?.forEach((value, key) => {
                filteredArray = filteredArray.filter(element =>
                    element.getFilterableFields().has(key.toLowerCase()) ? value.includes(element.getFilterableFields().get(key.toLowerCase())) : true//element.getFilterableFields().get(key.toLowerCase()).includes(value)
                )
            })
        // Apply the filter usings interval fields and >= and <= operator
        if(filter.intervalFields?.size != 0)
            filter.intervalFields?.forEach((value, key) => {
                filteredArray = filteredArray.filter(element =>
                    value.end >= element.getFilterableFields().get(key.toLowerCase()) <= value.start
                )
            })
        let collection = new Collection<T>();
        // Copy the filtered array to new collection
        collection.copyArrayToCollection(filteredArray);
        // Return the new collection
        return collection;
    }

    // Slice the collection
    slice(start: number, end: number): ICollection<T> {
        let collection = new Collection<T>()
        collection.copyArrayToCollection(this.toArray().slice(start,end));
        return collection;
    }

    // Paginate the collection
    paginate(pageNum: number, pageItems: number): ICollection<T> {
        let collection = new Collection<T>();
        collection.copyArrayToCollection(this.toArray().slice((pageNum - 1) * pageItems, pageNum * pageItems));
        return collection;
    }

    // Get the collection as an array
    toArray(): T[] {
        return Array.from(this.data.values());
    }

    // Copy an array of objects to the collection
    copyArrayToCollection(array: T[]): void {
        array.forEach((element:T) => {
            this.add(element);
        })
    }
}