import { IProduct, IModel, IProductCode, IProductCategory, IProductClass, IProductType, IProductStatus, ITax, IIRPF, IStorable } from "../interfaces/modelsInterfaces";
import { ICollection } from "../interfaces/utilitiesInterfaces";
import { Collection } from "../utils/Collection";
import { KeyGenerator } from "../utils/KeyGenerator";
import { IRPF } from "./IRPF";
import { ProductCategory } from "./ProductCategory";
import { ProductClass } from "./ProductClass";
import { ProductCode } from "./ProductCode";
import { ProductStatus } from "./ProductStatus";
import { ProductType } from "./ProductType";
import { Tax } from "./Tax";

export class Product implements IProduct, IModel {
    private key: string;
    private apiObject: any;
    private code: IProductCode;
    private name: string;
    private category: IProductCategory;
    private class: IProductClass;
    private type: IProductType;
    private status: IProductStatus;
    private tax: ITax;
    private irpf: IIRPF;
    private barCode: string;
    private description: string;
    private costPrice: number;
    private benefit: number;
    private price: number;
    private pvp: number;

    constructor(code?: IProductCode, name?: string, category?: IProductCategory, productClass?: IProductClass, type?: IProductType, status?: IProductStatus,
        tax?: ITax, irpf?: IIRPF, barCode?: string, description?: string, costPrice?: number, benefit?: number, price?: number, pvp?: number) {
        this.apiObject = {};
        this.key = KeyGenerator.generate(15);
        this.code = code || new ProductCode();
        this.name = name || '';
        this.category = category || new ProductCategory();
        this.class = productClass || new ProductClass();
        this.type = type || new ProductType();
        this.status = status || new ProductStatus();
        this.tax = tax || new Tax();
        this.irpf = irpf || new IRPF();
        this.barCode = barCode || '';
        this.description = description || '';
        this.costPrice = costPrice || 0;
        this.benefit = benefit || 0;
        this.price = price || 0;
        this.pvp = pvp || 0;
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

    public get Code() {
        return this.code;
    }

    public set Code(value: IProductCode) {
        this.code = value;
    }

    public get Name() {
        return this.name;
    }

    public set Name(value: string) {
        this.name = value;
    }

    public get Category() {
        return this.category;
    }

    public set Category(value: IProductCategory) {
        this.category = value;
    }

    public get Class() {
        return this.class;
    }

    public set Class(value: IProductClass) {
        this.class = value;
    }

    public get Type() {
        return this.type;
    }

    public set Type(value: IProductType) {
        this.type = value;
    }

    public get Status() {
        return this.status;
    }

    public set Status(value: IProductStatus) {
        this.status = value;
    }

    public get Tax() {
        return this.tax;
    }

    public set Tax(value: ITax) {
        this.tax = value;
    }

    public get IRPF() {
        return this.irpf;
    }

    public set IRPF(value: IIRPF) {
        this.irpf = value;
    }

    public get BarCode() {
        return this.barCode;
    }

    public set BarCode(value: string) {
        this.barCode = value;
    }

    public get Description() {
        return this.description;
    }

    public set Description(value: string) {
        this.description = value;
    }

    public get CostPrice() {
        return this.costPrice;
    }

    public set CostPrice(value: number) {
        this.costPrice = value;
    }

    public get Benefit() {
        return this.benefit;
    }

    public set Benefit(value: number) {
        this.benefit = value;
    }

    public get Price() {
        return this.price;
    }

    public set Price(value: number) {
        this.price = value;
    }

    public get Pvp() {
        return this.pvp;
    }

    public set Pvp(value: number) {
        this.pvp = value;
    }

    getKey(): string {
        return this.key;
    }
    getFilterableFields(): Map<string, any> {
        let map = new Map<string, any>();
        map.set('code', this.code);
        map.set('name', this.name);
        map.set('category', this.category);
        map.set('class', this.class);
        map.set('type', this.type);
        map.set('status', this.status);
        map.set('tax', this.tax);
        map.set('irpf', this.irpf);
        map.set('barCode', this.barCode);
        map.set('description', this.description);
        map.set('costPrice', this.costPrice);
        map.set('benefit', this.benefit);
        map.set('price', this.price);
        map.set('pvp', this.pvp);
        return map;
    }
    getSortableFields(): Map<string, any> {
        let map = new Map<string, any>();
        map.set('code', this.code);
        map.set('name', this.name);
        map.set('category', this.category);
        map.set('class', this.class);
        map.set('type', this.type);
        map.set('status', this.status);
        map.set('tax', this.tax);
        map.set('irpf', this.irpf);
        map.set('barCode', this.barCode);
        map.set('description', this.description);
        map.set('costPrice', this.costPrice);
        map.set('benefit', this.benefit);
        map.set('price', this.price);
        map.set('pvp', this.pvp);
        return map;
    }
}

export class StorableProduct implements IStorable<Product> {
    getCollection(): ICollection<Product> {
        return products;
    }
    getLocalStorage(): string {
        return 'products';
    }
}

export let products: ICollection<Product> = new Collection<Product>();
export function setProducts(value: any) { products = value; };