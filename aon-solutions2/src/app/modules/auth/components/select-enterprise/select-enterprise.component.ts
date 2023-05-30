import { AuthService } from 'src/app/core/services/auth.service';
import { Component, Input, OnInit, Output } from '@angular/core';
import { Enterprise } from 'src/app/shared/layouts/top-bar/top-bar.component';
import { environment } from 'src/environments/environment';
import { ApiService } from 'src/app/core/services/api.service';
import { HttpHeaders } from '@angular/common/http';

@Component({
  selector: 'app-select-enterprise',
  templateUrl: './select-enterprise.component.html',
  styleUrls: ['./select-enterprise.component.scss']
})
export class SelectEnterpriseComponent implements OnInit {

 empresas: any = '';

  enterprises: Enterprise [] = [
    {name: 'Nombre de empresa 1'},
    {name: 'Nombre de empresa 2'},
    {name: 'Nombre de empresa 3'},
    {name: 'Nombre de empresa 4'},
  ];

  constructor(private auth: AuthService, private apiService: ApiService) {
    // this.empresas = this.auth.auxEmpresas();

  }

  ngOnInit() {
    this.getEnterprises();
  }

  getEnterprises() {
    let domainName = environment.headerApi.domainName;
    let headers = new HttpHeaders();
    let token = sessionStorage.getItem(environment.localStorageJwt.accessToken);

    if (token) {
      headers
      .set('session_id', token)
      .set('domain_name', domainName);
    }


    return this.apiService.getWithHeaders('company', headers).subscribe(
      (response) => {
        console.log(response);
      },
      (error) => {
        console.error(`Se ha producido un error al recuperar empresas ${error}`);
      }
    );
  }

}
