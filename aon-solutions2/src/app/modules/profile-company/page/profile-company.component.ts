import { enterprises } from './../../../../../libraries/AonSDK/src/models/Enterprise';
import { Component, OnInit, ViewChild } from '@angular/core';
import { ModalDeleteCertificateComponent } from '../components/modal-delete-certificate/modal-delete-certificate.component';
import { EnterpriseService } from 'src/app/core/services/enterprise.service';
import { IEnterprise } from 'libraries/AonSDK/src/aon';

@Component({
  selector: 'app-profile-company',
  templateUrl: './profile-company.component.html',
  styleUrls: ['./profile-company.component.scss'],
})
export class ProfileCompanyComponent implements OnInit {
  modalComponentDelete: any;
  enterprise: IEnterprise | null = null;

  constructor(private enterpriseService: EnterpriseService) {}

  @ViewChild('modalDelete') modalComponentEdit: any = '';

  functionHome: any = (result: any) => this.afterModalClosed(result);

  afterModalClosed(result?: any) {
    console.log(result);
  }

  showModalDelete() {
    this.modalComponentDelete.openDialog(
      ModalDeleteCertificateComponent,
      this.functionHome,
      'Data from home'
    );
  }

  async ngOnInit() {
    this.enterprise = await this.enterpriseService.getCurrentEntepriseData();
  }
}
