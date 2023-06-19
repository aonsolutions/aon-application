import { AuthService } from 'src/app/core/services/auth.service';
import { Component, Input, OnInit, Output } from '@angular/core';
import { ApiService } from 'src/app/core/services/api.service';
import { Router } from '@angular/router';
import { Enterprise } from 'src/app/core/models/class/enterprise';
import { EnterpriseService } from 'src/app/core/services/enterprise.service';

@Component({
  selector: 'app-select-enterprise',
  templateUrl: './select-enterprise.component.html',
  styleUrls: ['./select-enterprise.component.scss']
})
export class SelectEnterpriseComponent implements OnInit {

  empresas: Enterprise[] = [];

  constructor(private authService: AuthService, private enterpriseService: EnterpriseService, private router: Router) {
    // this.empresas = this.auth.auxEmpresas();

  }

  ngOnInit() {
    this.enterpriseService.getEnterprises().then((enterprises) => {
      this.empresas = enterprises;
    });
  }

  selectEnterprise(enterprise: Enterprise) {
    // TODO: Llamar servicio JWT(generico) para que establezca empresa seleccionada en session
    // TODO: Establecer si guardamos toda la empresa, o por el contrario usar id/document
    this.router.navigate(['']);
  }

}
