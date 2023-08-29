import {
  Component,
  Input,
  OnInit,
  SimpleChanges,
  ViewChild,
} from '@angular/core';

import { TranslateService } from '@ngx-translate/core';
import { CertificateService } from '../../../../core/services/certificate.service';

@Component({
  selector: 'app-table-profile-certificate',
  templateUrl: './table-profile-certificate.component.html',
  styleUrls: ['./table-profile-certificate.component.scss'],
})
export class TableProfileCertificateComponent implements OnInit {
  headerTable: any = {};
  bodyTable: any = [];
  displayedColumns: string[] = [
    'Holder',
    'Representation',
    'Expiration Date',
    'Alias',
    'Type',
    'TGSS',
    'SEPE',
    'AEAT',
    'Actions',
  ];

//type: class Certificate { constructor(name, representationType, expirationDate, alias, type, tgss, sepe, aeat) }

  constructor(public certificateService: CertificateService,
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
        'PROFILE.ACTIONS'
      ])
      .subscribe((result) => {
        this.headerTable = {
          holder: result['PROFILE.HOLDER'] ,
          representation: result['PROFILE.REPRESENTATION'] ,
          expirationDate: result['PROFILE.EXPIRATION_DATE'] ,
          alias: result['PROFILE.ALIAS'],
          type: result['PROFILE.TYPE'],
          tgss: result['PROFILE.TGSS'],
          sepe: result['PROFILE.SEPE'],
          aeat: result['PROFILE.AEAT'],
          actions: result['PROFILE.TYPE']
        };
      });

    let tableRow: any = [];
    let column: any = {};

    certificateService.getCertificateList().then((response) => {

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
        console.log(certificateService)
      });

      this.bodyTable = tableRow;
    });
  }

  ngOnInit(): void {}

  modalClick(object: any) {

    this.certificateService.getCertificate(object.key).then((response) => {
      console.log(response);
    });
  }
}
