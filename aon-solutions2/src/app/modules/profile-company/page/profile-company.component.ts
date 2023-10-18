import { Component, OnInit, ViewChild } from '@angular/core';
import { ModalDeleteCertificateComponent } from '../components/modal-delete-certificate/modal-delete-certificate.component';

@Component({
  selector: 'app-profile-company',
  templateUrl: './profile-company.component.html',
  styleUrls: ['./profile-company.component.scss'],
})
export class ProfileCompanyComponent implements OnInit {
  modalComponentDelete: any;

  constructor() {
  }

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

  ngOnInit() {
  }

}
