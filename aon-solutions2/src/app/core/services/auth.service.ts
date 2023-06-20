import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { AonSDK } from 'libraries/AonSDK/AonSDK';

@Injectable({
  providedIn: 'root' 
})
export class AuthService {

  aonSDK: AonSDK = new AonSDK();

  constructor(private router: Router) {
  }
  
  login(email: string, password: string): Promise<boolean> {
    return new Promise((resolve,reject) => {
      this.aonSDK.model('auth').login(email,password)
      .then(
        (response: any) => {
          if(response.result == true){
            this.router.navigate(['/auth/selectEnterprise']);
          }
          resolve(true);
        }
      ).catch(
        (error: any) => {
          reject(error.description + error.result)
        }
      )
    })
  }

  logout(): void {
    this.aonSDK.model('auth').logout()
    .then(
      (response: any) => {
        if(response.result == true)
          this.router.navigate(['/auth']);
      }
    ).catch(
      (error: any) => {
        throw new Error(error.description + ' - ' + error.result)
      }
    )
  }

  isLoggedIn(): boolean {
    if(this.aonSDK.model('auth').getSession().result.token){
      return true;
    }else {
      return false;
    }
  }

}

