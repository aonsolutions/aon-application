import { IProductStatus, IProductCode, IProductType, IProductClass, IProductCategory } from "../interfaces/modelsInterfaces";
import { IProductSpecificMethods } from "../interfaces/serviceInterfaces";
import { IFilter, IResponse, ICollection } from "../interfaces/utilitiesInterfaces";
import { LocalProductSpecificMethodsRepository } from "../repositorys/ProductRepository";
import { ErrorResponse } from "../utils/Response";
import { Response } from "../utils/Response";

export class ProductSpecificMethods implements IProductSpecificMethods {

    protected SpecificMethodsRepository: LocalProductSpecificMethodsRepository;

    constructor(ProductSpecificMethodsRepository: LocalProductSpecificMethodsRepository) {
        this.SpecificMethodsRepository = ProductSpecificMethodsRepository;
    }

    async getProductStatus(filter?: IFilter): Promise<IResponse<ICollection<IProductStatus>>> {
        try{
            return new Response<ICollection<IProductStatus>>(this.SpecificMethodsRepository.getProductStatus(filter));
        }catch(error){
            throw error instanceof ErrorResponse ?  error : new ErrorResponse('0123');
        }
    }

    async getProductCodes(filter?: IFilter): Promise<IResponse<ICollection<IProductCode>>> {
        try{
            return new Response<ICollection<IProductCode>>(this.SpecificMethodsRepository.getProductCodes(filter));
        }catch(error){
            throw error instanceof ErrorResponse ?  error : new ErrorResponse('0123');
        }
    }

    async getProductTypes(filter?: IFilter): Promise<IResponse<ICollection<IProductType>>> {
        try{
            return new Response<ICollection<IProductType>>(this.SpecificMethodsRepository.getProductTypes(filter));
        }catch(error){
            throw error instanceof ErrorResponse ?  error : new ErrorResponse('0123');
        }
    }

    async getProductClasses(filter?: IFilter): Promise<IResponse<ICollection<IProductClass>>> {
        try{
            return new Response<ICollection<IProductClass>>(this.SpecificMethodsRepository.getProductClasses(filter));
        }catch(error){
            throw error instanceof ErrorResponse ?  error : new ErrorResponse('0123');
        }
    }

    async getProductCategories(filter?: IFilter): Promise<IResponse<ICollection<IProductCategory>>> {
        try{
            return new Response<ICollection<IProductCategory>>(this.SpecificMethodsRepository.getProductCategories(filter));
        }catch(error){
            throw error instanceof ErrorResponse ?  error : new ErrorResponse('0123');
        }
    }

}