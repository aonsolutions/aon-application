import { Location } from '@angular/common';
import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot, UrlTree } from '@angular/router';
import { Observable } from 'rxjs';
import { EnterpriseService } from '../services/enterprise.service';
import { AuthService } from '../services/auth.service';

@Injectable({
  providedIn: 'root'
})
export class EnterpriseGuard implements CanActivate {

  constructor(private location: Location, 
    private enterpriseService: EnterpriseService, 
    private authService: AuthService, 
    private router: Router){}

  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot): Observable<boolean | UrlTree> | Promise<boolean | UrlTree> | boolean | UrlTree {
      const isLoggedIn = this.authService.isLoggedIn();
      const isEnterpriseSelected = this.authService.isEnterpriseSelected();
      if(isEnterpriseSelected){
        this.router.navigate(['home'])
        return false;
      }else if(!isLoggedIn && !isEnterpriseSelected){
        this.router.navigate([''])
        return false;
      }else{
        return true;
      }
  }
  
}
