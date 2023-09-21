import { Injectable } from '@angular/core';
import { Factory, ICollection, IFilter, IProduct, IProductCategory, IProductClass, IProductCode, IProductStatus, IProductType, ProductFactory } from 'libraries/AonSDK/aon';
import { CommonService } from './common.service';

@Injectable({
  providedIn: 'root'
})
export class ProductService extends CommonService {

  factory = new ProductFactory();

  constructor(){
    super();
  }

  async getProduct(key: string): Promise<IProduct> {
    return (await this.factory.createSingleObjectCrud().getElement(key)).result
  }

  async getProductList(filter?: IFilter): Promise<ICollection<IProduct>> {
    return (await this.factory.createMultipleObjectCrud().getCollection(filter)).result
  }

  async createProduct(product: IProduct): Promise<IProduct> {
    return (await this.factory.createSingleObjectCrud().createElement(product)).result
  }

  async updateProduct(product: IProduct): Promise<IProduct> {
    return (await this.factory.createSingleObjectCrud().updateElement(product)).result
  }

  async deleteProduct(key: string): Promise<boolean> {
    return (await this.factory.createSingleObjectCrud().deleteElement(key)).result
  }

  async getProducCodeList(filter?: IFilter): Promise<ICollection<IProductCode>> {
    return (await this.factory.createSpecificMethods().getProductCodes(filter)).result;
  }

  async getProductTypeList(filter?: IFilter): Promise<ICollection<IProductType>> {
    return (await this.factory.createSpecificMethods().getProductTypes(filter)).result
  }

  async getProductClassList(filter?: IFilter): Promise<ICollection<IProductClass>> {
    return (await this.factory.createSpecificMethods().getProductClasses(filter)).result
  }

  async getProductCategoryList(filter?: IFilter): Promise<ICollection<IProductCategory>> {
    return (await this.factory.createSpecificMethods().getProductCategories(filter)).result
  }

  async getProductStatusList(filter?: IFilter): Promise<ICollection<IProductStatus>> {
    return (await this.factory.createSpecificMethods().getProductStatus(filter)).result
  }

}
