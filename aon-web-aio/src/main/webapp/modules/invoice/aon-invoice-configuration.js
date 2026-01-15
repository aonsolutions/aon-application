import { AonToolbar } from "../../components/aon-toolbar.js";
import { AonElement } from "../../components/AonElement.js";
import { MSG, TAG } from "../../environments/environments.js";
import { ToolbarType } from "../../models/enums.js";
import { getInvoiceConfiguration, saveInvoiceConfiguration } from "../../services/invoiceService.js";
import { AonInvoicePrint } from "./aon-invoice-print.js";
import { AonTab } from "../../components/aon-tab.js";
import { AonInvoiceCommunicationConfiguration } from "./aon-invoice-communication-configuration.js";
import { AonOcrConfiguration } from "./aon-ocr-configuration.js";

import * as ACTION from '../actions.js';
import * as LS from '../../services/localStorageService.js';
import { InvoiceCommunicationConfiguration } from "../../models/InvoiceCommunicationConfiguration.js";
import { isPersonaFisica, isValid } from "../../services/documentUtils.js";

export class AonInvoiceConfiguration extends AonElement {
    
    configuration;
    options;
    selectedOption;

    TOOLBAR;
    TABS;
    CONTENT;
    
    async connectedCallback () {
        this.configuration = await getInvoiceConfiguration();
        await this.buildDur();
        this.initialize();
        this.build();
  	}

    initialize() {
        this.configuration = this.configuration || {};
        this.TOOLBAR = 'invoiceConfigurationToolbar';
        this.TABS = 'invoiceConfigurationTabs';
        this.CONTENT = 'invoiceConfigurationContent';
        this.options = this.options || [
			{ title: MSG.INVOICE_PRINTING, fn: () => this.buildPrintConfiguration()},
            { title: MSG.COMMUNICATION, fn: () => this.buildCommunication()}
        ];
        if(this.getDur().isInvofox() && this.isConsole()) this.options.push({title: 'OCR', fn: () => this.buildOcrConfiguration()})
        if(!LS.isAonSolutions() && !this.configuration.print.active){
            this.options = [{ title: MSG.COMMUNICATION, fn: () => this.buildCommunication()}];
        }
    }

    build() {
        let toolbar = new AonToolbar();
		toolbar.id = this.TOOLBAR;
		toolbar.type = ToolbarType.SECONDARY;
		toolbar.title = MSG.INVOICE_CONFIGURATION;
		this.appendChild(toolbar);
		toolbar.addButton2(ACTION.SAVE, () => this.save());
        this.buildTabs();

        let content = this.createElement(TAG.DIV);
        content.id = this.CONTENT;
        this.appendChild(content);
        if(!LS.isAonSolutions() && !this.configuration.print.active){
            this.selectedOption = 'communication';
            this.buildCommunication();
        } else this.buildPrintConfiguration();
    }
    
	buildTabs() {
		let tab = new AonTab();
		tab.id = this.TABS;
		tab.setOptions(this.options);
		this.appendChild(tab);
	}

    buildPrintConfiguration() {
        let content = this.getElement(this.CONTENT);
        this.clearElement(content);
        let pc = new AonInvoicePrint();
        pc.setPrintConfiguration(this.configuration.print);
        pc.onChange(() => {
            this.configuration.print = pc.getPrintConfiguration();
        });
        content.appendChild(pc);
    }

    buildCommunication() {
        let content = this.getElement(this.CONTENT);
        this.clearElement(content);
        let communication =  new AonInvoiceCommunicationConfiguration();
        communication.setConfiguration(this.configuration);
        communication.onChange(() => {
            this.configuration = communication.getConfiguration();
            let com = communication.getCommunicationConfiguration();
            if(com instanceof InvoiceCommunicationConfiguration) {
                this.configuration.communication = com.toJSON();
            } else {
                this.configuration.communication = new InvoiceCommunicationConfiguration(com).toJSON();
            }
        });
        content.appendChild(communication);
        this.selectedOption = 'communication';
    }

    async buildOcrConfiguration() {
        let content = this.getElement(this.CONTENT);
        this.clearElement(content);
        let ocr =  new AonOcrConfiguration();
        ocr.setConfiguration(this.configuration.invofox);
        ocr.onChange(() => {
            this.configuration.invofox = ocr.getConfiguration();
        });
        content.appendChild(ocr);
    }


    save() {    
        if(this.checkCommunicationConfiguration(this.configuration)) {
            saveInvoiceConfiguration(this.configuration).then((c) => {
                this.showMessage(MSG.SAVED_DATA);
                this.configuration = c;

                if(this.selectedOption === 'communication') {
                    this.buildCommunication();
                }
            }).catch(() => {
                this.showMessageError(MSG.ERROR);
            });
        }
    }

    checkCommunicationConfiguration(config) {
            let isSpain = config && config.company && config.company.documentCountry && config.company.documentCountry === 'ES';
            if(!isSpain) return true;

            if(!config || !config.company || !config.company.document || !config.communication) return false;
            let icc = new InvoiceCommunicationConfiguration(config.communication);
            if(!isValid(config.company.document) && isSpain) {
                this.showMessageError("El NIF/CIF de la empresa no es válido.");
                return false;
            }
            if(isPersonaFisica(config.company.document) && !(config.company.person || config.person)) {
                this.showMessageError("Es obligatorio rellenar todos los datos de la persona física.");
                return false;
            } 
            if(!icc.isNoSif() && (icc.isCommonTerritory() || icc.isCanarias()) && !icc.isSii() && !icc.willBeSii() && !icc.isVerifactu() && !icc.willBeVerifactu() && !icc.isNoVerifactu() && !icc.willBeNoVerifactu()) {
                this.showMessageError("Es obligatorio selecionar Verifactu, No Verifactu o SII para empresas del territorio común.");
                return false;
            } 

            if(!icc.isNoSif() && (icc.isCommonTerritory() || icc.isCanarias()) &&
                !icc.isVerifactu() && icc.verifactuInvoice) {
                this.showMessageError("Es obligatorio seleccionar Verifactu, ya que existe una factura Verifactu en el año actual.");
                return false;                                
            }

            if(!icc.isNoSif() && (icc.isCommonTerritory() || icc.isCanarias()) &&
                !icc.isSii() && icc.siiInvoice) {
                this.showMessageError("Es obligatorio seleccionar SII, ya que existe una factura SII en el año actual.");
                return false;                                
            }

            if(!icc.isNoSif() && (icc.isCommonTerritory() || icc.isCanarias()) &&
                !icc.isNoVerifactu() && icc.noVerifactuInvoice) {
                this.showMessageError("Es obligatorio seleccionar No Verifactu, ya que existe una factura No Verifactu en el año actual.");
                return false;                                
            }

            if(!icc.getAdministration().isUnknown() && (icc.hasCommunication() || icc.willBeCommunication() || icc.isNoSif())) {
                return true;
            } else {
                this.showMessageError("Es obligatorio selecionar una administración para la comunicación electrónica de facturas.");
                return false;
        }
    }
}
if(!window.customElements.get(TAG.AON_INVOICE_CONFIGURATION)){
	window.customElements.define(TAG.AON_INVOICE_CONFIGURATION, AonInvoiceConfiguration);
}