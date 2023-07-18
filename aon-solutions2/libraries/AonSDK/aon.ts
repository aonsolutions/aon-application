/*
 * 
 * FACTORYS INTERFACES
 * 
 */


interface ISingleObjectCrudFactory<T extends ICollectable> {
    createSingleObjectCrud(): ISingleObjectCreator<T> | ISingleObjectUpdater<T> | ISingleObjectRemover<T> | ISingleObjectReader<T>;
}

interface IMultipleObjectCrudFactory<T extends ICollectable> {
    createMultipleObjectCrud(): IMultipleObjectCreator<T> | IMultipleObjectUpdater<T> | IMultipleObjectRemover<T> | IMultipleObjectReader<T>;
}

/*
 * 
 * CONCRETE FACTORYS IMPLEMENTATIONS
 * 
 */

export class DocumentFactory implements ISingleObjectCrudFactory<IDocument>, IMultipleObjectCrudFactory<IDocument> {
    createSingleObjectCrud(): ISingleObjectCrud<IDocument> {
        return new GenericSingleObjectCrud<IDocument>(new GenericSingleObjectCrudRepository<IDocument>(new StorableDocument()));
    }
    createMultipleObjectCrud(): IMultipleObjectCrud<IDocument> {
        return new GenericMultipleObjectCrud<IDocument>(new GenericMultipleObjectCrudRepository<IDocument>(new StorableDocument()));
    }
}

export class FolderFactory implements ISingleObjectCrudFactory<IFolder>, IMultipleObjectCrudFactory<IFolder> {
    createSingleObjectCrud(): ISingleObjectReader<IFolder> {
        return new GenericSingleObjectCrud<IFolder>(new GenericSingleObjectCrudRepository<IFolder>(new StorableFolder()));
    }
    createMultipleObjectCrud(): IMultipleObjectCrud<IFolder> {
        return new GenericMultipleObjectCrud<IFolder>(new GenericMultipleObjectCrudRepository<IFolder>(new StorableFolder()));
    }
}

/*
 * 
 * INTERFACES TO DEFINE METHODS FOR CLIENT
 * 
 */

interface ISingleObjectReader<T extends ICollectable> {
    getElement(key: string): Promise<T>;
}

interface ISingleObjectCreator<T extends ICollectable> {
    createElement(element: T): Promise<T>;
}

interface ISingleObjectUpdater<T extends ICollectable> {
    updateElement(element: T): Promise<T>;
}

interface ISingleObjectRemover<T extends ICollectable> {
    deleteElement(key: string): Promise<void>;
}

interface ISingleObjectCrud<T extends ICollectable> extends ISingleObjectReader<T>, ISingleObjectCreator<T>, ISingleObjectUpdater<T>, ISingleObjectRemover<T> {}

interface IMultipleObjectReader<T extends ICollectable> {
    getCollection(filter?: IFilter): Promise<ICollection<T>>;
}

interface IMultipleObjectCreator<T extends ICollectable> {
    createCollection(collection: ICollection<T>): Promise<ICollection<T>>;
}

interface IMultipleObjectUpdater<T extends ICollectable> {
    updateCollection(collection: ICollection<T>): Promise<ICollection<T>>;
}

interface IMultipleObjectRemover<T extends ICollectable> {
    deleteCollection(keys: string []): Promise<void>;
}

interface IMultipleObjectCrud<T extends ICollectable> extends IMultipleObjectReader<T>, IMultipleObjectCreator<T>, IMultipleObjectUpdater<T>, IMultipleObjectRemover<T> {}

/*
 * 
 * IMPLEMENTATION OF INTERFACES FOR THE CLIENTS
 * 
 */

class GenericSingleObjectCrud<T extends ICollectable> implements ISingleObjectCrud<T> {
    private repository: ISingleObjectCrudRepository<T>;

    constructor(repository: ISingleObjectCrudRepository<T>){
        this.repository = repository;
    }

    async createElement(element: T): Promise<T> {
        return this.repository.create(element);
    }

    async updateElement(element: T): Promise<T> {
        return this.repository.update(element);
    }

    async deleteElement(key: string): Promise<void> {
        this.repository.delete(key);
    }

    async getElement(key: string): Promise<T> {
        return this.repository.get(key);
    }
}

class GenericMultipleObjectCrud<T extends ICollectable> implements IMultipleObjectCrud<T> {
    private repository: IMultipleObjectCrudRepository<T>;

    constructor(repository: IMultipleObjectCrudRepository<T>){
        this.repository = repository;
    }

    async getCollection(filter?: IFilter): Promise<ICollection<T>> {
        return this.repository.get(filter);
    }

    async createCollection(collection: ICollection<T>): Promise<ICollection<T>> {
        return this.repository.create(collection);
    }

    async updateCollection(collection: ICollection<T>): Promise<ICollection<T>> {
        return this.repository.update(collection);
    }

    async deleteCollection(keys: string[]): Promise<void> {
        return this.repository.delete(keys);
    }
}

/*
 * 
 * REPOSITORY IMPLEMENTATION
 * 
 */

interface IRepositorySingleObjectReader<T> {
    get(key: string): Promise<T>;
}

interface IRepositorySingleObjectCreator<T> {
    create(element: T): Promise<T>;
}

interface IRepositorySingleObjectUpdater<T> {
    update(element: T): Promise<T>;
}

interface IRepositorySingleObjectRemover<T> {
    delete(key: string): Promise<void>;
}

interface IRepositoryMultipleObjectReader<T extends ICollectable> {
    get(filter?: IFilter): Promise<ICollection<T>>;
}

interface IRepositoryMultipleObjectCreator<T extends ICollectable> {
    create(collection: ICollection<T>): Promise<ICollection<T>>;
}

interface IRepositoryMultipleObjectUpdater<T extends ICollectable> {
    update(collection: ICollection<T>): Promise<ICollection<T>>;
}

interface IRepositoryMultipleObjectRemover<T extends ICollectable> {
    delete(keys: string []): Promise<void>;
}

interface ISingleObjectCrudRepository<T extends ICollectable> extends IRepositorySingleObjectReader<T>, IRepositorySingleObjectCreator<T>, IRepositorySingleObjectUpdater<T>, IRepositorySingleObjectRemover<T> {}

interface IMultipleObjectCrudRepository<T extends ICollectable> extends IRepositoryMultipleObjectReader<T>, IRepositoryMultipleObjectCreator<T>, IRepositoryMultipleObjectUpdater<T>, IRepositoryMultipleObjectRemover<T> {}

/*
 * 
 * CONCRETE REPOSITORY IMPLEMENTATION
 * 
 */

class GenericMultipleObjectCrudRepository<T extends ICollectable> implements IMultipleObjectCrudRepository<T> {
    private instance: IStorable<T>;
    private localStorageManager: LocalStorage<T>;

    constructor(instance: IStorable<T>) {
        this.instance = instance;
        this.localStorageManager = new LocalStorage<T>(this.instance);
    }

    async get(filter?: IFilter): Promise<ICollection<T>> {
        let collection = this.instance.getCollection()
        if(filter?.intervalFields || filter?.fields) collection = collection.filter(filter);
        if(filter?.orderBy) collection.sort(filter);
        if(filter?.pageItems && filter.pageNum) collection = collection.paginate(filter.pageNum,filter.pageItems);
        return collection;
    }

    async create(collection: ICollection<T>): Promise<ICollection<T>> {
        collection.toArray().forEach(element => {
            if(this.instance.getCollection().exists(element.getKey())){
                throw new Error('CREATE ERROR: Ya existe un elemento con ese key');
            }
        })
        collection.toArray().forEach(element => {
            element.Key = element.getKey();
            this.instance.getCollection().add(element);
        })
        this.localStorageManager.write(this.instance.getLocalStorage(), this.instance.getCollection())
        return collection;
    }

    async update(collection: ICollection<T>): Promise<ICollection<T>> {
        collection.toArray().forEach(element => {
            if(!(element.getKey() != element.Key && !this.instance.getCollection().exists(element.getKey()) && this.instance.getCollection().exists(element.Key)
            || element.getKey() == element.Key && this.instance.getCollection().exists(element.Key)))
                throw new Error('UPDATE ERROR: Error al actualizar el elemento')
        })
        collection.toArray().forEach(element => {
            this.instance.getCollection().remove(element.Key);
            element.Key = element.getKey();
            this.instance.getCollection().add(element)
        })
        this.localStorageManager.write(this.instance.getLocalStorage(), this.instance.getCollection())
        return collection;
    }

    async delete(keys: string []): Promise<void> {
        keys.forEach(key => {
            if(!this.instance.getCollection().exists(key))
                throw new Error('DELETE ERROR: No se encontro el elemento');
        })
        keys.forEach(key => {
            this.instance.getCollection().remove(key);
        })
        this.localStorageManager.write(this.instance.getLocalStorage(), this.instance.getCollection())
    }
}

class GenericSingleObjectCrudRepository<T extends ICollectable> implements ISingleObjectCrudRepository<T> {
    private instance: IStorable<T>;
    private localStorageManager: LocalStorage<T>;

    constructor(instance: IStorable<T>) {
        this.instance = instance;
        this.localStorageManager = new LocalStorage<T>(this.instance);
    }

    async get(key: string): Promise<T> {
        if(this.instance.getCollection().exists(key))
            return this.instance.getCollection().get(key);
        else
            throw new Error('GET ERROR: No existe un elemento con ese key');
    }

    async create(element: T): Promise<T> {
        if(!this.instance.getCollection().exists(element.getKey())){
            element.Key = element.getKey()
            this.instance.getCollection().add(element);
            this.localStorageManager.write(this.instance.getLocalStorage(), this.instance.getCollection())
            return element;
        }else{
            throw new Error('CREATE ERROR: Ya existe un elemento con ese key');
        }
    }

    async update(element: T): Promise<T> {
        if(element.getKey() != element.Key && !this.instance.getCollection().exists(element.getKey()) && this.instance.getCollection().exists(element.Key)
        || element.getKey() == element.Key && this.instance.getCollection().exists(element.Key)){
            this.instance.getCollection().remove(element.Key);
            element.Key = element.getKey();
            this.instance.getCollection().add(element)
            this.localStorageManager.write(this.instance.getLocalStorage(), this.instance.getCollection())
            return element;    
        }else{
            throw new Error('UPDATE ERROR: Error al actualizar el elemento');
        }
    }

    async delete(key: string): Promise<void> {
        if(this.instance.getCollection().exists(key)){
            this.instance.getCollection().remove(key);
            this.localStorageManager.write(this.instance.getLocalStorage(), this.instance.getCollection())
        }else throw new Error('DELETE ERROR: No se encontro el elemento');
    }
}

/*
 * 
 * UTILITIES INTERFACE, FOR EXAMPLE COLLECTION TO MANAGE A LIST OF OBJECTS
 * 
 */

interface ILocalStorage<T extends ICollectable> {
    read(model:string, instance: T): ICollection<T>;
    write(model:string, collection: ICollection<T>): void;
}

export interface ICollection<T extends ICollectable> {
    size(): number;
    isEmpty(): boolean;
    exists(key: string): boolean;
    get(key: string): T;
    add(element: T): void;
    remove(key: string): void;
    sort(filter: IFilter): void;
    forEach(callbackfn: (value: T, index: string) => void): void;
    filter(filter: IFilter): ICollection<T>;
    slice(start: number, end: number): ICollection<T>;
    paginate(pageNum: number, totalPage:number): ICollection<T>;
    toArray(): T[];
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

/*
 * 
 * CONCRETE IMPLEMENTATION OF UTILITIES INTERFACES
 * 
 */


class LocalStorage<T extends ICollectable> implements ILocalStorage<T> {
    private instance: IStorable<T>;

    constructor(instance: IStorable<T>) {
        this.instance = instance;
    }

    read(model:string, instance: T): ICollection<T> {
        let collection: ICollection<T> = new Collection<T>();
        if(localStorage.getItem(model)){
            let array: string[] = JSON.parse(localStorage.getItem(model) || '')
            let arrayAux: T[] = []
            array.forEach(element => {
                arrayAux.push(Object.assign(this.instance.getInstance(),JSON.parse(element)))
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

export class FilterBuilder {
    private filter: IFilter = {};

    constructor(){
    }

    setSelectedFields(fields: string[]): void {
        this.filter.selectedFields = fields;
    }

    setPageNumAndItems(pageNum: number, pageItems: number): void {
        this.filter.pageNum = pageNum;
        this.filter.pageItems = pageItems;
    }

    addField(field: string, value:any): void {
        if(!this.filter.fields) this.filter.fields = new Map<string,any>();
        this.filter.fields?.set(field, value);
    }

    addInterval(field: string, startValue: any, endValue: any): void {
        if(!this.filter.intervalFields) this.filter.intervalFields = new Map<string,any>();
        this.filter.intervalFields?.set(field, {
            start: startValue,
            end: endValue
        });
    }

    addOrder(field:string, order: string): void {
        if(!this.filter.orderBy) this.filter.orderBy = new Map<string,string>();
        this.filter.orderBy?.set(field, order);
        // if(!this.filter.orderBy) this.filter.orderBy = field + ";" + order;
        // else this.filter.orderBy += "/" + field + ";" + order;
    }

    getFilter(): IFilter {
        return this.filter;
    }

    clearAll(): void {
        this.filter = {};
    }

    clearFields(): void {
        delete this.filter.fields;
    }

    clearInterval(): void {
        delete this.filter.intervalFields;
    }

    clearSelectedFields(): void {
        delete this.filter.selectedFields;
    }

    clearPageNumAndItems(): void {
        delete this.filter.pageNum;
        delete this.filter.pageItems;
    }
}

export class Collection<T extends ICollectable> implements ICollection<T> {
    private data: Map<string,T>;

    constructor() {
        this.data = new Map<string, T>();
    }

    size(): number {
        return this.data.size;
    }

    isEmpty(): boolean {
        return this.data.size == 0 ? true : false;
    }

    exists(key: string): boolean {
        return this.data.has(key);
    }

    get(key: string): T {
        const element = this.data.get(key);
        if(element) return element; else throw new Error("Element not found in collection");
    }

    add(element: T): void {
        this.data.set(element.Key, element);
    }

    remove(key: string): void {
        this.data.delete(key);
    }

    sort(filter: IFilter): void {
        if(this.isEmpty()) throw new Error('SORT ERROR: No hay elementos para ordenar');
        if(filter.orderBy == undefined) throw new Error('SORT ERROR: No esta definido el filtro para ordenar');
        filter.orderBy.forEach((key, value) => {
            if(this.toArray()[0].getSortableFields().get(value.toLowerCase()) == undefined) throw new Error('SORT ERROR: No campo a ordenar no existe o no se puede ordenar');
            if(key.toLowerCase() != 'asc' && key.toLowerCase() != 'desc') throw new Error('SORT ERROR: Orden invalido');
        });
        let orders = Array.from(filter.orderBy?.entries())        
        let sortFuction = (left: T, right: T, n: number): number => {
            if(left.getSortableFields().get(orders[n][0]) < right.getSortableFields().get(orders[n][0])) return orders[n][1] == 'desc' ? 1 : -1;
            if(left.getSortableFields().get(orders[n][0]) > right.getSortableFields().get(orders[n][0])) return orders[n][1] == 'desc' ? -1 : 1;
            if(orders[n+1]) return sortFuction(left, right, n+1);
            else return 0;
        }
        let array = this.toArray().sort((left, right) => {
            let n = 0;
            return sortFuction(left, right, n);
        })
        this.data.clear();
        this.copyArrayToCollection(array);
    }

    forEach(callbackfn: (value: T, index: string) => void): void {
        this.data.forEach((element, key) => {
            callbackfn(element, key);
        })
    }

    filter(filter: IFilter): ICollection<T> {
        if(this.isEmpty()) throw new Error('FILTER ERROR: No hay elementos para filtrar');
        if(filter.fields?.size == 0 && filter.intervalFields?.size == 0) throw new Error('FILTER ERROR: No esta definido el filtro para filtrar');
        let filteredArray = this.toArray();
        if(filter.fields?.size != 0)
            filter.fields?.forEach((value, key) => {
                filteredArray = filteredArray.filter(element => 
                    element.getFilterableFields().get(key.toLowerCase()) == value
                )
            })
        if(filter.intervalFields?.size != 0)
            filter.intervalFields?.forEach((value, key) => {
                filteredArray = filteredArray.filter(element => 
                    value.end >= element.getFilterableFields().get(key.toLowerCase()) >= value.start
                )
            })
        let collection = new Collection<T>();
        collection.copyArrayToCollection(filteredArray);
        return collection;
    }

    slice(start: number, end: number): ICollection<T> {
        let collection = new Collection<T>()
        collection.copyArrayToCollection(this.toArray().slice(start,end));
        return collection;
    }

    paginate(pageNum: number, pageItems: number): ICollection<T> {
        let collection = new Collection<T>();
        collection.copyArrayToCollection(this.toArray().slice((pageNum - 1) * pageItems, pageNum * pageItems));
        return collection;
    }

    toArray(): T[] {
        return Array.from(this.data.values());
    }

    copyArrayToCollection(array: T[]): void {
        array.forEach((element:T) => {
            this.add(element);
        })
    }
}

/*
 * 
 * INTERFACES FOR CONCRETE CLASSES
 * 
 */

interface IFactory {
    createDocument() : IDocument;
    createFolder() : IFolder;
}

interface ICollectable {
    Key: string;
    getKey(): string;
    getFilterableFields(): Map<string,any>;
    getSortableFields(): Map<string,any>;
}

interface IStorable<T extends ICollectable> {
    getCollection(): ICollection<T>;
    getLocalStorage(): string;
    getInstance(): T;
}

export interface IDocument extends ICollectable {
    File: string;
    FileName: string;
    FileSize: number;
    FileType: string;
    Path: string;
    Date: Date;
}

export interface IFolder extends ICollectable {
    Name: string;
    Path: string;
    Parent: string;
}

/*
 * 
 * IMPLEMENTATION OF INTERFACES FOR CONCRETE CLASSES
 * 
 */

export class Factory implements IFactory {
    createDocument(file?: string, fileName?: string, fileSize?: number, fileType?: string, date?: Date, path?: string): IDocument {
        return new Document(file, fileName, fileSize, fileType, date, path);
    }
    createFolder(name?: string, parent?: string): IFolder {
        return new Folder(name, parent);
    }
}

class Document implements IDocument {
    private file: string;
    private fileName: string;
    private fileSize: number;
    private fileType: string;
    private path: string;
    private date: Date;
    private key: string;

    constructor(file?: string, fileName?: string, fileSize?: number, fileType?: string, date?: Date, path?: string) {
        this.file = file || '';
        this.fileName = fileName || '';
        this.fileSize = fileSize || 0;
        this.fileType = fileType || '';
        this.path =  path || '';
        this.date = date || new Date();
        this.key = path || '';
    }

    public get FileName(): string {
        return this.fileName;
    }

    public get FileSize(): number {
        return this.fileSize;
    }

    public get FileType(): string {
        return this.fileType;
    }

    public get Date(): Date {
        return this.date;
    }

    public get File(): string {
        return this.file;
    }

    public get Path(): string {
        return this.path;
    }

    public set File(file: string) {
        this.file = file;
    }

    public set FileName(fileName: string) {
        this.fileName = fileName;
    }

    public set FileSize(fileSize: number) {
        this.fileSize = fileSize;
    }

    public set FileType(fileType: string) {
        this.fileType = fileType;
    }

    public set Date(date: Date) {
        this.date = date;
    }

    public set Path(path: string) {
        this.path = path;
    }

    public get Key(): string {
        return this.key;
    }

    public set Key(key: string) {
        this.key = key;
    } 

    getKey(): string {
        return this.path;
    }

    getFilterableFields(): Map<string,any> {
        let map = new Map<string, any>();
        map.set('filename', this.FileName);
        map.set('filesize', this.FileSize);
        map.set('filetype', this.FileType);
        return map;
    }

    getSortableFields(): Map<string,any> {
        let map = new Map<string, any>();
        map.set('filename', this.FileName);
        map.set('filesize', this.FileSize);
        map.set('filetype', this.FileType);
        return map;
    }
}

class StorableDocument extends Document implements IStorable<IDocument> {
    getCollection(): ICollection<IDocument> {
        return documents;
    }
    getLocalStorage(): string {
        return 'documents';
    }
    getInstance(): IDocument {
        return new Factory().createDocument();
    }
}

class Folder implements IFolder {
    private name: string;
    private path: string;
    private parent: string;
    private key: string;

    constructor(name?: string, parent?: string, path?: string) {
        this.name = name || '';
        this.path = parent?.toLowerCase().split(' ').join('_') + '/' + name?.split(' ').join('_') || '';
        this.parent = parent || '';
        this.key = this.path || '';
    }

    public get Name(): string {
        return this.name;
    }

    public set Name(value: string) {
        this.name = value;
    }

    public get Path(): string {
        return this.path;
    }

    public set Path(value: string) {
        this.path = value;
    }

    public get Parent(): string {
        return this.parent;
    }

    public set Parent(value: string) {
        this.parent = value;
    }

    public get Key(): string {
        return this.key;
    }

    public set Key(value: string) {
        this.key = value;
    }

    getKey(): string {
        return this.path;
    }

    getFilterableFields(): Map<string,any> {
        let map = new Map<string, any>();
        map.set('name', this.Name);
        map.set('parent', this.Parent);
        return map;
    }

    getSortableFields(): Map<string,any> {
        let map = new Map<string, any>();
        map.set('name', this.Name);
        map.set('parent', this.Parent);
        return map;
    }
}

class StorableFolder extends Folder implements IStorable<IFolder> {
    getCollection(): ICollection<IFolder> {
        return folders;
    }
    getLocalStorage(): string {
        return 'folders';
    }
    getInstance(): IFolder {
        return new Folder();
    }
}

/**
 * 
 * READ FROM LOCAL STORAGE ON PROYECT START FOR DEVELOPMENT
 * 
 */

let documents: ICollection<IDocument> = new Collection<IDocument>();
let storableDocuments = new StorableDocument();
let localDocuments = new LocalStorage<IDocument>(new StorableDocument());
documents = localDocuments.read(storableDocuments.getLocalStorage(),storableDocuments.getInstance())
// documents.add(new Document('', 'file1', 1, 'pdf', new Date(), 'path1'));
// documents.add(new Document('', 'file2', 2, 'pdf', new Date(), 'path2'));
// documents.add(new Document('', 'file3', 3, 'jpg', new Date(), 'path3'));
// documents.add(new Document('', 'file4', 4, 'pdf', new Date(), 'path4'));
// documents.add(new Document('', 'file5', 5, 'pdf', new Date(), 'path5'));
// documents.add(new Document('', 'file6', 6, 'jpg', new Date(), 'path6'));

let folders: ICollection<IFolder> = new Collection<IFolder>();
let storableFolders = new StorableFolder();
let localFolders = new LocalStorage<IFolder>(new StorableFolder());
folders = localFolders.read(storableFolders.getLocalStorage(),storableFolders.getInstance())
// folders.add(new Folder('A contabilizar', '/', '/a_contabilizar',));
// folders.add(new Folder('Contabilizado', '/', '/contabilizado',));
// folders.add(new Folder('Papelera', '/', '/papelera',));
// folders.add(new Folder('Fiscal', '/', '/fiscal',));
// folders.add(new Folder('Laboral', '/', '/laboral',));
// folders.add(new Folder('Maria Rico Gómez', '/laboral', '/laboral/48150243L',));
// folders.add(new Folder('Juan Carlos Aragón Pérez', '/laboral', '/laboral/11556837G',));
// folders.add(new Folder('Maria Rico Álvarez', '/laboral', '/laboral/86638678R',));
