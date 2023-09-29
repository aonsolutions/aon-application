import { ProductFactory } from "../factorys/ProductFactory";
import { IProduct, IProductCategory, IProductClass, IProductCode, IProductStatus, IProductType } from "../interfaces/modelsInterfaces";
import { ICollection, IFilter, IResponse } from "../interfaces/utilitiesInterfaces";

export class ProductFunctions {
    factory = new ProductFactory();

    async getProduct(key: string): Promise<IResponse<IProduct>> {
        return (await this.factory.createSingleObjectCrud().getElement(key))
    }

    async getProductList(filter?: IFilter): Promise<IResponse<ICollection<IProduct>>> {
        return (await this.factory.createMultipleObjectCrud().getCollection(filter))
    }

    async createProduct(product: IProduct): Promise<IResponse<IProduct>> {
        return (await this.factory.createSingleObjectCrud().createElement(product))
    }

    async updateProduct(product: IProduct): Promise<IResponse<IProduct>> {
        return (await this.factory.createSingleObjectCrud().updateElement(product))
    }

    async deleteProduct(key: string): Promise<IResponse<boolean>> {
        return (await this.factory.createSingleObjectCrud().deleteElement(key))
    }

    async getProducCodeList(filter?: IFilter): Promise<IResponse<ICollection<IProductCode>>> {
        return (await this.factory.createSpecificMethods().getProductCodes(filter));
    }

    async getProductTypeList(filter?: IFilter): Promise<IResponse<ICollection<IProductType>>> {
        return (await this.factory.createSpecificMethods().getProductTypes(filter))
    }

    async getProductClassList(filter?: IFilter): Promise<IResponse<ICollection<IProductClass>>> {
        return (await this.factory.createSpecificMethods().getProductClasses(filter))
    }

    async getProductCategoryList(filter?: IFilter): Promise<IResponse<ICollection<IProductCategory>>> {
        return (await this.factory.createSpecificMethods().getProductCategories(filter))
    }

    async getProductStatusList(filter?: IFilter): Promise<IResponse<ICollection<IProductStatus>>> {
        return (await this.factory.createSpecificMethods().getProductStatus(filter))
    }
}