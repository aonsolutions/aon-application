import { IMultipleObjectCrudFactory } from "../interfaces/factoryInterfaces";
import { IPaymentMethod } from "../interfaces/modelsInterfaces";
import { IMultipleObjectReader } from "../interfaces/serviceInterfaces";
import { PaymentMethod, StorablePaymentMethod } from "../models/PaymenMethod";
import { GenericMultipleObjectCrudRepository } from "../repositorys/GenericRepository";
import { GenericMultipleObjectCrud } from "../services/GenericCrudService";

export class PaymentMethodFactory implements IMultipleObjectCrudFactory<IPaymentMethod> {
    createMultipleObjectCrud(): IMultipleObjectReader<IPaymentMethod> {
        return new GenericMultipleObjectCrud<PaymentMethod>(new GenericMultipleObjectCrudRepository<PaymentMethod>(new StorablePaymentMethod(), PaymentMethod), PaymentMethod);
    }
}