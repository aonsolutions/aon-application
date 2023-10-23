import { Component, OnInit, ViewChild } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';

import { CertificateService } from '../../../../core/services/certificate.service';
import { ModalDeleteCertificateComponent } from '../modal-delete-certificate/modal-delete-certificate.component';
import { ModalInfoCertificateComponent } from '../modal-info-certificate/modal-info-certificate.component';
import { ModalValidatedCertificateComponent } from '../modal-validated-certificate/modal-validated-certificate.component';
import { DatePipe } from '@angular/common';

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
          this.functionHome,
          'Data from home'
        );
        break;
      case 'verified_user':
        this.ModalValidatedCertificateComponent.openDialog(
          ModalValidatedCertificateComponent,
          this.functionHome,
          'Data from home'
        );
        break;
    }
  }
  rowClick(object: any) {
    // if (object.keyName === 'name') {
    this.ModalInfoCertificateComponent.openDialog(
      ModalInfoCertificateComponent,
      this.functionHome,
      'Data from home'
    );
    console.log(object);
    // }
  }

  private getCertificates() {
    let tableRow: any = [];
    let column: any = {};
    const resYes = this.translateService.instant('COMMON.YES');
    const resNo = this.translateService.instant('COMMON.NO');

    this.certificateService.getCertificateList().then((response) => {
      response.forEach(function (certificate, certificateKey) {
        column = Object.assign({}, certificate);
        column.key = certificateKey;
        column.name = certificate.Name;
        column.representationType = certificate.RepresentationType;
        column.expirationDate = new DatePipe('es-ES').transform(
          certificate.ExpirationDate,
          'dd/MM/yyyy'
        );
        column.alias = certificate.Alias;
        column.type = certificate.Type;
        column.tgss = (certificate.Tgss) ? resYes : resNo;
        column.sepe = (certificate.Sepe) ? resYes : resNo;
        column.aeat = (certificate.Aeat) ? resYes : resNo;
        column.actions = ['verified_user', 'delete'];

        tableRow.push(column);
      });

      this.bodyTable = tableRow;
    });
  }

}
