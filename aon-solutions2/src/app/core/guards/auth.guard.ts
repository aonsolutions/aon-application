import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot, UrlTree } from '@angular/router';
import { Observable } from 'rxjs';
import { AuthService } from '../services/auth.service';
import { EnterpriseService } from '../services/enterprise.service';

@Injectable({
  providedIn: 'root'
})
export class AuthGuard implements CanActivate {

  constructor(private router: Router, private authService: AuthService, private enterpriseService: EnterpriseService) { }

  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot): Observable<boolean | UrlTree> | Promise<boolean | UrlTree> | boolean | UrlTree {
        const isLoggedIn = this.authService.isLoggedIn();
        const isEnterpriseSelected = this.authService.isEnterpriseSelected();

        if(!isLoggedIn) {
            this.router.navigate(['']);
            return false;
        } else if(!isEnterpriseSelected){
            this.router.navigate(['/selectEnterprise']);
            return false;
        }else {
          return true;
        }
    }

}
