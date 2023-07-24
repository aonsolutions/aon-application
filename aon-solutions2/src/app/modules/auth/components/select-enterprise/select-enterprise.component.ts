import { AuthService } from 'src/app/core/services/auth.service';
import { Component, OnInit} from '@angular/core';
import { Router } from '@angular/router';
import { EnterpriseService } from 'src/app/core/services/enterprise.service';
import { CollectionFactory, ICollection, IEnterprise } from 'libraries/AonSDK/aon';

@Component({
  selector: 'app-select-enterprise',
  templateUrl: './select-enterprise.component.html',
  styleUrls: ['./select-enterprise.component.scss']
})
export class SelectEnterpriseComponent implements OnInit {

  enterprises: ICollection<IEnterprise> = new CollectionFactory().createEnterpriseCollection();

  constructor(private authService: AuthService, private enterpriseService: EnterpriseService, private router: Router) {
    // this.empresas = this.auth.auxEmpresas();

  }

  ngOnInit() {
    this.enterpriseService.getEnterpriseList().then((enterprises) => {
      this.enterprises = enterprises;
    });
  }

  selectEnterprise(enterprise: IEnterprise) {
    // TODO: Llamar servicio JWT(generico) para que establezca empresa seleccionada en session
    // TODO: Establecer si guardamos toda la empresa, o por el contrario usar id/document
    this.authService.setEnterprise(enterprise.Document)
    this.router.navigate(['']);
  }

}
