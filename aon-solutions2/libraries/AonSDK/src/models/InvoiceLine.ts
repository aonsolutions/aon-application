import { IInvoiceLine, IModel, IProduct, ITax, IIRPF } from "../interfaces/modelsInterfaces";
import { KeyGenerator } from "../utils/KeyGenerator";
import { IRPF } from "./IRPF";
import { Product } from "./Product";
import { Tax } from "./Tax";

export class InvoiceLine implements IInvoiceLine, IModel {
    key: string;
    apiObject: any;
    product: IProduct;
    quantity: number;
    price: number;
    discount: number;
    totalPrice: number;
    tax: ITax;
    irpf: IIRPF;

    constructor(product?: IProduct,quantity?: number,price?: number,discount?: number,totalPrice?: number,tax?: ITax,irpf?: IIRPF){
        this.apiObject = {};
        this.key = KeyGenerator.generate(15);
        this.product = product || new Product();
        this.quantity = quantity || 0;
        this.price = price || 0;
        this.discount = discount || 0;
        this.totalPrice = totalPrice || 0;
        this.tax = tax || new Tax();
        this.irpf = irpf || new IRPF();
    }
    getProduct(): IProduct {
        return this.product
    }
    setProduct(value: IProduct): IInvoiceLine {
        this.product = value;
        return this
    }
    getQuantity(): number {
        return this.quantity
    }
    setQuantity(value: number): IInvoiceLine {
        this.quantity = value;
        return this
    }
    getPrice(): number {
        return this.price
    }
    setPrice(value: number): IInvoiceLine {
        this.price = value;
        return this
    }
    getDiscount(): number {
        return this.discount
    }
    setDiscount(value: number): IInvoiceLine {
        this.discount = value;
        return this
    }
    getTotalPrice(): number {
        return this.totalPrice
    }
    setTotalPrice(value: number): IInvoiceLine {
        this.totalPrice = value;
        return this
    }
    getTax(): ITax {
        return this.tax
    }
    setTax(value: ITax): IInvoiceLine {
        this.tax = value;
        return this
    }
    getIRPF(): IIRPF {
        return this.irpf
    }
    setIRPF(value: IIRPF): IInvoiceLine {
        this.irpf = value;
        return this
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

    public get Product(): IProduct {
        return this.product;
    }

    public set Product(value: IProduct) {
        this.product = value;
    }

    public get Quantity(): number {
        return this.quantity;
    }

    public set Quantity(value: number) {
        this.quantity = value;
    }

    public get Price(): number {
        return this.price;
    }

    public set Price(value: number) {
        this.price = value;
    }

    public get Discount(): number {
        return this.discount;
    }

    public set Discount(value: number) {
        this.discount = value;
    }

    public get TotalPrice(): number {
        return this.totalPrice;
    }

    public set TotalPrice(value: number) {
        this.totalPrice = value;
    }

    public get Tax(): ITax {
        return this.tax;
    }

    public set Tax(value: ITax) {
        this.tax = value;
    }

    public get IRPF(): IIRPF {
        return this.irpf;
    }

    public set IRPF(value: IIRPF) {
        this.irpf = value;
    }

    getKey(): string {
        return this.key;
    }

    getFilterableFields(): Map<string, any> {
        return new Map<string,any>();
    }

    getSortableFields(): Map<string, any> {
        return new Map<string,any>();
    }
}