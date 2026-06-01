import { MSG, TAG } from '../../environments/environments.js';
import { AonSuiteMenu } from '../aon-suite-menu.js';
import { isPersonaFisica } from '../../services/documentUtils.js';
import { getCompany } from '../../services/companyService.js';
import { getInvoiceConfiguration } from '../../services/invoiceService.js';
import { InvoiceCommunicationConfiguration } from '../../models/InvoiceCommunicationConfiguration.js';

import * as GWT from '../../gwt/gwt.js';

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
        this.icc = new InvoiceCommunicationConfiguration(c.communication);
    }

    async fiscalInitialize() {
        this.AON_HEADER = 'aonHeader';
        this.ROOT_PANEL = 'rootPanel';
        this.RIGHT_PANEL = 'rightPanel';
        this.last = MSG.FISCAL_MODELS_MATRIX;
        this.new = MSG.NEW_ACTION;
        this.cardData={
            title: MSG.INVOICES,
            info:[MSG.WITH_PROFESSIONAL_IRPF, MSG.WITH_RENTAL_IRPF, MSG.INTRACOMUNITARIAS, MSG.EXTRACOMUNITARIAS]
        };
        this.selectOptions= [{
            title: MSG.MODEL_303,
            action : () => GWT.iLoad(GWT.MODEL_303)
        },{
            title: MSG.MODEL_111,
            action : () => GWT.iLoad(GWT.MODEL_111)
        },{
            title: MSG.MODEL_115,
            action : () => GWT.iLoad(GWT.MODEL_115)
        }];
        await this.initOptions();
    }

    async initOptions() {
		await this.initInvoiceConfiguration();
        this.options = [{
            title: MSG.AEAT + ' IVA',
            visible: this.icc?.isCommonTerritory() || this.icc?.isCanarias(),
            disabled: !(this.icc?.isCommonTerritory() || this.icc?.isCanarias()),
            options: [{
                description: MSG.MODEL_303,
                title: MSG.MODEL_303,
                action: () => GWT.iLoad(GWT.MODEL_303)
            }, {
                description: MSG.MODEL_349,
                title: MSG.MODEL_349,
                action: () => GWT.iLoad(GWT.MODEL_349)
            }, {
                description: MSG.MODEL_390,
                title: MSG.MODEL_390,
                action: () => GWT.iLoad(GWT.MODEL_390)
            }, {
                description: MSG.MODEL_347,
                title: MSG.MODEL_347,
                action: () => GWT.iLoad(GWT.MODEL_347)
            }, {
                description: MSG.MODEL_369,
                title: MSG.MODEL_369,
                action: () => GWT.iLoad(GWT.MODEL_369)
			}, {
                description: MSG.DECLARATION_SII,
                title: MSG.DECLARATION_SII,
                // disabled: !this.icc?.isSii(),
                action: () => GWT.iLoad(GWT.NEW_MODEL_SII)
            }],
            filter: () => this.isNotDomainManagementAvailable()
        }, {
            title: MSG.AEAT + ' IRPF',
            visible: this.icc?.isCommonTerritory() || this.icc?.isCanarias(),
            disabled: !(this.icc?.isCommonTerritory() || this.icc?.isCanarias()),
            options: [{
                description: MSG.MODEL_111,
                title: MSG.MODEL_111,
                action: () => GWT.iLoad(GWT.MODEL_111)
            }, {
                description: MSG.MODEL_115,
                title: MSG.MODEL_115,
                action: () => GWT.iLoad(GWT.MODEL_115)
            }, {
                description: MSG.MODEL_123,
                title: MSG.MODEL_123,
                action: () => GWT.iLoad(GWT.MODEL_123)
            }, {
                description: MSG.MODEL_180,
                title: MSG.MODEL_180,
                action: () => GWT.iLoad(GWT.MODEL_180)
            }, {
                description: MSG.MODEL_184,
                title: MSG.MODEL_184,
                action: () => GWT.iLoad(GWT.MODEL_184)
            }, {
                description: MSG.MODEL_190,
                title: MSG.MODEL_190,
                action: () => GWT.iLoad(GWT.MODEL_190)
            }, {
                description: MSG.MODEL_193,
                title: MSG.MODEL_193,
                action: () => GWT.iLoad(GWT.MODEL_193)
            }],
            filter: () => this.isNotDomainManagementAvailable()
        }, {
            title: MSG.AEAT,
            visible: this.icc?.isCommonTerritory() || this.icc?.isCanarias(),
            disabled: !(this.icc?.isCommonTerritory() || this.icc?.isCanarias()),
            options: [{
                description: MSG.MODEL_130,
                description2: " |Profes./Empresar.",
                title: MSG.MODEL_130,
                action: () => GWT.iLoad(GWT.MODEL_130)
            }, {
                description: MSG.MODEL_131,
                description2: " |Profes./Empresar.",
                title: MSG.MODEL_131,
                action: () => GWT.iLoad(GWT.MODEL_131)
            }, {
                description: MSG.MODEL_200,
                description2: " |Sociedades",
                title: MSG.MODEL_200,
                action: () => GWT.iLoad(GWT.MODEL_200)
            }, {
                description: MSG.MODEL_202,
                description2: " |Sociedades",
                title: MSG.MODEL_202,
                action: () => GWT.iLoad(GWT.MODEL_202)
            }],
            filter: () => this.isNotDomainManagementAvailable()
        }, {
            title: MSG.FORAL_IVA,
            visible: this.icc?.isAlava()  || this.icc?.isGipuzkoa() || this.icc?.isBizkaia(),
            disabled: !(this.icc?.isAlava()  || this.icc?.isGipuzkoa() || this.icc?.isBizkaia()),
            options: [{
                description: MSG.MODEL_303,
                description2: " |320 Gipuzkoa",
                title: MSG.MODEL_303,
                action: () => GWT.iLoad(GWT.MODEL_303)
            }, {
                description: MSG.MODEL_349,
                title: MSG.MODEL_349,
                action: () => GWT.iLoad(GWT.MODEL_349)
            }, {
                description: MSG.MODEL_390,
                title: MSG.MODEL_390,
                action: () => GWT.iLoad(GWT.MODEL_390_HF)
            }, {
                description: MSG.MODEL_347,
                title: MSG.MODEL_347,
                action: () => GWT.iLoad(GWT.MODEL_347)
            }, {
                description: MSG.DECLARATION_SII,
                title: MSG.DECLARATION_SII,
                disabled: !this.icc?.isSii(),
                action: () => GWT.iLoad(GWT.NEW_MODEL_SII)
            }],
            filter: () => this.isNotDomainManagementAvailable()
        }, {
            title: MSG.FORAL_IRPF,
            visible: this.icc?.isAlava()  || this.icc?.isGipuzkoa() || this.icc?.isBizkaia(),
            disabled: !(this.icc?.isAlava()  || this.icc?.isGipuzkoa() || this.icc?.isBizkaia()),
            options: [{
                description: MSG.MODEL_110_111,
                title: MSG.MODEL_110_111,
                action: () => GWT.iLoad(GWT.MODEL_111)
            }, {
                description: MSG.MODEL_115,
                title: MSG.MODEL_115,
                action: () => GWT.iLoad(GWT.MODEL_115)
            }, {
                description: MSG.MODEL_123,
                title: MSG.MODEL_123,
                action: () => GWT.iLoad(GWT.MODEL_123)
            }, {
                description: MSG.MODEL_180,
                title: MSG.MODEL_180,
                action: () => GWT.iLoad(GWT.MODEL_180)
            }, {
                description: MSG.MODEL_184,
                title: MSG.MODEL_184,
                action: () => GWT.iLoad(GWT.MODEL_184)
            }, {
                description: MSG.MODEL_190,
                title: MSG.MODEL_190,
                action: () => GWT.iLoad(GWT.MODEL_190)
            }, {
                description: MSG.MODEL_193,
                title: MSG.MODEL_193,
                action: () => GWT.iLoad(GWT.MODEL_193)
            }],
            filter: () => this.isNotDomainManagementAvailable()
        }, {
            title: MSG.FORAL_LROE_BIZKAIA,
            visible: this.icc?.isBizkaia(),
            disabled: !this.icc?.isBizkaia(),
            options: [{
                description: MSG.MODEL_140,
                title: MSG.MODEL_140,
                disabled: !this.icc?.isLroe() || !isPersonaFisica(this.company.document),
                action: () => GWT.iLoad(GWT.MODEL_140)
            }, {
                description: MSG.MODEL_240,
                title: MSG.MODEL_240,
                disabled: !this.icc?.isLroe() || isPersonaFisica(this.company.document),
                action: () => GWT.iLoad(GWT.MODEL_240)
            }],
            filter: () => this.isNotDomainManagementAvailable()
        }, {
            title: MSG.FORAL_IVA_NAVARRA,
            visible: this.icc?.isNavarra(),
            disabled: !this.icc?.isNavarra(),
            options: [{
                description: MSG.MODEL_F69,
                title: MSG.MODEL_F69,
                action: () => GWT.iLoad(GWT.MODEL_303)
            }, {
                description: MSG.MODEL_349,
                title: MSG.MODEL_349,
                action: () => GWT.iLoad(GWT.MODEL_349)
            }, {
                description: MSG.MODEL_111,
                title: MSG.MODEL_111,
                action: () => GWT.iLoad(GWT.MODEL_111)
            }, {
                description: MSG.MODEL_347,
                title: MSG.MODEL_347,
                action: () => GWT.iLoad(GWT.MODEL_347)
            }, {
                description: MSG.DECLARATION_SII,
                title: MSG.DECLARATION_SII,
                // disabled: !this.icc?.isSii(),
                action: () => GWT.iLoad(GWT.NEW_MODEL_SII)
            }],
            filter: () => this.isNotDomainManagementAvailable()
        }, {
            title: MSG.FORAL_IRPF_NAVARRA,
            visible: this.icc?.isNavarra(),
            disabled: !this.icc?.isNavarra(),
            options: [{
                description: MSG.MODEL_745_715,
                title: MSG.MODEL_745_715,
                action: () => GWT.iLoad(GWT.MODEL_111)
            }, {
                description: MSG.MODEL_759_760,
                title: MSG.MODEL_759_760,
                action: () => GWT.iLoad(GWT.MODEL_115)
            }, {
                description: MSG.MODEL_716,
                title: MSG.MODEL_716,
                action: () => GWT.iLoad(GWT.MODEL_123)
            }, {
                description: MSG.MODEL_180,
                title: MSG.MODEL_180,
                action: () => GWT.iLoad(GWT.MODEL_180)
            }, {
                description: MSG.MODEL_184,
                title: MSG.MODEL_184,
                action: () => GWT.iLoad(GWT.MODEL_184)
            }, {
                description: MSG.MODEL_190,
                title: MSG.MODEL_190,
                action: () => GWT.iLoad(GWT.MODEL_190)
            }, {
                description: MSG.MODEL_193,
                title: MSG.MODEL_193,
                action: () => GWT.iLoad(GWT.MODEL_193)
            }],
            filter: () => this.isNotDomainManagementAvailable()
        }, {
            title: MSG.FORAL_IGIC,
            visible: this.icc?.isCanarias(),
            disabled: !this.icc?.isCanarias(),
            options: [{
                description: MSG.MODEL_420_417,
                title: MSG.MODEL_420_417,
                action: () => GWT.iLoad(GWT.MODEL_303)
            },
			{
                description: MSG.MODEL_421,
                title: MSG.MODEL_421,
                action: () => GWT.iLoad(GWT.MODEL_421)
            },
			{
                description: MSG.MODEL_425,
                title: MSG.MODEL_425,
                action: () => GWT.iLoad(GWT.MODEL_390)
            },
            {
                description: MSG.MODEL_415,
                title: MSG.MODEL_415,
                action: () => GWT.iLoad(GWT.MODEL_347)
			}],
            filter: () => this.isNotDomainManagementAvailable()
        }, {
            title: MSG.FISCAL_MODELS_MATRIX,
            visible: true,
            disabled: false,
            options: [{
                title: MSG.FISCAL_MODELS_MATRIX,
                description: MSG.FISCAL_MODELS_MATRIX,
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