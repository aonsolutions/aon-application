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

    /**
     * Realiza una operación de inicio de sesión utilizando el correo electrónico y la contraseña proporcionados.
     *
     * @param {string} email - El correo electrónico del usuario.
     * @param {string} password - La contraseña del usuario.
     * @return {Promise<IResponse<boolean>>} - Una promesa que se resuelve en un objeto de respuesta que indica
     * si el inicio de sesión fue exitoso o no.
     */
    async login(email: string, password: string): Promise<IResponse<boolean>> {
        try {
            await this.authenticationRepository.login(email,password)
            return new Response<boolean>(true);
        } catch (error) {
            throw error instanceof ErrorResponse ?  error : new ErrorResponse('0101');
        }
    }

    /**
     * Cierra la sesión del usuario.
     * @return {Promise<IResponse<boolean>>} Una promesa que se resuelve a un objeto de respuesta de tipo booleano.
     */
    async logout(): Promise<IResponse<boolean>> {
        try {
            await this.authenticationRepository.logout()
            return new Response<boolean>(true);
        } catch (error) {
            throw error instanceof ErrorResponse ?  error : new ErrorResponse('0111');
        }
    }

    /**
     * Inicia sesión utilizando un token.
     *
     * @param {string} token - El token utilizado para iniciar sesión.
     * @return {Promise<IResponse<boolean>>} Una promesa que se resuelve en una respuesta que indica si el inicio de sesión fue exitoso.
     */
    async tokenLogin(token: string): Promise<IResponse<boolean>> {
        try {
            await this.authenticationRepository.tokenLogin(token)
            return new Response<boolean>(true);
        } catch (error) {
            throw error instanceof ErrorResponse ?  error : new ErrorResponse('114');
        }
    }

    /**
     * Verifica si el usuario está autenticado.
     *
     * @return {IResponse<boolean>} Una respuesta que indica si el usuario está autenticado.
     */
    isAuthenticated(): IResponse<boolean> {
        return new Response<boolean>(localStorage.getItem('token') ? true : false);
    }

    /**
     * Determina si se ha seleccionado una empresa.
     *
     * @return {IResponse<boolean>} La respuesta que indica si se ha seleccionado una empresa.
     */
    isEnterpriseSelected(): IResponse<boolean> {
        return new Response<boolean>(localStorage.getItem('enterprise') ? true : false);
    }

      /**
       * Establece los datos de la empresa en el almacenamiento local y realiza acciones adicionales si la empresa existe.
       *
       * @param {IEnterprise} enterprise - El objeto de la empresa que contiene los datos a establecer en el almacenamiento local.
       * @return {IResponse<boolean>} - Devuelve un objeto de respuesta que indica si la operación fue exitosa.
       */
    setEnterprise(enterprise: IEnterprise): IResponse<boolean> {
        if(enterprise){
            localStorage.setItem('enterprise', enterprise.Document);
            localStorage.setItem('domainId', enterprise.DomainId);
            localStorage.setItem('domainName', enterprise.DomainName);
            localStorage.setItem('idEnterprise', enterprise.Id);
            this.authenticationRepository.userInfo();
            this.authenticationRepository.setRegistry();
        }
        else throw new ErrorResponse('0113');
        return new Response<boolean>(true);
    }

    /**
     * Recupera la empresa seleccionada del almacenamiento local.
     *
     * @return {IResponse<string>} La empresa seleccionada.
     */
    getEnterpriseSelected(): IResponse<string> {
      try {
        return new Response<string>(localStorage.getItem('enterprise'));
      } catch (error) {
        throw error instanceof ErrorResponse ?  error : new ErrorResponse('0112');
      }
    }
}
