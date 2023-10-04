import { Component, OnInit} from '@angular/core';
import { Router } from '@angular/router';

import { AuthService } from 'src/app/core/services/auth.service';
import { CollectionFactory, ErrorResponse, ICollection, IEnterprise } from 'libraries/AonSDK/src/aon';
import { EnterpriseService } from 'src/app/core/services/enterprise.service';

@Component({
  selector: 'app-select-enterprise',
  templateUrl: './select-enterprise.component.html',
  styleUrls: ['./select-enterprise.component.scss']
})
export class SelectEnterpriseComponent implements OnInit {
  enterprises: ICollection<IEnterprise> = new CollectionFactory().createEnterpriseCollection();
  spinner: boolean = true;

/**
 * Inicializar el componente y obtener la lista de empresas del servicio de empresas.
 *
 * @param {AuthService} authService - El servicio de autenticación utilizado para la autenticación de usuarios.
 * @param {EnterpriseService} enterpriseService - El servicio utilizado para interactuar con los datos de las empresas.
 * @param {Router} router - El enrutador utilizado para la navegación.
 * @throws {ErrorResponse} Si ocurre un error durante la construcción de la instancia.
 * @finally Establece el valor de la propiedad spinner en false.
 */
  constructor(private authService: AuthService, private enterpriseService: EnterpriseService, private router: Router) {
    this.enterpriseService.getEnterpriseList().then((enterprises) => {
      this.spinner = false;
      this.enterprises = enterprises;
    })
  }

  ngOnInit() {
  }

  /**
   * Selecciona una empresa y la establece en el servicio de autenticación.
   * Navega a la página de inicio.
   *
   * @param {IEnterprise} enterprise - La empresa que se va a seleccionar.
   * @return {void} No se devuelve nada.
   */
  selectEnterprise(enterprise: IEnterprise) {
    try {
      this.authService.setEnterprise(enterprise)
      this.router.navigate(['']);
    } catch (error) {
      throw error instanceof ErrorResponse ? error : new ErrorResponse(error);
    }
  }

}
