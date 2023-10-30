import { IModel } from "../interfaces/modelsInterfaces";
import { ISingleObjectCrudRepository, IMultipleObjectCrudRepository } from "../interfaces/repositoryInterfaces";
import { ISingleObjectCrud, IMultipleObjectCrud } from "../interfaces/serviceInterfaces";
import { IResponse, IFilter, ICollection } from "../interfaces/utilitiesInterfaces";
import { ErrorResponse } from "../utils/Response";
import { Response } from "../utils/Response";

export class GenericSingleObjectCrud<T extends IModel> implements ISingleObjectCrud<T> {
    private repository: ISingleObjectCrudRepository<T>;
    private type: { new (): T };

    constructor(repository: ISingleObjectCrudRepository<T>, type: { new (): T }){
        this.repository = repository;
        this.type = type;
    }

    /**
     * Crea un nuevo elemento de forma asíncrona y devuelve la respuesta.
     *
     * @param {T} element - El elemento a crear.
     * @return {Promise<IResponse<T>>} Una promesa que se resuelve con la respuesta de la operación de creación.
     */
    async createElement(element: T): Promise<IResponse<T>> {
        try {
            return new Response<T>(await this.repository.create(element));
        } catch (error) {
            throw error instanceof ErrorResponse ?  error : new ErrorResponse('0201');
        }
    }

    /**
     * Actualiza un elemento.
     *
     * @param {T} elemento - El elemento a actualizar.
     * @return {Promise<IResponse<T>>} Una promesa que se resuelve con la respuesta del elemento actualizado.
     */
    async updateElement(element: T): Promise<IResponse<T>> {
        try {
            return new Response<T>(await this.repository.update(element));
        } catch (error) {
            throw error instanceof ErrorResponse ?  error : new ErrorResponse('0202');
        }
    }

    /**
     * Elimina un elemento con la clave proporcionada.
     *
     * @param {string} key - La clave del elemento a eliminar.
     * @return {Promise<IResponse<boolean>>} - Una promesa que se resuelve a un booleano que indica si el elemento se eliminó correctamente.
     */
    async deleteElement(key: string): Promise<IResponse<boolean>> {
        try {
            this.repository.delete(key);
            return new Response<boolean>(true);
        } catch (error) {
            throw error instanceof ErrorResponse ?  error : new ErrorResponse('0203');
        }
    }

    /**
     * Recupera un elemento basado en la clave proporcionada.
     *
     * @param {string} key - La clave utilizada para recuperar el elemento.
     * @return {Promise<IResponse<T>>} Una promesa que se resuelve con el elemento recuperado .
     */
    async getElement(key: string): Promise<IResponse<T>> {
        try {
            return new Response<T>(await this.repository.get(key));
        } catch (error) {
            throw error instanceof ErrorResponse ?  error : new ErrorResponse('0206');
        }
    }
}

export class GenericMultipleObjectCrud<T extends IModel> implements IMultipleObjectCrud<T> {
    private repository: IMultipleObjectCrudRepository<T>;
    private type: { new (): T };

    /**
     * Construye una nueva instancia de la clase.
     *
     * @param {IMultipleObjectCrudRepository<T>} repository - El repositorio para la clase.
     * @param {{ new (): T }} type - El tipo de la clase.
     */
    constructor(repository: IMultipleObjectCrudRepository<T>, type: { new (): T }){
        this.repository = repository;
        this.type = type;
    }

    /**
     * Recupera una colección de elementos.
     *
     * @param {IFilter} [filter] - Filtro opcional para aplicar a la colección.
     * @return {Promise<IResponse<ICollection<T>>>} - Una promesa que se resuelve en una respuesta que contiene la colección.
     */
    async getCollection(filter?: IFilter): Promise<IResponse<ICollection<T>>> {
        try {
            throw new Error('Method not implemented.');
            return new Response<ICollection<T>>(await this.repository.get(filter));
        } catch (error) {
            // throw error;
            throw error instanceof ErrorResponse ?  error : new ErrorResponse('0206', this.type.name);
        }
    }

    /**
     * Crea una colección.
     *
     * @param {ICollection<T>} collection - La colección a crear.
     * @return {Promise<IResponse<ICollection<T>>>} Una promesa que se resuelve en la respuesta que contiene la colección creada.
     */
    async createCollection(collection: ICollection<T>): Promise<IResponse<ICollection<T>>> {
        try {
            return new Response<ICollection<T>>(await this.repository.create(collection));
        } catch (error) {
            throw error instanceof ErrorResponse ?  error : new ErrorResponse('0201', this.type.name);
        }
    }

    /**
     * Actualiza una colección.
     *
     * @param {ICollection<T>} collection - La colección a actualizar.
     * @return {Promise<IResponse<ICollection<T>>>} - Una promesa que se resuelve en la colección actualizada.
     */
    async updateCollection(collection: ICollection<T>): Promise<IResponse<ICollection<T>>> {
        try {
            return new Response<ICollection<T>>(await this.repository.update(collection));
        } catch (error) {
            throw error instanceof ErrorResponse ?  error : new ErrorResponse('0202', this.type.name);
        }
    }

    /**
     * Elimina una colección.
     *
     * @param {string[]} keys - Las claves a eliminar.
     * @return {Promise<IResponse<boolean>>} Una promesa que se resuelve en una respuesta booleana.
     */
    async deleteCollection(keys: string[]): Promise<IResponse<boolean>> {
        try {
            this.repository.delete(keys);
            return new Response<boolean>(true);
        } catch (error) {
            throw error instanceof ErrorResponse ?  error : new ErrorResponse('0203', this.type.name);
        }
    }
}
