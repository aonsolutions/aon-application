import { AuthenticationFactory } from "../factorys/AuthenticationFactory";
import { IEnterprise } from "../interfaces/modelsInterfaces";
import { IResponse } from "../interfaces/utilitiesInterfaces";

export class AuthFunctions {
    authManager = new AuthenticationFactory().createAuthenticationManager()

    async login(email: string, password: string): Promise<IResponse<boolean>> {
      return await this.authManager.login(email, password);
    }
  
    async logout(): Promise<void> {
      await this.authManager.logout();
    }
  
    isLoggedIn(): boolean {
      return this.authManager.isAuthenticated().result;
    }
  
    async magicLogin(token: string): Promise<boolean> {
      return (await this.authManager.tokenLogin(token)).result;
    }
  
    setEnterprise(enterprise: IEnterprise): boolean {
      return this.authManager.setEnterprise(enterprise).result;
    }
  
    isEnterpriseSelected(): boolean {
      return this.authManager.isEnterpriseSelected().result;
    }
  
    getEnterpriseSelected(): string {
      return this.authManager.getEnterpriseSelected().result;
    }    
}

