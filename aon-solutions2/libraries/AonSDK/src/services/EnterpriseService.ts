import { IEnterprise, IRegistryEnterprise } from "../interfaces/modelsInterfaces";
import { IEnterpriseSpecificMethodsRepository } from "../interfaces/repositoryInterfaces";
import { IEnterpriseSpecificMethods } from "../interfaces/serviceInterfaces";
import { IResponse } from "../interfaces/utilitiesInterfaces";
import { Enterprise } from "../models/Enterprise";
import { RegistryEnterprise } from "../models/RegistryEnterprise";
import { ErrorResponse } from "../utils/Response";
import { Response } from "../utils/Response";

export class EnterpriseSpecificMethods implements IEnterpriseSpecificMethods {
    protected SpecificMethodsRepository: IEnterpriseSpecificMethodsRepository;

    constructor(SpecificMethodsRepository: IEnterpriseSpecificMethodsRepository){
        this.SpecificMethodsRepository = SpecificMethodsRepository;
    }

    /**
     * Recupera los datos de la empresa actual.
     *
     * @return {Promise<IResponse<IEnterprise>>} Una promesa que se resuelve en un objeto IResponse<IEnterprise>.
     */
    async getCurrentEnterpriseData(): Promise<IResponse<IEnterprise>> {
        try {
            return new Response<IEnterprise>(await this.SpecificMethodsRepository.getCurrentEnterpriseData());
        } catch (error) {
            throw error instanceof ErrorResponse ?  error : new ErrorResponse('0123', 'Enterprise');
        }
    }

    /**
     * Recupera los datos del registro de la empresa actual.
     *
     * @return {Promise<IResponse<IRegistryEnterprise>>} Una promesa que se resuelve en los datos del registro de la empresa.
     */
    async getCurrentEnterpriseRegistryData(): Promise<IResponse<IRegistryEnterprise>> {
        try {
            return new Response<IRegistryEnterprise>(await this.SpecificMethodsRepository.getCurrentEnterpriseRegistryData());
        } catch (error) {
            throw error instanceof ErrorResponse ?  error : new ErrorResponse('0123', 'Enterprise Registry');
        }
    }

    /**
     * Actualiza los datos de la empresa actual.
     *
     * @param {Enterprise} enterprise - El objeto de la empresa que contiene los datos actualizados.
     * @return {Promise<IResponse<IEnterprise>>} Una promesa que se resuelve en la respuesta que contiene los datos actualizados de la empresa.
     */
    async updateCurrentEnterpriseData(enterprise: Enterprise): Promise<IResponse<IEnterprise>> {
        try {
            return new Response<IEnterprise>(await this.SpecificMethodsRepository.updateCurrentEntepriseData(enterprise));
        } catch (error) {
            throw error instanceof ErrorResponse ?  error : new ErrorResponse('0202', 'Enterprise');
        }
    }

    /**
     * Actualiza los datos del registro de la empresa actual.
     *
     * @param {RegistryEnterprise} registryEnterprise - El registro de la empresa a actualizar.
     * @return {Promise<IResponse<IRegistryEnterprise>>} Una promesa que se resuelve con el registro de la empresa actualizado.
     */
    async updateCurrentEnterpriseRegistryData(registryEnterprise: RegistryEnterprise): Promise<IResponse<IRegistryEnterprise>> {
        try {
            return new Response<IRegistryEnterprise>(await this.SpecificMethodsRepository.updateCurrentEnterpriseRegistryData(registryEnterprise));
        } catch (error) {
            throw error instanceof ErrorResponse ?  error : new ErrorResponse('0202', 'Enterprise Registry');
        }
    }
}
