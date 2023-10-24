import { Component, OnInit, ViewChild } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';

import { CertificateService } from '../../../../core/services/certificate.service';
import { ModalDeleteCertificateComponent } from '../modal-delete-certificate/modal-delete-certificate.component';
import { ModalInfoCertificateComponent } from '../modal-info-certificate/modal-info-certificate.component';
import { ModalValidatedCertificateComponent } from '../modal-validated-certificate/modal-validated-certificate.component';

@Component({
  selector: 'app-table-profile-certificate',
  templateUrl: './table-profile-certificate.component.html',
  styleUrls: ['./table-profile-certificate.component.scss'],
})
export class TableProfileCertificateComponent implements OnInit {
  displayedColumns: string[] = [
    'name',
    'representationType',
    'expirationDate',
    'alias',
    'type',
    'tgss',
    'sepe',
    'aeat',
    'actions',
  ];
  headerTable: any = {};
  bodyTable: any = [];

  constructor(
    public certificateService: CertificateService,
    private translateService: TranslateService
  ) {
    this.translateService
      .get([
        'PROFILE.HOLDER',
        'PROFILE.REPRESENTATION',
        'PROFILE.EXPIRATION_DATE',
        'PROFILE.ALIAS',
        'PROFILE.TYPE',
        'PROFILE.TGSS',
        'PROFILE.SEPE',
        'PROFILE.AEAT',
        'PROFILE.ACTIONS',
      ])
      .subscribe((result) => {
        this.headerTable = {
          name: result['PROFILE.HOLDER'],
          representationType: result['PROFILE.REPRESENTATION'],
          expirationDate: result['PROFILE.EXPIRATION_DATE'],
          alias: result['PROFILE.ALIAS'],
          type: result['PROFILE.TYPE'],
          tgss: result['PROFILE.TGSS'],
          sepe: result['PROFILE.SEPE'],
          aeat: result['PROFILE.AEAT'],
          actions: result['PROFILE.ACTIONS'],
        };
        this.getCertificates();
      });
  }

  ngOnInit(): void {}

  @ViewChild('modalDelete') ModalDeleteCertificateComponent: any = '';
  @ViewChild('modalValidated') ModalValidatedCertificateComponent: any = '';
  @ViewChild('modalInfoCertificate') ModalInfoCertificateComponent: any = '';

  functionHome: any = (result: any) => this.afterModalClosed(result);
  afterModalClosed(result?: any) {}

  modalClick(object: any) {
    switch (object.keyButton) {
      case 'delete':
        this.ModalDeleteCertificateComponent.openDialog(
          ModalDeleteCertificateComponent,
          this.functionHome
        );
        break;
      case 'verified_user':
        this.ModalValidatedCertificateComponent.openDialog(
          ModalValidatedCertificateComponent,
          this.functionHome
        );
        break;
    }
  }
  rowClick(object: any) {
    // if (object.keyName === 'name') {
    this.ModalInfoCertificateComponent.openDialog(
      ModalInfoCertificateComponent,
      this.functionHome
    );
    console.log(object);
    // }
  }

  private getCertificates() {
    let tableRow: any = [];
    let column: any = {};

    this.certificateService.getCertificateList().then((response) => {
      response.forEach(function (certificate, certificateKey) {
        column = Object.assign({}, certificate);
        column.key = certificateKey;
        column.name = certificate.Name;
        column.representationType = certificate.RepresentationType;
        column.expirationDate = certificate.ExpirationDate;
        column.alias = certificate.Alias;
        column.type = certificate.Type;
        column.tgss = certificate.Tgss;
        column.sepe = certificate.Sepe;
        column.aeat = certificate.Aeat;
        column.actions = ['verified_user', 'delete'];

        tableRow.push(column);
      });

      this.bodyTable = tableRow;
    });
  }

}
