import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef, MatDialog } from '@angular/material';
import { SharedService } from '../../../services/shared.service';
import { RootLoader, GwtLoader} from '../../../utils/loader';
import { Router } from '@angular/router';
import { Location } from '@angular/common';

@Component({
  selector: 'app-extension-dialog',
  templateUrl: './extension-dialog.component.html',
  styleUrls: ['./extension-dialog.component.css']
})
export class ExtensionDialogComponent {

  apps = [{
    app:'invoice',
    title: 'Facturas',
    logo: 'assets/apps/invoice.png'
  },{
    app: 'documental',
    title: 'Documental',
    logo: 'assets/apps/documental.png'
  },{
    app:'helpdesk',
    title: 'Help Desk',
    logo: 'assets/apps/helpdesk.png'
  },{
    app:'accounting',
    title: 'Contable',
    logo: 'assets/apps/conta.png'
  },{
    app:'fiscal',
    title: 'Fiscal',
    logo: 'assets/apps/fiscal.png'
  },{
    app:'payroll',
    title: 'Laboral',
    logo: 'assets/apps/laboral.png'
  },{
    app:'ocr',
    title: 'OCR',
    logo: 'assets/apps/ocr.png'
  },{
    app:'selfconta',
    title: 'Selfconta',
    logo: 'assets/apps/selfconta.png'
  },{
    app:'saltra',
    title: 'Saltra',
    logo: 'assets/apps/saltra.png'
  },{
    app:'bidoq',
    title: 'Bidoq',
    logo: 'assets/apps/bidoq.png'
  },{
    app:'alma',
    title: 'ALMA',
    logo: 'assets/apps/alma.png'
  },{
    app:'serviconvenios',
    title: 'Convenios',
    logo: 'assets/apps/serviconvenios.png'
  },{
    app: 'aon',
    title: 'Conect@ Aio',
    logo: 'assets/apps/aon.png'
  },{
    app: 'aon',
    title: 'Conect@ SMB',
    logo: 'assets/apps/aon.png'
  },{
    app: 'learning',
    title: 'Learning',
    logo: 'assets/apps/learning.png'
  }];

  constructor(public dialogRef: MatDialogRef<ExtensionDialogComponent>,
      @Inject(MAT_DIALOG_DATA) public data: any, public dialog: MatDialog,
      public service: SharedService, private router: Router, private location: Location) {}

  appSelection(app: string) {
    this.dialogRef.close();
    if('invoice' === app) {
      RootLoader.angularPanel(this.router, this.location, 'invoice');
    } else if('documental' === app) {
      GwtLoader.startModule('aon_gwt_aio', 'documents');
    } else if('helpdesk' === app) {
      GwtLoader.startModule('aon_gwt_aio', 'issues');
    } else if('accounting' === app) {

    } else if('fiscal' === app) {

    } else if('payroll' === app) {
		GwtLoader.startModule('aon_gwt_aio', 'employees');
    } else if('ocr' === app) {

    } else if('selfconta' === app) {

    } else if('saltra' === app) {

    } else if('bidoq' === app) {

    } else if('alma' === app) {

    } else if('serviconvenios' === app) {
      open('https://www.serviconvenios.com/');
    } else if('aon' === app) {
      open('https://' + localStorage.getItem('aon_domain_name'));
    } else if('learning' === app) {

    }
  }
}
