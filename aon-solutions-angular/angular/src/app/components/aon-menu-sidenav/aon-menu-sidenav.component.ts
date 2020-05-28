import { Component, OnInit, OnDestroy } from '@angular/core';
import { SharedService, AonService } from '../../services/services';
import { RootLoader, GwtLoader} from '../../utils/loader';
import { Router } from '@angular/router';
import { Location } from '@angular/common';

@Component({
  selector: 'aon-menu-sidenav',
  templateUrl: './aon-menu-sidenav.component.html',
  styleUrls: ['./aon-menu-sidenav.component.css'],
})
export class AonMenuSidenavComponent implements OnInit, OnDestroy {
  app: any;
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
  }];

  constructor(public aonService: AonService, public service : SharedService,
    private router: Router, private location: Location) {

  }

  ngOnInit() {
    RootLoader.rootPanel("");
  }

  ngOnDestroy() {

  }

  appSelection(app: string) {
    if('invoice' === app) {
      RootLoader.angularPanel(this.router, this.location, 'invoice');
    } else if('documental' === app) {
      GwtLoader.startModule('aon_gwt_aio', 'documents');
    } else if('helpdesk' === app) {
      GwtLoader.startModule('aon_gwt_aio', 'issues');
    } else if('accounting' === app) {

    } else if('fiscal' === app) {
      this.app = {
        app:'fiscal',
        title: 'Fiscal',
        logo: 'assets/apps/fiscal.png',
        options: [{
          title:'IVA. Autoliquidación.',
          code: '303'
        },{
          title: 'Declaración anual operaciones con terceras personas.',
          code: '347'
        }]
      }
    } else if('payroll' === app) {
      GwtLoader.startModule('aon_gwt_aio', 'employees');
    }
  }

  optionSelection(option: string) {
    if('303' === option) {
      GwtLoader.startModule('aon_gwt_fiscal', 'Model303');
    } else if('347' === option) {
      GwtLoader.startModule('aon_gwt_fiscal', 'Model347');
    }

  }

  isPrincipalMenu(): boolean {
    return this.app === undefined;
  }

  backMenu(): void {
    this.app = undefined;
  }
}
