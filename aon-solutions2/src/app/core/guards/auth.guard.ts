import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot, UrlTree } from '@angular/router';
import { Observable } from 'rxjs';
import { JwtAuthService } from '../services/jwt-auth.service';

@Injectable({
  providedIn: 'root'
})
export class AuthGuard implements CanActivate {

  constructor(private router: Router, private jwtAuth: JwtAuthService) { }

  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot): Observable<boolean | UrlTree> | Promise<boolean | UrlTree> | boolean | UrlTree {
        const isLoggedIn = this.jwtAuth.isLoggedIn();

        if(!isLoggedIn) {
            this.router.navigate(['auth']);
            return false;
        } else {
            return true;
        }
    }

}
