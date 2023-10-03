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

    async getCurrentEnterpriseData(): Promise<IResponse<IEnterprise>> {
        try {
            return new Response<IEnterprise>(await this.SpecificMethodsRepository.getCurrentEnterpriseData());
        } catch (error) {
            throw error instanceof ErrorResponse ?  error : new ErrorResponse('0123');
        }
    }

    async getCurrentEnterpriseRegistryData(): Promise<IResponse<IRegistryEnterprise>> {
        try {
            return new Response<IRegistryEnterprise>(await this.SpecificMethodsRepository.getCurrentEnterpriseRegistryData());
        } catch (error) {
            throw error instanceof ErrorResponse ?  error : new ErrorResponse('0123');
        }
    }

    async updateCurrentEnterpriseData(enterprise: Enterprise): Promise<IResponse<IEnterprise>> {
        try {
            return new Response<IEnterprise>(await this.SpecificMethodsRepository.updateCurrentEntepriseData(enterprise));
        } catch (error) {
            throw error instanceof ErrorResponse ?  error : new ErrorResponse('0123');
        }
    }

    async updateCurrentEnterpriseRegistryData(registryEnterprise: RegistryEnterprise): Promise<IResponse<IRegistryEnterprise>> {
        try {
            return new Response<IRegistryEnterprise>(await this.SpecificMethodsRepository.updateCurrentEnterpriseRegistryData(registryEnterprise));
        } catch (error) {
            throw error instanceof ErrorResponse ?  error : new ErrorResponse('0123');
        }
    }
}