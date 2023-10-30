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

    async createElement(element: T): Promise<IResponse<T>> {
        try {
            return new Response<T>(await this.repository.create(element));
        } catch (error) {
            throw error instanceof ErrorResponse ?  error : new ErrorResponse('0201');
        }
    }

    async updateElement(element: T): Promise<IResponse<T>> {
        try {
            return new Response<T>(await this.repository.update(element));
        } catch (error) {
            throw error instanceof ErrorResponse ?  error : new ErrorResponse('0202');
        }
    }

    async deleteElement(key: string): Promise<IResponse<boolean>> {
        try {
            this.repository.delete(key);
            return new Response<boolean>(true);
        } catch (error) {
            throw error instanceof ErrorResponse ?  error : new ErrorResponse('0203');
        }
    }

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

    constructor(repository: IMultipleObjectCrudRepository<T>, type: { new (): T }){
        this.repository = repository;
        this.type = type;
    }

    async getCollection(filter?: IFilter): Promise<IResponse<ICollection<T>>> {
        try {
            throw new Error('Method not implemented.');
            return new Response<ICollection<T>>(await this.repository.get(filter));
        } catch (error) {
            // throw error;
            throw error instanceof ErrorResponse ?  error : new ErrorResponse('0206', this.type.name);
        }
    }

    async createCollection(collection: ICollection<T>): Promise<IResponse<ICollection<T>>> {
        try {
            return new Response<ICollection<T>>(await this.repository.create(collection));
        } catch (error) {
            throw error instanceof ErrorResponse ?  error : new ErrorResponse('0201', this.type.name);
        }
    }

    async updateCollection(collection: ICollection<T>): Promise<IResponse<ICollection<T>>> {
        try {
            return new Response<ICollection<T>>(await this.repository.update(collection));
        } catch (error) {
            throw error instanceof ErrorResponse ?  error : new ErrorResponse('0202', this.type.name);
        }
    }

    async deleteCollection(keys: string[]): Promise<IResponse<boolean>> {
        try {
            this.repository.delete(keys);
            return new Response<boolean>(true);
        } catch (error) {
            throw error instanceof ErrorResponse ?  error : new ErrorResponse('0203', this.type.name);
        }
    }
}
