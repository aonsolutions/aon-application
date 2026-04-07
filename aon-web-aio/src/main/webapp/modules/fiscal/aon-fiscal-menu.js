import { TAG } from '../../environments/environments.js';
import { InvoiceCommunicationConfig } from '../../models/InvoiceCommunicationConfig.js';
import { getCompany } from '../../services/companyService.js';
import { isPersonaFisica } from '../../services/documentUtils.js';
import { getInvoiceConfiguration } from '../../services/invoiceService.js';
import { AonSuiteMenu } from '../aon-suite-menu.js';

import * as GWT from '../../gwt/gwt.js';
import { Administration, ADMINISTRATIONS } from '../../models/Administration.js';

export class AonFiscalMenu extends AonSuiteMenu {

    AON_MENU;
    AON_HEADER;
    ROOT_PANEL;
    RIGHT_PANEL;
    APP_PARAMS;

    constructor () {
        super();
    }

    async connectedCallback() {
        await this.getCompany();
		this.buildDur().then(async () => {		
	        this.clear();
	        this.initialize();
       		await this.fiscalInitialize();
	        this.build();
		})
    }

    async getCompany() {
        getCompany().then(c => {
            this.company = c;
        }).catch(e => {
            console.log("error getCompany", e);
        });
    }
 
    async initInvoiceConfiguration() {
        let c = await getInvoiceConfiguration();
        this.icc = new InvoiceCommunicationConfig(c.communication);
        this.administration = c.administration
            ? new Administration(c.administration)
            : ADMINISTRATIONS.COMMON_TERRITORY;
    }

    async fiscalInitialize() {
        this.AON_HEADER = 'aonHeader';
        this.ROOT_PANEL = 'rootPanel';
        this.RIGHT_PANEL = 'rightPanel';
        this.last = "Matriz fiscal";
        this.new = "Nueva Acción";
        this.cardData={
            title: "Facturas",
            info:["con IRPF profesional", "con IRPF alquiler", "Intracomunitarias", "Extracomunitarias"]
        };
        this.selectOptions= [{
            title: "Modelo 303",
            action : () => GWT.iLoad(GWT.MODEL_303)
        },{
            title: "Modelo 111",
            action : () => GWT.iLoad(GWT.MODEL_111)
        },{
            title: "Modelo 115",
            action : () => GWT.iLoad(GWT.MODEL_115)
        }];
        await this.initOptions();
    }

    async initOptions() {
		await this.initInvoiceConfiguration();
        this.options = [{
            title: 'IVA AEAT',
            visible: this.administration.isCommonTerritory() || this.administration.isCanarias() || this.administration.isUnknown(),
            disabled: !(this.administration.isCommonTerritory() || this.administration.isCanarias() || this.administration.isUnknown()),
            options: [{
                description: "Modelo 303 ",
                title: "IVA Autoliquidación",
                action: () => GWT.iLoad(GWT.MODEL_303)
            }, {
                description: "Modelo 349 ",
                title: "Declaración recapitulativas de operaciones intracomunitarias",
                action: () => GWT.iLoad(GWT.MODEL_349)
            }, {
                description: "Modelo 390 ",
                title: "Declaración resumen anual IVA",
                action: () => GWT.iLoad(GWT.MODEL_390)
            }, {
                description: "Modelo 347 ",
                title: "Declaración anual operaciones con terceras personas",
                action: () => GWT.iLoad(GWT.MODEL_347)
            }, {
                description: "Modelo 369 ",
                title: "Declaraciones de IVA del régimen One Stop Shop (OSS)",
                action: () => GWT.iLoad(GWT.MODEL_369)
			}, {
                description: "Declaración SII ",
                title: "Suministro Inmediato de Información",
                // disabled: !this.icc?.isSii(),
                action: () => GWT.iLoad(GWT.NEW_MODEL_SII)
            }],
            filter: () => this.isNotDomainManagementAvailable()
        }, {
            title: 'IRPF AEAT',
            visible: this.administration.isCommonTerritory() || this.administration.isCanarias() || this.administration.isUnknown(),
            disabled: !(this.administration.isCommonTerritory() || this.administration.isCanarias() || this.administration.isUnknown()),
            options: [{
                description: "Modelo 111 ",
                title: "Retenciones e ingresos a cuenta sobre rendimientos del trabajo y de actividades económicas, permios y determinadas ganancias patrimoniales e imputaciones de renta",
                action: () => GWT.iLoad(GWT.MODEL_111)
            }, {
                description: "Modelo 115 ",
                title: "Retenciones e ingresos a cuenta sobre determinadas rentas o rendimientos procedentes del arrendamiento de inmuebles urbanos",
                action: () => GWT.iLoad(GWT.MODEL_115)
            }, {
                description: "Modelo 123 ",
                title: "Retención e ingreso a cuenta sobre determinadas rendimientos del capital mobiliario o determinadas rentas",
                action: () => GWT.iLoad(GWT.MODEL_123)
            }, {
                description: "Modelo 180 ",
                title: "Resumen anual de retenciones e ingresos a cuenta. Rendimientos procedentes del arrendamiento de inmuebles Urbanos",
                action: () => GWT.iLoad(GWT.MODEL_180)
            }, {
                description: "Modelo 184 ",
                title: "Declaración anual. Entidades en régimen de atribución de rentas",
                action: () => GWT.iLoad(GWT.MODEL_184)
            }, {
                description: "Modelo 190 ",
                title: "Resumen anual de retenciones e ingresos a cuenta. Rendimientos del trabajo de determinadas actividades económicas, premios y determinadas imputaciones de renta",
                action: () => GWT.iLoad(GWT.MODEL_190)
            }, {
                description: "Modelo 193 ",
                title: "Resumen anual de retenciones e ingresos a cuenta. Rendimientosdel capital mobiliario, IS e IRNR sobre determinadas rentas",
                action: () => GWT.iLoad(GWT.MODEL_193)
            }],
            filter: () => this.isNotDomainManagementAvailable()
        }, {
            title: 'AEAT',
            visible: this.administration.isCommonTerritory() || this.administration.isCanarias() || this.administration.isUnknown(),
            disabled: !(this.administration.isCommonTerritory() || this.administration.isCanarias() || this.administration.isUnknown()),
            options: [{
                description: "Modelo 130 ",
                description2: " |Profes./Empresar.",
                title: "IRPF. Pago fraccionado. Empresarios y profesionales en estimación directa",
                action: () => GWT.iLoad(GWT.MODEL_130)
            }, {
                description: "Modelo 131 ",
                description2: " |Profes./Empresar.",
                title: "Pago fraccionado. Empresarios y profesionales en estimación objetiva",
                action: () => GWT.iLoad(GWT.MODEL_131)
            }, {
                description: "Modelo 200 ",
                description2: " |Sociedades",
                title: "Impuesto sobre Sociedades",
                action: () => GWT.iLoad(GWT.MODEL_200)
            }, {
                description: "Modelo 202 ",
                description2: " |Sociedades",
                title: "Impuesto Sociedades. Pago fraccionado",
                action: () => GWT.iLoad(GWT.MODEL_202)
            }],
            filter: () => this.isNotDomainManagementAvailable()
        }, {
            title: 'IVA Forales',
            visible: this.administration.isAlava()  || this.administration.isGipuzkoa() || this.administration.isBizkaia() || this.administration.isUnknown(),
            disabled: !(this.administration.isAlava()  || this.administration.isGipuzkoa() || this.administration.isBizkaia() || this.administration.isUnknown()),
            options: [{
                description: "Modelo 303 ",
                description2: " |320 Gipuzkoa",
                title: "IVA. Autoliquidación",
                action: () => GWT.iLoad(GWT.MODEL_303)
            }, {
                description: "Modelo 349 ",
                tite: "Declaración recapitulativa de operaciones intracomunitarias",
                action: () => GWT.iLoad(GWT.MODEL_349)
            }, {
                description: "Modelo 390 ",
                title: "Declaración resumen anual IVA",
                action: () => GWT.iLoad(GWT.MODEL_390_HF)
            }, {
                description: "Modelo 347 ",
                title: "Declaración anual operaciones con terceras personas",
                action: () => GWT.iLoad(GWT.MODEL_347)
            }, {
                description: "Declaración SII ",
                title: "Suministro Inmediato de Información",
                disabled: !this.icc?.hasSiiHistory(),
                action: () => GWT.iLoad(GWT.NEW_MODEL_SII)
            }],
            filter: () => this.isNotDomainManagementAvailable()
        }, {
            title: 'IRPF Forales',
            visible: this.administration.isAlava()  || this.administration.isGipuzkoa() || this.administration.isBizkaia() || this.administration.isUnknown(),
            disabled: !(this.administration.isAlava()  || this.administration.isGipuzkoa() || this.administration.isBizkaia() || this.administration.isUnknown()),
            options: [{
                description: "Modelo 110/111 ",
                title: "Retenciones e ingresos a cuenta sobre rendimientos del trabajo y de actividades económicas, premios y determinadas ganancias patrimoniales e imputaciones de renta",
                action: () => GWT.iLoad(GWT.MODEL_111)
            }, {
                description: "Modelo 115 ",
                tite: "Retenciones e ingresos a cuenta sobre determinadas rentas o rendimientos procedentes del arrendamiento de inmuebles urbanes",
                action: () => GWT.iLoad(GWT.MODEL_115)
            }, {
                description: "Modelo 123 ",
                title: "Retención e ingreso a cuenta sobre determinados rendimientos del capital mobiliario o determinadas rentas",
                action: () => GWT.iLoad(GWT.MODEL_123)
            }, {
                description: "Modelo 180 ",
                title: "Resumen anual de retenciones e ingresos a cuenta. Rendimientos procedentes del arrendamiento de inmuebles Urbanos",
                action: () => GWT.iLoad(GWT.MODEL_180)
            }, {
                description: "Modelo 184 ",
                title: "Declaración anual. Entidades en régimen de atribución de rentas",
                action: () => GWT.iLoad(GWT.MODEL_184)
            }, {
                description: "Modelo 190 ",
                title: "Resumen anual de retenciones e ingresos a cuenta. Rendimientos del trabajo de determinadas actividades económicas, premios y determinadas imputaciones de renta",
                action: () => GWT.iLoad(GWT.MODEL_190)
            }, {
                description: "Modelo 193 ",
                title: "Resumen anual de retenciones e ingresos a cuenta. Rendimientos del capital mobiliario, IS e IRNR sobre determinadas rentas",
                action: () => GWT.iLoad(GWT.MODEL_193)
            }],
            filter: () => this.isNotDomainManagementAvailable()
        }, {
            title: 'LROE Bizkaia',
            visible: this.administration.isBizkaia() || this.administration.isUnknown(),
            disabled: !(this.administration.isBizkaia() || this.administration.isUnknown()),
            options: [{
                description: "Modelo 140 ",
                title: "Libro-registro de operaciones económicas de personas físicas",
                disabled: !this.icc?.hasLroeHistory() || !isPersonaFisica(this.company.document),
                action: () => GWT.iLoad(GWT.MODEL_140)
            }, {
                description: "Modelo 240 ",
                tite: "Libro-registro de operaciones económicas de sociedades",
                disabled: !this.icc?.hasLroeHistory() || isPersonaFisica(this.company.document),
                action: () => GWT.iLoad(GWT.MODEL_240)
            }],
            filter: () => this.isNotDomainManagementAvailable()
        }, {
            title: 'IVA Navarra',
            visible: this.administration.isNavarra() || this.administration.isUnknown(),
            disabled: !(this.administration.isNavarra() || this.administration.isUnknown()),
            options: [{
                description: "Modelo F69 ",
                title: "Autoliquidación",
                action: () => GWT.iLoad(GWT.MODEL_303)
            }, {
                description: "Modelo 349 ",
                tite: "Declaración recapitulativa de operaciones intracomunitarias",
                action: () => GWT.iLoad(GWT.MODEL_349)
            }, {
                description: "Modelo 111 ",
                title: "Retenciones e ingresos a cuenta sobre rendimientos del trabajo y actividades económicas, premios y determinadas ganancias patrimoniales e imputaciones de renta",
                action: () => GWT.iLoad(GWT.MODEL_111)
            }, {
                description: "Modelo 347 ",
                title: "Declaración anual operaciones con terceras personas",
                action: () => GWT.iLoad(GWT.MODEL_347)
            }, {
                description: "Declaración SII ",
                title: "Suministro Inmediato de Información",
                disabled: !this.icc?.hasSiiHistory(),
                action: () => GWT.iLoad(GWT.NEW_MODEL_SII)
            }],
            filter: () => this.isNotDomainManagementAvailable()
        }, {
            title: 'IRPF Navarra',
            visible: this.administration.isNavarra() || this.administration.isUnknown(),
            disabled: !(this.administration.isNavarra() || this.administration.isUnknown()),
            options: [{
                description: "Modelo 745/715 ",
                title: "Retenciones e ingresos a cuenta sobre rendimientos del trabajo y actividades económicas, premios y determinadas ganancias patrimoniales e imputaciones de renta",
                action: () => GWT.iLoad(GWT.MODEL_111)
            }, {
                description: "Modelo 759/760 ",
                tite: "Retenciones e ingresos a cuenta sobre determinadas rentas o rendimientos procedentes del arrendamiento de inmuebles urbanos",
                action: () => GWT.iLoad(GWT.MODEL_115)
            }, {
                description: "Modelo 716 ",
                title: "Retención e ingreso a cuenta sobre determinados rendimientos del capital mobiliario o determinadas rentas",
                action: () => GWT.iLoad(GWT.MODEL_123)
            }, {
                description: "Modelo 180 ",
                title: "Resumen anual de retenciones e ingresos a cuenta. Rendimiento procedentes de arrendamiento de inmuebles Urbanos",
                action: () => GWT.iLoad(GWT.MODEL_180)
            }, {
                description: "Modelo 184 ",
                title: "Declaración anual. Entidades en régimen de atribución de rentas",
                action: () => GWT.iLoad(GWT.MODEL_184)
            }, {
                description: "Modelo 190 ",
                title: "Resumen anual de retenciones e ingresos a cuenta. Rendimientos del trabajo de determinadas actividades económicas, premios y determinadas imputaciones de renta",
                action: () => GWT.iLoad(GWT.MODEL_190)
            }, {
                description: "Modelo 193 ",
                title: "Resumen anual de retenciones e ingresos a cuenta. Rendimientos del capital mobiliario, IS e IRNR sobre determinadas rentas",
                action: () => GWT.iLoad(GWT.MODEL_193)
            }],
            filter: () => this.isNotDomainManagementAvailable()
        }, {
            title: 'IGIC Canarias',
            visible: this.administration.isCanarias() || this.administration.isUnknown(),
            disabled: !(this.administration.isCanarias() || this.administration.isUnknown()),
            options: [{
                description: "Modelo 420/417 ",
                title: "IGIC Autoliquidación",
                action: () => GWT.iLoad(GWT.MODEL_303)
            }, 
			{
                description: "Modelo 421 ",
                title: "IGIC Régimen Simplificado",
                action: () => GWT.iLoad(GWT.MODEL_421)
            }, 
			{
                description: "Modelo 425 ",
                title: "IGIC Declaración resumen anual",
                action: () => GWT.iLoad(GWT.MODEL_390)
            }, 
            {
                description: "Modelo 415 ",
                title: "Declaración anual operaciones con terceras personas",
                action: () => GWT.iLoad(GWT.MODEL_347)
			}],
            filter: () => this.isNotDomainManagementAvailable()
        }, {
            title: 'Matriz de empresas y modelos fiscales',
            visible: true,
            disabled: false,
            options: [{
                title: "Matriz de empresas y modelos fiscales",
                description: "Matriz de empresas y modelos fiscales",
                action: () => GWT.iLoad(GWT.MODEL_MATRIX, this.getApplication().CONTENT)
            }],
            filter: () => this.isDomainManagementAvailable()
        }];
    }
    /*
    build() {
        
    }
    */
}
if(!window.customElements.get(TAG.AON_FISCAL_MENU)){
    window.customElements.define(TAG.AON_FISCAL_MENU, AonFiscalMenu);
}