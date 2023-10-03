import { ProductFactory } from "../factorys/ProductFactory";
import { IProduct, IProductCategory, IProductClass, IProductCode, IProductStatus, IProductType } from "../interfaces/modelsInterfaces";
import { ICollection, IFilter, IResponse } from "../interfaces/utilitiesInterfaces";

export class ProductFunctions {
    private static factory = new ProductFactory();

    static async getProduct(key: string): Promise<IResponse<IProduct>> {
        return (await this.factory.createSingleObjectCrud().getElement(key))
    }

    static async getProductList(filter?: IFilter): Promise<IResponse<ICollection<IProduct>>> {
        return (await this.factory.createMultipleObjectCrud().getCollection(filter))
    }

    static async createProduct(product: IProduct): Promise<IResponse<IProduct>> {
        return (await this.factory.createSingleObjectCrud().createElement(product))
    }

    static async updateProduct(product: IProduct): Promise<IResponse<IProduct>> {
        return (await this.factory.createSingleObjectCrud().updateElement(product))
    }

    static async deleteProduct(key: string): Promise<IResponse<boolean>> {
        return (await this.factory.createSingleObjectCrud().deleteElement(key))
    }

    static async getProducCodeList(filter?: IFilter): Promise<IResponse<ICollection<IProductCode>>> {
        return (await this.factory.createSpecificMethods().getProductCodes(filter));
    }

    static async getProductTypeList(filter?: IFilter): Promise<IResponse<ICollection<IProductType>>> {
        return (await this.factory.createSpecificMethods().getProductTypes(filter))
    }

    static async getProductClassList(filter?: IFilter): Promise<IResponse<ICollection<IProductClass>>> {
        return (await this.factory.createSpecificMethods().getProductClasses(filter))
    }

    static async getProductCategoryList(filter?: IFilter): Promise<IResponse<ICollection<IProductCategory>>> {
        return (await this.factory.createSpecificMethods().getProductCategories(filter))
    }

    static async getProductStatusList(filter?: IFilter): Promise<IResponse<ICollection<IProductStatus>>> {
        return (await this.factory.createSpecificMethods().getProductStatus(filter))
    }
}