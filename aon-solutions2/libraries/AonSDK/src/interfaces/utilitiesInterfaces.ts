import { ICollectable } from "./modelsInterfaces";

export interface IResponse<T> {
    code: string;
    description: string;
    result: T;
}

export interface ILocalStorage<T extends ICollectable> {
    /**
     * Read a collection from localstorage
     * @param model The name of the model in local storage
     */
    read(model:string): ICollection<T>;
    /**
     * Overwrite a collection in localstorage
     * @param model The name of the model in local storage
     * @param collection The collection to save on localstorage
     */
    write(model:string, collection: ICollection<T>): void;
}

export interface ICollection<T extends ICollectable> extends Iterable<T> {
    /**
     * Gets the size of the collection
     * @returns The size of the collection
     */
    size(): number;
    /**
     * Checks if the collection is empty
     * @returns True if the collection is empty, false otherwise
     */
    isEmpty(): boolean;
    /**
     * Check if element exists in collection
     * @param key The key of the element. You can use .getKey() to get it
     * @returns True if the element exists, false otherwise
     */
    exists(key: string): boolean;
    /**
     * Get element by key
     * @param key The key of the element. You can use .getKey() to get it
     * @return The element
     */
    get(key: string): T;
    /**
     * Add new element to collection
     * @param element Element to add
     */
    add(element: T): void;
    /**
     * Remove the element from collection
     * @param key The key of the element. You can use .getKey() to get it
     */
    remove(key: string): void;
    /**
     * Sort the current collection. You need to add order to IFilter
     * @param filter The filter to apply
     */
    sort(filter: IFilter): void;
    /**
     * forEach method to loop over the collection
     *
     * @Example collection.foreach((element,index) => {
     *    console.log(element, index);
     * })
     */
    forEach(callbackfn: (value: T, index: string) => void): void;
    /**
     * Filter the current collection. You need to add field or interval fields to IFilter
     * @param filter The filter to apply
     */
    filter(filter: IFilter): ICollection<T>;
    /**
     * Get a section of the array
     * @param start The beginning index of the specified portion of the array.
     * @param end The end index of the specified portion of the array.
     * @returns Copy of a section of the array
     */
    slice(start: number, end: number): ICollection<T>;
    /**
     * Paginate the collection
     * @param pageNum The page number
     * @param totalPage The number of elements per page
     * @returns A copy of the collection paginated
     */
    paginate(pageNum: number, totalPage:number): ICollection<T>;
    /**
     * Convert the collection to an array
     * @returns The collection as an array
     */
    toArray(): T[];
    /**
     * Copy the array content on the collection
     * @param Array Array of objects to copy to the collection
     */
    copyArrayToCollection(array: T[]): void;
}

export interface IFilter {
    selectedFields?: string[];
    pageNum?: number;
    pageItems?: number;
    fields?: Map<string,any>;
    intervalFields?: Map<string,any>;
    orderBy?: Map<string,string>;
}