import { AonElement } from "../../components/AonElement.js";
import { AonDialog } from '../../components/aon-dialog.js';
import { EVENT, MSG, TAG } from "../../environments/environments.js";
import { EnterpriseDataNames } from "../../models/EnterpriseData.js";
import { AonBasicTable } from "../../components/aon-basic-table.js";
import { getInvoiceCommunicationConfiguration, updateAdministration, updateNoSif
    , updateNoVerifactu, updateVerifactu, updateSii, updateTbai, updateLroe } from "../../services/invoiceService.js";
import { createCard, createDate, createSelect, createSwitch } from "../../components/CreateComponent.js";
import { InvoiceCommunicationConfig } from "../../models/InvoiceCommunicationConfig.js";
import { Administration, ADMINISTRATIONS_WITH_UNKNOWN } from "../../models/Administration.js";
import { ExemptType, EXEMPT_TYPES } from "../../models/ExemptType.js";

export class AonInvoiceCommunicationConfig extends AonElement {

    /** @type {InvoiceCommunicationConfig} */ 
    icc;
    today = new Date();
    /** @type {AonDialog} */ 
    dialog;

    async connectedCallback() {
        this.icc = await getInvoiceCommunicationConfiguration();
        if (this.icc) {
            this.icc = this.icc instanceof InvoiceCommunicationConfig
                ? this.icc
                : new InvoiceCommunicationConfig(this.icc);
        }
        await this.buildDur();
        this.initialize();
        this.build();
    }

    initialize() {
        this.id = this.id || 'aonInvoiceConfigurationCommunication';
        this.DIV = this.id + 'Div';

        this.COMMUNICATION_CARD = this.id + 'CommunicationCard';
        this.COMMUNICATION_CARD_DIV = this.COMMUNICATION_CARD + 'Div';
        this.COMMUNICATION_CARD_TABLE = this.COMMUNICATION_CARD + 'Table';
        this.ADMINISTRATION = this.COMMUNICATION_CARD_TABLE + 'Administration';
        this.NO_SIF  = this.COMMUNICATION_CARD_TABLE + 'NoSIF';
        this.NO_VERIFACTU = this.COMMUNICATION_CARD_TABLE + 'NoVerifactu';
        this.NO_VERIFACTU_INFO = this.NO_VERIFACTU + 'Info';
        this.VERIFACTU = this.COMMUNICATION_CARD_TABLE + 'Verifactu';
        this.VERIFACTU_INFO = this.VERIFACTU + 'Info';
        this.SII = this.COMMUNICATION_CARD_TABLE + 'Sii';
        this.SII_INFO = this.SII + 'Info';
        this.TBAI = this.COMMUNICATION_CARD_TABLE + 'Tbai';
        this.TBAI_INFO = this.TBAI + 'Info';
        this.LROE = this.COMMUNICATION_CARD_TABLE + 'Lroe';
        this.LROE_INFO = this.LROE + 'Info';
        this.DIALOG = this.COMMUNICATION_CARD + 'Dialog';
        this.DIALOG_TABLE = this.DIALOG + 'Table';
        this.DIALOG_START_DATE = this.DIALOG_TABLE + 'StartDate';
        this.DIALOG_EXEMPT = this.DIALOG_TABLE + 'Exempt';
        this.DIALOG_EXEMPT_TYPE = this.DIALOG_TABLE + 'ExemptType';

        // this.FACTURAE = this.COMMUNICATION_CARD_TABLE + 'Facturae';
    }

    build() {
        let div = this.createElement(TAG.DIV);
        div.id = this.DIV;
        div.style.display = 'flex';
        div.style.width = '100%';
        this.appendChild(div);
        this.buildCard(div);
        this.buildDialog(div);
    }

    showAndReload(icc) {
        this.showMessage(MSG.SAVED_DATA);
        this.reload(icc);
    }

    async showErrorAndReload(e) {
        this.showError(e);
        this.icc = await getInvoiceCommunicationConfiguration();
        if (this.icc) {
            this.icc = this.icc instanceof InvoiceCommunicationConfig
                ? this.icc
                : new InvoiceCommunicationConfig(this.icc);
        }
        this.reload(this.icc);
    }

    reload(icc) {
        if (icc != undefined) {
            this.icc = icc instanceof InvoiceCommunicationConfig
                ? icc
                : new InvoiceCommunicationConfig(icc);
        }
        let parent = this.getElement(this.DIV);
        this.clearElement(parent);
        this.buildCard(parent);
        this.buildDialog(parent);
    }

    buildCard(parent) {
        let card = createCard(this.COMMUNICATION_CARD, MSG.INVOICE_COMMUNICATION);
        card.style.width = '50%';
        parent.appendChild(card);

        let content = this.createElement(TAG.DIV);
        content.id = this.COMMUNICATION_CARD_DIV;
        card.setContent(content);

        let table = new AonBasicTable();
        table.id = this.COMMUNICATION_CARD_TABLE;
        content.appendChild(table);

        this.buildAdministration(table);
        this.buildNoSif(table);
        this.buildNoVerifactu(table);
        this.buildVerifactu(table);
        this.buildSii(table);
        this.buildTbai(table);
        this.buildLroe(table);
        // this.buildFacturae(table);
    }

    buildAdministration(table) {
        table.addRow();
        let administration = createSelect(this.ADMINISTRATION, 'Administración en la que tributa la empresa');
        table.addCell(administration, 2).style.height = '50px';
        administration.setOptions(Object.values(ADMINISTRATIONS_WITH_UNKNOWN));
        if (  this.icc.getAdministration() && this.icc.getAdministration().value) {
            administration.setValue(this.icc.getAdministration().value);
        } else {
            administration.setValue(Administration.UNKNOWN);
        }
        administration.addEventListener(EVENT.CHANGE, () => {
            this.icc.setAdministration( new Administration(administration.getValue()))
            this.reload(this.icc);
        });
    }

    buildNoSif(table) {
        table.addRow();
        let noSifSwitch = createSwitch(this.NO_SIF, 'La empresa emite facturas oficiales con la aplicación');
        noSifSwitch.checked = !this.icc.isNoSif(this.today);
        noSifSwitch.addEventListener(EVENT.CHANGE, () => this.updateNoSif(noSifSwitch.isChecked(),this.today));
        table.addCell(noSifSwitch, 2).style.height = '50px';
    }

    buildNoVerifactu(table) {
        if (this.icc.isNoVerifactu() || this.icc.isCanarias() || this.icc.isCommonTerritory()) {
            table.addRow();
            let noVerifactuSwitch = createSwitch( this.NO_VERIFACTU, MSG.NO_VERIFACTU);
            noVerifactuSwitch.checked = this.icc.isNoVerifactu(this.today);
            table.addCell(noVerifactuSwitch).style.height = '50px';
            noVerifactuSwitch.addEventListener(EVENT.CHANGE, () => {
                this.showDialog( 
                    "Opciones NO VERIFACTU",
                    (date, exemptType) => this.updateNoVerifactu(noVerifactuSwitch.isChecked(), date, exemptType)
                );
            });
            let infoDiv  = this.createElement(TAG.DIV);
            this.icc.getNoVerifactuStream()
                .forEach( ed => infoDiv.appendChild(this.getInfo(ed)));
            table.addCell(infoDiv).style.height = '50px';
        }
    }

    buildVerifactu(table) {
        if (this.icc.isVerifactu() || this.icc.isCanarias() || this.icc.isCommonTerritory()) {
            table.addRow();
            let verifactuSwitch = createSwitch( this.VERIFACTU, MSG.VERIFACTU);
            verifactuSwitch.checked = this.icc.isVerifactu(this.today);
            table.addCell(verifactuSwitch).style.height = '50px';

            verifactuSwitch.addEventListener(EVENT.CHANGE, () => {
                this.showDialog( 
                    "Opciones VERIFACTU",
                    (date, exemptType) => this.updateVerifactu(verifactuSwitch.isChecked(), date, exemptType)
                );
            });
            let infoDiv  = this.createElement(TAG.DIV);
            this.icc.getVerifactuStream()
                .forEach( ed => infoDiv.appendChild(this.getInfo(ed)));
            table.addCell(infoDiv).style.height = '50px';
        }
    }

    buildSii(table) {
        if (this.icc.isSii() 
         || this.icc.isAlava() 
         || this.icc.isGipuzkoa() 
         || this.icc.isCommonTerritory() 
         || this.icc.isNavarra() 
         || this.icc.isCanarias()) {
            table.addRow();
            let siiSwitch = createSwitch( this.SII, MSG.SII);
            siiSwitch.checked = this.icc.isSii(this.today);
            table.addCell(siiSwitch).style.height = '50px';
            siiSwitch.addEventListener(EVENT.CHANGE, () => {
                this.showDialog( 
                    "Opciones SII",
                    (date, exemptType) => this.updateSii(siiSwitch.isChecked(), date, exemptType)
                );
            });
            let infoDiv  = this.createElement(TAG.DIV);
            this.icc.getSiiStream()
                .forEach( ed => infoDiv.appendChild(this.getInfo(ed)));
            table.addCell(infoDiv).style.height = '50px';
        }
    }

    buildTbai(table) {
        if (this.icc.isTbai() || this.icc.isAlava() || this.icc.isGipuzkoa()) {
            table.addRow();
            let tbaiSwitch = createSwitch( this.TBAI, MSG.TICKETBAI);
            tbaiSwitch.checked = this.icc.isTbai(this.today);
            table.addCell(tbaiSwitch).style.height = '50px';
            tbaiSwitch.addEventListener(EVENT.CHANGE, () => {
                this.showDialog( 
                    "Opciones Ticket Bai",
                    (date, exemptType) => this.updateTbai(tbaiSwitch.isChecked(), date, exemptType)
                );
            });
            let infoDiv  = this.createElement(TAG.DIV);
            this.icc.getTbaiStream()
                .forEach( ed => infoDiv.appendChild(this.getInfo(ed)));
            table.addCell(infoDiv).style.height = '50px';
        }
    }

    buildLroe(table) {
        if (this.icc.isLroe() || this.icc.isBizkaia() ) {
            table.addRow();
            let lroeSwitch = createSwitch(this.LROE, MSG.LROE);
            lroeSwitch.checked = this.icc.isLroe(this.today);
            table.addCell(lroeSwitch).style.height = '50px';
            lroeSwitch.addEventListener(EVENT.CHANGE, () => {
                this.showDialog( 
                    "Opciones LROE / Ticket Bai",
                    (date, exemptType) => this.updateLroe(lroeSwitch.isChecked(), date, exemptType)
                );
            });
            let infoDiv  = this.createElement(TAG.DIV);
            this.icc.getLroeStream()
                .forEach( ed => infoDiv.appendChild(this.getInfo(ed)));
            table.addCell(infoDiv).style.height = '50px';
        }
    }

    // UPDATING METHODS
    getInfo(data) {
        let infoDiv  = this.createElement(TAG.DIV);
        infoDiv.style.fontStyle = 'italic';
        if (data.isTest()) {
            infoDiv.innerHTML = `[TEST] `;
        }
        let startDate = data.startDate;
        if (startDate) {
            infoDiv.innerHTML += `Desde: ${startDate.toLocaleDateString()}`;
        }
        if (infoDiv.innerHTML) {
            infoDiv.innerHTML += ' - ';
        }
        let endDate = data.endDate;
        if (endDate) {
            infoDiv.innerHTML += `Hasta: ${endDate.toLocaleDateString()}`;
        } else {
            infoDiv.innerHTML += 'En adelante';
        }
        return infoDiv;
    }

    payload( checked, enterpriseDataName, date ) {
        console.log("Tipo de cheked " + typeof checked);
        let action = checked ? 'enable' : 'disable';
        let payload = {
            action : action,
            name: enterpriseDataName,
            date: date,
            administration: this.icc?.getAdministration()?.value
        };
        console.log("PAYLOAD --> ", JSON.stringify(payload));
        return payload;
    }

    updateAdministration(checked, date) { 
        updateAdministration(this.payload( checked, EnterpriseDataNames.ADMINISTRATION, date ))
            .then( icc => this.showAndReload(icc))
            .catch( e => this.showErrorAndReload(e));
    }

    updateNoSif(checked, date) { 
        updateNoSif(this.payload( checked, EnterpriseDataNames.NO_SIF, date ))
            .then( icc => this.showAndReload(icc))
            .catch( e => this.showErrorAndReload(e));
    }

    updateNoVerifactu(checked, date) { 
        updateNoVerifactu(this.payload( checked, EnterpriseDataNames.NO_VERIFACTU, date ))
            .then( icc => this.showAndReload(icc))
            .catch( e => this.showErrorAndReload(e));
    }

    updateVerifactu(checked, date) { 
        updateVerifactu(this.payload( checked, EnterpriseDataNames.VERIFACTU, date ))
            .then( icc => this.showAndReload(icc))
            .catch( e => this.showErrorAndReload(e));
    }
    updateSii(checked, date) {
        updateSii   (this.payload( checked, EnterpriseDataNames.SII, date ))
            .then( icc => this.showAndReload(icc))
            .catch( e => this.showErrorAndReload(e));
    }

    updateTbai(checked, date) { 
        updateTbai(this.payload( checked, EnterpriseDataNames.TBAI, date ))
            .then( icc => this.showAndReload(icc))
            .catch( e => this.showErrorAndReload(e));
    }

    updateLroe(checked, date) { 
        updateLroe(this.payload( checked, EnterpriseDataNames.LROE, date ))
            .then( icc => this.showAndReload(icc))
            .catch( e => this.showError(e));
    }

    buildDialog( parent ) {
        this.dialog = new AonDialog();
        this.dialog.id = this.DIALOG;
        this.dialog.type = "other";
        if(!this.isMobile()) this.dialog.width = '600px';
        parent.appendChild(this.dialog);

        let div = this.createElement(TAG.DIV);
        div.style.margin = '15px';

        this.dialog.setContent(div);

		let aviso = this.createDiv();
		aviso.style.backgroundColor = '#fde400ff';
		aviso.style.padding = '10px';
		aviso.style.margin = '10px';
		aviso.innerHTML = "<span style='color:red'>Aviso Importante</span>: Como usuario de AON SIF (Sistema de Facturación) adaptado a la normativa de la \"ley antifraude\" y regulado por el Reglamento RRSIF (RD 1007/2023), debe cumplimentar los datos que se solicitan a continuación. El Cliente es el único responsable de la correcta activación de la modalidad de comunicación, configuración del software y validación de su certificado digital en el software para la comunicación de facturas a la Administración Tributaria (AEAT o Haciendas Forales) a través de los sistemas VeriFactu, No VeriFactu, LROE o Ticket BAI. <br><b>AON SOLUTIONS, S.L.U. no será responsable</b> de información no veraz o incorrecta incluida por el usuario en el SIF.</br>";

		div.appendChild(aviso);

        let tab = new AonBasicTable();
        tab.id = this.DIALOG_TABLE;
        div.appendChild(tab);
        
        tab.addRow();
        let startDate  = createDate( this.DIALOG_START_DATE, MSG.FROM);
        startDate.setDate(this.today);
        tab.addCell(startDate)
        
        tab.addRow();
        let exempt = createSwitch(this.DIALOG_EXEMPT, MSG.EXEMPT);
        let exemptType = createSelect(this.DIALOG_EXEMPT_TYPE, 'Causa de la exención');
        exempt.addEventListener(EVENT.CHANGE, () => {
            exemptType.disabled = !exempt.checked;
            if (!exempt.checked) {
                exemptType.setValue(ExemptType.EMPTY.value);
            }
        });

        tab.addCell(exempt);

        tab.addRow();
        tab.addCell(exemptType, 2).style.height = '50px';
        exemptType.setOptions(Object.values(EXEMPT_TYPES));
    }
 
    showDialog( title, callback ) {
        this.dialog.setTitle(title);
        this.dialog.addCancelAction(() => this.reload());
        this.dialog.addAcceptAction(() => {
            let date = this.getElement(this.DIALOG_START_DATE).date;
            let exemptType = this.getElement(this.DIALOG_EXEMPT_TYPE).value;
            callback(date, exemptType);
        });
        this.dialog.open();
    }
}

if (!window.customElements.get(TAG.AON_INVOICE_COMMUNICATION_CONFIG)) {
    window.customElements.define(TAG.AON_INVOICE_COMMUNICATION_CONFIG, AonInvoiceCommunicationConfig);
}