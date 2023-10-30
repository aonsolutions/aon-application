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

    /**
     * Obtiene el estado del producto.
     *
     * @param {IFilter} filter - Filtro opcional para aplicar al estado del producto.
     * @return {Promise<IResponse<ICollection<IProductStatus>>>} Una promesa que se resuelve en la respuesta que contiene la colección de estados de producto.
     */
    async getProductStatus(filter?: IFilter): Promise<IResponse<ICollection<IProductStatus>>> {
        try{
            return new Response<ICollection<IProductStatus>>(this.SpecificMethodsRepository.getProductStatus(filter));
        }catch(error){
            throw error instanceof ErrorResponse ?  error : new ErrorResponse('0123', 'Product Status');
        }
    }

    /**
     * Recupera los códigos de producto basados en el filtro(opcional) proporcionado.
     *
     * @param {IFilter} [filtro] - Filtro opcional para aplicar a los códigos de producto.
     * @return {Promise<IResponse<ICollection<IProductCode>>>} Una promesa que resuelve a la respuesta que contiene la colección de códigos de producto.
     */
    async getProductCodes(filter?: IFilter): Promise<IResponse<ICollection<IProductCode>>> {
        try{
            return new Response<ICollection<IProductCode>>(this.SpecificMethodsRepository.getProductCodes(filter));
        }catch(error){
            throw error instanceof ErrorResponse ?  error : new ErrorResponse('0123', 'Product Code');
        }
    }

    /**
     * Recupera los tipos de productos basados en el filtro(opcional) proporcionado.
     *
     * @param {IFilter} filter - El filtro a aplicar a los tipos de productos.
     * @return {Promise<IResponse<ICollection<IProductType>>>} Una promesa que se resuelve en la respuesta que contiene una colección de tipos de productos.
     */
    async getProductTypes(filter?: IFilter): Promise<IResponse<ICollection<IProductType>>> {
        try{
            return new Response<ICollection<IProductType>>(this.SpecificMethodsRepository.getProductTypes(filter));
        }catch(error){
            throw error instanceof ErrorResponse ?  error : new ErrorResponse('0123', 'Product Type');
        }
    }

    /**
     * Obtiene las clases de productos basadas en el filtro(opcional) especificado.
     *
     * @param {IFilter} [filter] - El filtro a aplicar a las clases de productos.
     * @return {Promise<IResponse<ICollection<IProductClass>>>} Una promesa que se resuelve con la respuesta que contiene la colección de clases de productos.
     */
    async getProductClasses(filter?: IFilter): Promise<IResponse<ICollection<IProductClass>>> {
        try{
            return new Response<ICollection<IProductClass>>(this.SpecificMethodsRepository.getProductClasses(filter));
        }catch(error){
            throw error instanceof ErrorResponse ?  error : new ErrorResponse('0123', 'Product Class');
        }
    }

    /**
     * Recupera las categorías de productos con un filtro opcional.
     *
     * @param {IFilter} filter - Un filtro opcional para aplicar a las categorías de productos.
     * @return {Promise<IResponse<ICollection<IProductCategory>>>} Una promesa que se resuelve en una respuesta que contiene una colección de categorías de productos.
     */
    async getProductCategories(filter?: IFilter): Promise<IResponse<ICollection<IProductCategory>>> {
        try{
            return new Response<ICollection<IProductCategory>>(this.SpecificMethodsRepository.getProductCategories(filter));
        }catch(error){
            throw error instanceof ErrorResponse ?  error : new ErrorResponse('0123', 'Product Category');
        }
    }

}
