import { AuthenticationFactory } from "../factorys/AuthenticationFactory";
import { IEnterprise } from "../interfaces/modelsInterfaces";
import { IResponse } from "../interfaces/utilitiesInterfaces";

export class AuthFunctions {
    private static authManager = new AuthenticationFactory().createAuthenticationManager()

    static async login(email: string, password: string): Promise<IResponse<boolean>> {
      return await this.authManager.login(email, password);
    }
  
    static async logout(): Promise<void> {
      await this.authManager.logout();
    }
  
    static isLoggedIn(): boolean {
      return this.authManager.isAuthenticated().result;
    }
  
    static async magicLogin(token: string): Promise<boolean> {
      return (await this.authManager.tokenLogin(token)).result;
    }
  
    static setEnterprise(enterprise: IEnterprise): boolean {
      return this.authManager.setEnterprise(enterprise).result;
    }
  
    static isEnterpriseSelected(): boolean {
      return this.authManager.isEnterpriseSelected().result;
    }
  
    static getEnterpriseSelected(): string {
      return this.authManager.getEnterpriseSelected().result;
    }    
}

