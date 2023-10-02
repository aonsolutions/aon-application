/*
 *
 * FACTORYS INTERFACES
 *
 */

import { ICollectable } from "./modelsInterfaces";
import { ISingleObjectCreator, ISingleObjectUpdater, ISingleObjectRemover, ISingleObjectReader, IMultipleObjectCreator, IMultipleObjectUpdater, IMultipleObjectRemover, IMultipleObjectReader, IAuthenticationManager, IReportingDataAccess } from "./serviceInterfaces";

export interface ISingleObjectCrudFactory<T extends ICollectable> {
    /**
     * Create a class for available single object crud methods, that will be used to create, update, delete or get a single object.
     */
    createSingleObjectCrud(): ISingleObjectCreator<T> | ISingleObjectUpdater<T> | ISingleObjectRemover<T> | ISingleObjectReader<T>;
}

export interface IMultipleObjectCrudFactory<T extends ICollectable> {
    /**
     * Create a class for available multiple object crud methods, that will be used to create, update, delete or get a collection of objects.
     */
    createMultipleObjectCrud(): IMultipleObjectCreator<T> | IMultipleObjectUpdater<T> | IMultipleObjectRemover<T> | IMultipleObjectReader<T>;
}

export interface IAuthenticationManagerFactory {
    /**
     * Create the authentication manager instance that contains the methods to manage authentication
     */
    createAuthenticationManager(): IAuthenticationManager;
}

export interface IReportingDataAccessFactory {
    /**
     * Create the reporting data access instance that contains the methods to manage reporting
     */
    createReportingDataAccess(): IReportingDataAccess;
}