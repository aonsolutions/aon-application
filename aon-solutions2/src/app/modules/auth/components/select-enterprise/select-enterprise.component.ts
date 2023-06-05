import { AuthService } from 'src/app/core/services/auth.service';
import { Component, Input, OnInit, Output } from '@angular/core';
import { ApiService } from 'src/app/core/services/api.service';
import { IEnterprise } from 'src/app/core/models/interface/enterprise';
import { Router } from '@angular/router';

@Component({
  selector: 'app-select-enterprise',
  templateUrl: './select-enterprise.component.html',
  styleUrls: ['./select-enterprise.component.scss']
})
export class SelectEnterpriseComponent implements OnInit {

 empresas: IEnterprise[] = [];


  constructor(private authService: AuthService, private apiService: ApiService, private router: Router) {
    // this.empresas = this.auth.auxEmpresas();

  }

  ngOnInit() {
    this.authService.getListEnterprises().subscribe((enterprises) => {
      this.empresas = enterprises;
    });
  }

  selectEnterprise(enterprise: IEnterprise) {
    // TODO: Llamar servicio JWT(generico) para que establezca empresa seleccionada en session
    // TODO: Establecer si guardamos toda la empresa, o por el contrario usar id/document
    this.router.navigate(['']);
  }


}
