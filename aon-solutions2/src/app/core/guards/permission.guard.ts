import { AuthService } from 'src/app/core/services/auth.service';
import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot, UrlTree } from '@angular/router';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class PermissionGuard implements CanActivate {

  constructor(private authService: AuthService, private router: Router) {}

  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot): Observable<boolean | UrlTree> | Promise<boolean | UrlTree> | boolean | UrlTree {
    const userPermissions = this.authService.getUserPermissions();
    const requiredPermissions = route.data.requiredPermissions;

    // Verifica si el usuario tiene los permisos para acceder a la ruta
    const hasPermissions = userPermissions.some(permission => requiredPermissions.includes(permission));

    if (hasPermissions) {
      return true;
    } else {
      this.router.navigate(['']);
      return false;
    }

  }

}
