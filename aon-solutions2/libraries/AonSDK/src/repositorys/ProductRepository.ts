import { ProductCategoryFactory } from "../factorys/ProductCategoryFactory";
import { ProductClassFactory } from "../factorys/ProductClassFactory";
import { ProductCodeFactory } from "../factorys/ProductCodeFactory";
import { ProductStatusFactory } from "../factorys/ProductStatusFactory";
import { ProductTypeFactory } from "../factorys/ProductTypeFactory";
import { IProductStatus, IProductCode, IProductType, IProductClass, IProductCategory } from "../interfaces/modelsInterfaces";
import { IProductSpecificMethodsRepository } from "../interfaces/repositoryInterfaces";
import { IFilter, ICollection } from "../interfaces/utilitiesInterfaces";

export class LocalProductSpecificMethodsRepository implements IProductSpecificMethodsRepository {
    async getProductStatus(filter?: IFilter): Promise<ICollection<IProductStatus>> {
        return (await new ProductStatusFactory().createMultipleObjectCrud().getCollection(filter)).result;
    }
    async getProductCodes(filter?: IFilter): Promise<ICollection<IProductCode>> {
        return (await new ProductCodeFactory().createMultipleObjectCrud().getCollection(filter)).result;
    }
    async getProductTypes(filter?: IFilter): Promise<ICollection<IProductType>> {
        return (await new ProductTypeFactory().createMultipleObjectCrud().getCollection(filter)).result;
    }
    async getProductClasses(filter?: IFilter): Promise<ICollection<IProductClass>> {
        return (await new ProductClassFactory().createMultipleObjectCrud().getCollection(filter)).result;
    }
    async getProductCategories(filter?: IFilter): Promise<ICollection<IProductCategory>> {
        return (await new ProductCategoryFactory().createMultipleObjectCrud().getCollection(filter)).result;
    }
}