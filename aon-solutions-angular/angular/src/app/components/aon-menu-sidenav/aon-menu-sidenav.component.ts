import { Component, OnInit, OnDestroy } from '@angular/core';
import { SharedService, AonService } from '../../services/services';
import { RootLoader, GwtLoader} from '../../utils/loader';
import { Router } from '@angular/router';
import { Location } from '@angular/common';
import { Administration } from '../../models/models';

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
  },{
    app:'aon-classic',
    title: 'AiO',
    logo: 'assets/apps/aon-classic.png'
  }];

  constructor(public aonService: AonService, public service : SharedService,
    private router: Router, private location: Location) {

  }

  ngOnInit() {
    RootLoader.rootPanel("");
  }

  ngOnDestroy() {
    this.app = undefined;
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
        options: this.getFiscalOptions()
      }
    } else if('payroll' === app) {
      this.app = {
        app:'payroll',
        title: 'Laboral',
        options: this.payrollMenu
      }
    } else if('aon-classic' === app) {
      open('https://' + localStorage.getItem('aon_domain_name'));
    }
  }

  optionSelection(option: any) {
    GwtLoader.startModule(option.module, option.entryPoint);
  }

  isPrincipalMenu(): boolean {
    return this.app === undefined;
  }

  backMenu(): void {
    this.app = undefined;
  }

  getAppToolbarClass(): string {
    if('fiscal' === this.app.app) {
      let administration = this.service.company.administration;
      if(Administration.ALAVA === administration){
        return 'aon-fiscal-toolbar-araba';
      } else if(Administration.GIPUZKOA === administration){
        return 'aon-fiscal-toolbar-gipuzkoa';
      } else if(Administration.BIZKAIA === administration){
        return 'aon-fiscal-toolbar-bizkaia';
      } else if(Administration.NAVARRA === administration){
        return 'aon-fiscal-toolbar-navarra';
      } else return 'aon-fiscal-toolbar-aeat';
    } else if('payroll' === this.app.app) {
      return 'aon-payroll-toolbar';
    } else return '';
  }

  getFiscalOptions(): any[]{
    let administration = this.service.company.administration;
    if(Administration.ALAVA === administration){
      return this.arabaFiscalMenu;
    } else if(Administration.GIPUZKOA === administration){
      return this.gipuzkoaFiscalMenu;
    } else if(Administration.BIZKAIA === administration){
      return this.bizkaiaFiscalMenu;
    } else if(Administration.NAVARRA === administration){
      return this.navarraFiscalMenu;
    } else return this.aeatFiscalMenu;
  }

  payrollMenu: any[] =
    [{
      title:'Integral de Nóminas.',
      module: 'aon_gwt_aio',
      entryPoint: 'employees'
    },{
      title: 'Convenios.',
      module: 'aon_gwt_payroll',
      entryPoint:'MainAgreement'
    },{
      title: 'Modelo 111 - Retenciones e ingresos a cuenta sobre rendimientos del trabajo y de actividades económicas, premios y determinadas ganancias patrimoniales e imputaciones de renta.',
      module: 'aon_gwt_fiscal',
      entryPoint:'Model111'
    },{
      title: 'Modelo 190 - Resumen anual de retenciones e ingresos a cuenta. Rendimientos del trabajo de determinadas actividades económicas, premios y determinadas imputaciones de renta.',
      module: 'aon_gwt_fiscal',
      entryPoint:'Model190'
    },{
      title: 'Configuración. Globales.',
      module: 'aon_gwt_payroll',
      entryPoint:'MainSystem'
    },{
      title: 'Papelera.',
      module: 'aon_gwt_payroll',
      entryPoint:'MainTrash'
    }];

  arabaFiscalMenu: any[] =
    [{
      title:'Modelo 300 - IVA. Autoliquidación.',
      module: 'aon_gwt_fiscal',
      entryPoint: 'Model303'
    },{
      title: 'Modelo 347 - Declaración anual operaciones con terceras personas.',
      module: 'aon_gwt_fiscal',
      entryPoint:'Model347'
    },{
      title: 'Modelo 349 - Declaración recapitulativa de operaciones intracomunitarias.',
      module: 'aon_gwt_fiscal',
      entryPoint:'Model349'
    },{
      title: 'Modelo 390 - Haciendas Forales. Declaración resumen anual IVA.',
      module: 'aon_gwt_fiscal',
      entryPoint:'Model390HF'
    },{
      title: 'Modelo 110/111 - Retenciones e ingresos a cuenta sobre rendimientos del trabajo y de actividades económicas, premios y determinadas ganancias patrimoniales e imputaciones de renta.',
      module: 'aon_gwt_fiscal',
      entryPoint:'Model111'
    },{
      title: 'Modelo 115-A - Retenciones e ingresos a cuenta sobre determinadas rentas o rendimientos procedentes del arrendamiento de inmuebles urbanos.',
      module: 'aon_gwt_fiscal',
      entryPoint:'Model115'
    },{
      title: 'Modelo 123 - Retención e ingreso a cuenta sobre determinados rendimientos del capital mobiliario o determinadas rentas.',
      module: 'aon_gwt_fiscal',
      entryPoint:'Model123'
    },{
      title: 'Modelo 180 - Resumen anual de retenciones e ingresos a cuenta. Rendimientos procedentes del arrendamiento de inmuebles Urbanos.',
      module: 'aon_gwt_fiscal',
      entryPoint:'Model180'
    },{
      title: 'Modelo 184 - Declaración anual. Entidades en régimen de atribución de rentas.',
      module: 'aon_gwt_fiscal',
      entryPoint:'Model184'
    },{
      title: 'Modelo 190 - Resumen anual de retenciones e ingresos a cuenta. Rendimientos del trabajo de determinadas actividades económicas, premios y determinadas imputaciones de renta.',
      module: 'aon_gwt_fiscal',
      entryPoint:'Model190'
    },{
      title: 'Modelo 193 - Resumen anual de retenciones e ingresos a cuenta. Rendimientos del capital mobiliario, IS e IRNR sobre determinadas rentas.',
      module: 'aon_gwt_fiscal',
      entryPoint:'Model193'
    },{
      title: 'SII - Suministro Inmediato de Información.',
      module: 'aon_gwt_aio',
      entryPoint:'sii'
    }];

  gipuzkoaFiscalMenu: any[] =
    [{
      title:'Modelo 300/320 - IVA. Autoliquidación.',
      module: 'aon_gwt_fiscal',
      entryPoint: 'Model303'
    },{
      title: 'Modelo 347 - Declaración anual operaciones con terceras personas.',
      module: 'aon_gwt_fiscal',
      entryPoint:'Model347'
    },{
      title: 'Modelo 349 - Declaración recapitulativa de operaciones intracomunitarias.',
      module: 'aon_gwt_fiscal',
      entryPoint:'Model349'
    },{
      title: 'Modelo 390 - Haciendas Forales. Declaración resumen anual IVA.',
      module: 'aon_gwt_fiscal',
      entryPoint:'Model390HF'
    },{
      title: 'Modelo 110/111 - Retenciones e ingresos a cuenta sobre rendimientos del trabajo y de actividades económicas, premios y determinadas ganancias patrimoniales e imputaciones de renta.',
      module: 'aon_gwt_fiscal',
      entryPoint:'Model111'
    },{
      title: 'Modelo 115 - Retenciones e ingresos a cuenta sobre determinadas rentas o rendimientos procedentes del arrendamiento de inmuebles urbanos.',
      module: 'aon_gwt_fiscal',
      entryPoint:'Model115'
    },{
      title: 'Modelo 123 - Retención e ingreso a cuenta sobre determinados rendimientos del capital mobiliario o determinadas rentas.',
      module: 'aon_gwt_fiscal',
      entryPoint:'Model123'
    },{
      title: 'Modelo 180 - Resumen anual de retenciones e ingresos a cuenta. Rendimientos procedentes del arrendamiento de inmuebles Urbanos.',
      module: 'aon_gwt_fiscal',
      entryPoint:'Model180'
    },{
      title: 'Modelo 184 - Declaración anual. Entidades en régimen de atribución de rentas.',
      module: 'aon_gwt_fiscal',
      entryPoint:'Model184'
    },{
      title: 'Modelo 190 - Resumen anual de retenciones e ingresos a cuenta. Rendimientos del trabajo de determinadas actividades económicas, premios y determinadas imputaciones de renta.',
      module: 'aon_gwt_fiscal',
      entryPoint:'Model190'
    },{
      title: 'Modelo 193 - Resumen anual de retenciones e ingresos a cuenta. Rendimientos del capital mobiliario, IS e IRNR sobre determinadas rentas.',
      module: 'aon_gwt_fiscal',
      entryPoint:'Model193'
    },{
      title: 'SII - Suministro Inmediato de Información.',
      module: 'aon_gwt_aio',
      entryPoint:'sii'
    }];

  bizkaiaFiscalMenu: any[] =
    [{
      title:'Modelo 303 - IVA. Autoliquidación.',
      module: 'aon_gwt_fiscal',
      entryPoint: 'Model303'
    },{
      title: 'Modelo 140 - Libro-registro de operaciones económicas.',
      module: 'aon_gwt_fiscal',
      entryPoint:'Model140'
    },{
      title: 'Modelo 347 - Declaración anual operaciones con terceras personas.',
      module: 'aon_gwt_fiscal',
      entryPoint:'Model347'
    },{
      title: 'Modelo 349 - Declaración recapitulativa de operaciones intracomunitarias.',
      module: 'aon_gwt_fiscal',
      entryPoint:'Model349'
    },{
      title: 'Modelo 390 - Haciendas Forales. Declaración resumen anual IVA.',
      module: 'aon_gwt_fiscal',
      entryPoint:'Model390HF'
    },{
      title: 'Modelo 110/111 - Retenciones e ingresos a cuenta sobre rendimientos del trabajo y de actividades económicas, premios y determinadas ganancias patrimoniales e imputaciones de renta.',
      module: 'aon_gwt_fiscal',
      entryPoint:'Model111'
    },{
      title: 'Modelo 115 - Retenciones e ingresos a cuenta sobre determinadas rentas o rendimientos procedentes del arrendamiento de inmuebles urbanos.',
      module: 'aon_gwt_fiscal',
      entryPoint:'Model115'
    },{
      title: 'Modelo 123 - Retención e ingreso a cuenta sobre determinados rendimientos del capital mobiliario o determinadas rentas.',
      module: 'aon_gwt_fiscal',
      entryPoint:'Model123'
    },{
      title: 'Modelo 130 - I.R.P.F. Pago fraccionado. Empresarios y profesionales en estimación directa.',
      module: 'aon_gwt_fiscal',
      entryPoint:'Model130'
    },{
      title: 'Modelo 180 - Resumen anual de retenciones e ingresos a cuenta. Rendimientos procedentes del arrendamiento de inmuebles Urbanos.',
      module: 'aon_gwt_fiscal',
      entryPoint:'Model180'
    },{
      title: 'Modelo 184 - Declaración anual. Entidades en régimen de atribución de rentas.',
      module: 'aon_gwt_fiscal',
      entryPoint:'Model184'
    },{
      title: 'Modelo 190 - Resumen anual de retenciones e ingresos a cuenta. Rendimientos del trabajo de determinadas actividades económicas, premios y determinadas imputaciones de renta.',
      module: 'aon_gwt_fiscal',
      entryPoint:'Model190'
    },{
      title: 'Modelo 193 - Resumen anual de retenciones e ingresos a cuenta. Rendimientos del capital mobiliario, IS e IRNR sobre determinadas rentas.',
      module: 'aon_gwt_fiscal',
      entryPoint:'Model193'
    },{
      title: 'SII - Suministro Inmediato de Información.',
      module: 'aon_gwt_aio',
      entryPoint:'sii'
    }];

  navarraFiscalMenu: any[] =
    [{
      title:'Modelo F69 - IVA. Autoliquidación.',
      module: 'aon_gwt_fiscal',
      entryPoint: 'Model303'
    },{
      title: 'Modelo 347 - Declaración anual operaciones con terceras personas.',
      module: 'aon_gwt_fiscal',
      entryPoint:'Model347'
    },{
      title: 'Modelo 349 - Declaración recapitulativa de operaciones intracomunitarias.',
      module: 'aon_gwt_fiscal',
      entryPoint:'Model349'
    },{
      title: 'Modelo 745/715 - Retenciones e ingresos a cuenta sobre rendimientos del trabajo y de actividades económicas, premios y determinadas ganancias patrimoniales e imputaciones de renta.',
      module: 'aon_gwt_fiscal',
      entryPoint:'Model111'
    },{
      title: 'Modelo 759/760 - Retenciones e ingresos a cuenta sobre determinadas rentas o rendimientos procedentes del arrendamiento de inmuebles urbanos.',
      module: 'aon_gwt_fiscal',
      entryPoint:'Model115'
    },{
      title: 'Modelo 716 - Retención e ingreso a cuenta sobre determinados rendimientos del capital mobiliario o determinadas rentas.',
      module: 'aon_gwt_fiscal',
      entryPoint:'Model123'
    },{
      title: 'Modelo 180 - Resumen anual de retenciones e ingresos a cuenta. Rendimientos procedentes del arrendamiento de inmuebles Urbanos.',
      module: 'aon_gwt_fiscal',
      entryPoint:'Model180'
    },{
      title: 'Modelo 184 - Declaración anual. Entidades en régimen de atribución de rentas.',
      module: 'aon_gwt_fiscal',
      entryPoint:'Model184'
    },{
      title: 'Modelo 190 - Resumen anual de retenciones e ingresos a cuenta. Rendimientos del trabajo de determinadas actividades económicas, premios y determinadas imputaciones de renta.',
      module: 'aon_gwt_fiscal',
      entryPoint:'Model190'
    },{
      title: 'Modelo 193 - Resumen anual de retenciones e ingresos a cuenta. Rendimientos del capital mobiliario, IS e IRNR sobre determinadas rentas.',
      module: 'aon_gwt_fiscal',
      entryPoint:'Model193'
    },{
      title: 'SII - Suministro Inmediato de Información.',
      module: 'aon_gwt_aio',
      entryPoint:'sii'
    }];

  aeatFiscalMenu: any[] =
    [{
      title:'Modelo 303 - IVA. Autoliquidación.',
      module: 'aon_gwt_fiscal',
      entryPoint: 'Model303'
    },{
      title: 'Modelo 347 - Declaración anual operaciones con terceras personas.',
      module: 'aon_gwt_fiscal',
      entryPoint:'Model347'
    },{
      title: 'Modelo 349 - Declaración recapitulativa de operaciones intracomunitarias.',
      module: 'aon_gwt_fiscal',
      entryPoint:'Model349'
    },{
      title: 'Modelo 390 - Declaración resumen anual IVA.',
      module: 'aon_gwt_fiscal',
      entryPoint:'Model390'
    },{
      title: 'Modelo 111 - Retenciones e ingresos a cuenta sobre rendimientos del trabajo y de actividades económicas, premios y determinadas ganancias patrimoniales e imputaciones de renta.',
      module: 'aon_gwt_fiscal',
      entryPoint:'Model111'
    },{
      title: 'Modelo 115 - Retenciones e ingresos a cuenta sobre determinadas rentas o rendimientos procedentes del arrendamiento de inmuebles urbanos.',
      module: 'aon_gwt_fiscal',
      entryPoint:'Model115'
    },{
      title: 'Modelo 123 - Retención e ingreso a cuenta sobre determinados rendimientos del capital mobiliario o determinadas rentas.',
      module: 'aon_gwt_fiscal',
      entryPoint:'Model123'
    },{
      title: 'Modelo 130 - I.R.P.F. Pago fraccionado. Empresarios y profesionales en estimación directa.',
      module: 'aon_gwt_fiscal',
      entryPoint:'Model130'
    },{
      title: 'Modelo 131 - I.R.P.F. Pago fraccionado. Empresarios y profesionales en estimación objetiva.',
      module: 'aon_gwt_fiscal',
      entryPoint:'Model131'
    },{
      title: 'Modelo 180 - Resumen anual de retenciones e ingresos a cuenta. Rendimientos procedentes del arrendamiento de inmuebles Urbanos.',
      module: 'aon_gwt_fiscal',
      entryPoint:'Model180'
    },{
      title: 'Modelo 184 - Declaración anual. Entidades en régimen de atribución de rentas.',
      module: 'aon_gwt_fiscal',
      entryPoint:'Model184'
    },{
      title: 'Modelo 190 - Resumen anual de retenciones e ingresos a cuenta. Rendimientos del trabajo de determinadas actividades económicas, premios y determinadas imputaciones de renta.',
      module: 'aon_gwt_fiscal',
      entryPoint:'Model190'
    },{
      title: 'Modelo 193 - Resumen anual de retenciones e ingresos a cuenta. Rendimientos del capital mobiliario, IS e IRNR sobre determinadas rentas.',
      module: 'aon_gwt_fiscal',
      entryPoint:'Model193'
    },{
      title: 'Modelo 202 - Impuesto Sociedades. Pago fraccionado.',
      module: 'aon_gwt_fiscal',
      entryPoint:'Model202'
    },{
      title: 'Modelo 200 - Impuesto sobre Sociedades.',
      module: 'aon_gwt_fiscal',
      entryPoint:'Model200'
    },{
      title: 'SII - Suministro Inmediato de Información.',
      module: 'aon_gwt_aio',
      entryPoint:'sii'
    }];
}
