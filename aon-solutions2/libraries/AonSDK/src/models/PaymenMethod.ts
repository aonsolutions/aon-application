import { IPaymentMethod, IModel, IStorable, IApiModel } from "../interfaces/modelsInterfaces";
import { ICollection, IFilter } from "../interfaces/utilitiesInterfaces";
import { Collection } from "../utils/Collection";
import { SimpleValue } from "./SimpleValue";

export class PaymentMethod extends SimpleValue implements IPaymentMethod, IModel {}

export class StorablePaymentMethod implements IStorable<PaymentMethod> {
    getCollection(): ICollection<PaymentMethod> {
        return paymentMethods;
    }
    getLocalStorage(): string {
        return 'paymentMethods';
    }
}

export class ApiPaymentMethod implements IApiModel {
    getUrl(currentMethod: string, filter?: IFilter | undefined): string[] {
        throw new Error("Method not implemented.");
    }
    getMethod(currentMethod: string, filter?: IFilter | undefined): string {
        throw new Error("Method not implemented.");
    }
    parseDataToSend(data: any, currentMethod?: string | undefined, filter?: IFilter | undefined) {
        throw new Error("Method not implemented.");
    }
    parseDataToReceive(data: any, currentMethod?: string | undefined, filter?: IFilter | undefined) {
        throw new Error("Method not implemented.");
    }
    localFilter(currentMethod?: string | undefined, filter?: IFilter | undefined): boolean {
        throw new Error("Method not implemented.");
    }
}

export let paymentMethods: ICollection<PaymentMethod> = new Collection<PaymentMethod>();
export function setPaymentMethods(value: any) { paymentMethods = value; };