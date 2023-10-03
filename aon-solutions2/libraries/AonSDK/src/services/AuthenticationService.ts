import { IEnterprise } from "../interfaces/modelsInterfaces";
import { IAuthenticationRepository } from "../interfaces/repositoryInterfaces";
import { IAuthenticationManager } from "../interfaces/serviceInterfaces";
import { IResponse } from "../interfaces/utilitiesInterfaces";
import { ErrorResponse } from "../utils/Response";
import { Response } from "../utils/Response";

export class AuthenticationManager implements IAuthenticationManager {

    authenticationRepository: IAuthenticationRepository;

    constructor(authenticationRepository: IAuthenticationRepository) {
        this.authenticationRepository = authenticationRepository;
    }

    async login(email: string, password: string): Promise<IResponse<boolean>> {
        try {
            await this.authenticationRepository.login(email,password)
            return new Response<boolean>(true);
        } catch (error) {
            throw error instanceof ErrorResponse ?  error : new ErrorResponse('0101');
        }
    }

    async logout(): Promise<IResponse<boolean>> {
        try {
            await this.authenticationRepository.logout()
            return new Response<boolean>(true);
        } catch (error) {
            throw error instanceof ErrorResponse ?  error : new ErrorResponse('0111');
        }
    }

    async tokenLogin(token: string): Promise<IResponse<boolean>> {
        try {
            await this.authenticationRepository.tokenLogin(token)
            return new Response<boolean>(true);
        } catch (error) {
            throw error instanceof ErrorResponse ?  error : new ErrorResponse('0101');
        }
    }

    isAuthenticated(): IResponse<boolean> {
        return new Response<boolean>(localStorage.getItem('token') ? true : false);
    }

    isEnterpriseSelected(): IResponse<boolean> {
        return new Response<boolean>(localStorage.getItem('enterprise') ? true : false);
    }

    setEnterprise(enterprise: IEnterprise): IResponse<boolean> {
        if(enterprise){
            localStorage.setItem('enterprise', enterprise.Document);
            localStorage.setItem('domainId', enterprise.DomainId);
            localStorage.setItem('domainName', enterprise.DomainName);
            this.authenticationRepository.userInfo();
            this.authenticationRepository.setRegistry();
        }
        else throw new ErrorResponse('0113');
        return new Response<boolean>(true);
    }

    getEnterpriseSelected(): IResponse<string> {
        return new Response<string>(localStorage.getItem('enterprise'));
    }
}