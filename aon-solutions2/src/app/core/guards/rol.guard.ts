import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot, UrlTree } from '@angular/router';
import { Observable } from 'rxjs';
import { AuthService } from '../services/auth.service';

@Injectable({
  providedIn: 'root'
})
export class RolGuard implements CanActivate {

  constructor(private authService: AuthService, private router: Router) {}
  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot): Observable<boolean | UrlTree> | Promise<boolean | UrlTree> | boolean | UrlTree {
    const userRoles = this.authService.getUserRoles();
    const requiredRoles = route.data.requiredRoles;

    // Verifica si el usuario tiene los permisos para acceder a la ruta
    const hasRoles = userRoles.some(role => requiredRoles.includes(role));

    if (hasRoles) {
      return true;
    } else {
      this.router.navigate(['']);
      return false;
    }

  }

}
