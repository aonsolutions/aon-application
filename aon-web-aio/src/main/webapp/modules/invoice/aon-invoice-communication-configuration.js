import { AonElement } from "../../components/AonElement.js";
import { CONSTANT, CSS, EVENT, MSG, TAG } from "../../environments/environments.js";
import { AonBasicTable } from "../../components/aon-basic-table.js";
import { AonDialog } from "../../components/aon-dialog.js";
import { createCard, createDate, createInput, createSelect, createSwitch } from "../../components/CreateComponent.js";
import { isPersonaFisica, isValid } from "../../services/documentUtils.js";
import { AonToast } from "../../components/aon-toast.js";
import { InvoiceCommunicationConfiguration } from "../../models/InvoiceCommunicationConfiguration.js";
import { Administration, ADMINISTRATIONS } from "../../models/Administration.js";
import { Countries } from "../../services/country.js";

export class AonInvoiceCommunicationConfiguration extends AonElement {

    configuration;
    communicationConfiguration;

    connectedCallback() {
        this.initialize();
        this.build();
    }

    initialize() {
        this.id = this.id || 'aonInvoiceConfigurationCommunication';
        this.DIV = this.id + 'Div';

        this.COMPANY_CARD = this.id + 'CompanyCard';
        this.COMPANY_CARD_DIV = this.COMPANY_CARD + 'Div';
        this.COMPANY_CARD_TABLE = this.COMPANY_CARD + 'Table';

        this.CARD = this.id + 'Card';
        this.CARD_TABLE = this.CARD + 'Table';
        this.CARD_DIV = this.CARD + 'Div';


        // FACTURAE
        this.FACTURAE = this.CARD_TABLE + 'Facturae';
        this.TBAI = this.CARD_TABLE + 'Tbai';
        this.LROE = this.CARD_TABLE + 'Lroe';
        this.VERIFACTU = this.CARD_TABLE + 'Verifactu';
        this.NO_VERIFACTU = this.CARD_TABLE + 'NoVerifactu';
        this.SII = this.CARD_TABLE + 'Sii';
    }

    build() {
        let div = this.createElement(TAG.DIV);
        div.id = this.DIV;
        div.style.display = 'flex';
        div.style.width = '100%';
        this.appendChild(div);
        this.buildCompanyCard(div);
        if (this.showCommunicationCard())
            this.buildCommunicationCard(div);
    }

    reload() {
        let parent = this.getElement(this.DIV);
        this.clearElement(parent);
        this.buildCompanyCard(parent);
        if (this.showCommunicationCard())
            this.buildCommunicationCard(parent);
    }

    showCommunicationCard() {
        return !this.communicationConfiguration.getAdministration().isUnknown() && isValid(this.configuration.company.document)
            && !this.communicationConfiguration.isNoSif();
    }

    buildCompanyCard(parent) {
        let card = createCard(this.COMPANY_CARD, MSG.COMPANY);
        card.style.width = '50%';
        parent.appendChild(card);

        let content = this.createElement(TAG.DIV);
        content.id = this.COMPANY_CARD_DIV;
        card.setContent(content);

        let table = new AonBasicTable();
        table.id = this.COMPANY_CARD_TABLE;
        content.appendChild(table);

        table.addRow();

        this.buildCompanyInfo(table);
        if(this.isSpainCompany()) this.buildAdministration(table);
        this.buildNoSif(table);
    }

    buildCommunicationCard(parent) {
        if (!this.communicationConfiguration.getAdministration().isUnknown() && isValid(this.configuration.company.document)) {
            let card = createCard(this.CARD, MSG.COMMUNICATIONS);
            card.style.width = '50%';
            parent.appendChild(card);

            let content = this.createElement(TAG.DIV);
            content.id = this.CARD_DIV;
            card.setContent(content);

            let table = new AonBasicTable();
            table.id = this.CARD_TABLE;
            content.appendChild(table);

            table.addRow();

            this.buildFacturae(table);

            if (this.communicationConfiguration.isAlava() || this.communicationConfiguration.isGipuzkoa()) {
                this.buildTicketBai(table);
                this.buildSii(table);
            } else if (this.communicationConfiguration.isBizkaia()) {
                this.buildLroe(table);
            } else if (this.communicationConfiguration.isNavarra()) {
                this.buildSii(table);
            } else {
                this.buildVerifactu(table);
                this.buildNoVerifactu(table);
                this.buildSii(table);
            }
        }
    }

    buildCompanyInfo(table) {
        let country = createSelect(this.id + 'CompanyCountry', MSG.COUNTRY);
        country.autocomplete = true;
        country.options = JSON.stringify(
            Countries.map((c) => {
                return { value: c.iso2, name: c.nombre };
            })
        );
        country.value = this.configuration.company.documentCountry || 'ES';
        country.addEventListener(EVENT.SELECT, () => {
            this.configuration.company.documentCountry = country.value;
            this.dispatchEvent(new Event(EVENT.CHANGE));
            this.reload();
        });
        table.addCell(country, 1).style.height = '50px';

        const valid = isValid(this.configuration.company.document);
        let document = createInput(this.id + 'companyDocument', MSG.DOCUMENT);
        document.setValue(this.configuration.company.document || '');

        if (valid) {
            document.disabled = valid;
            document.readonly = valid;
        }
        table.addCell(document, 1).style.height = '50px';
        if (!valid && this.isSpainCompany()) {
            document.addError('Documento no válido');
            document.addEventListener(EVENT.CHANGE, () => {
                this.configuration.company.document = document.value;
                this.dispatchEvent(new Event(EVENT.CHANGE));
                this.reload();
            });
        }
        table.addRow();
        if (valid && isPersonaFisica(this.configuration.company.document) && this.isSpainCompany()) {
            let nameValue = this.configuration.person && this.configuration.person.firstSurname
                ? this.configuration.person.firstName : this.configuration.company.name;
            let surname1Value = this.configuration.person && this.configuration.person.firstSurname
                ? this.configuration.person.firstSurname : '';
            let surname2Value = this.configuration.person && this.configuration.person.secondSurname
                ? this.configuration.person.secondSurname : '';
            let name = createInput(this.id + 'personName', MSG.NAME);
            name.setValue(nameValue);
            name.addEventListener(EVENT.CHANGE, () => {
                if (!this.configuration.person) this.configuration.person = {};
                if (!this.configuration.company.person) this.configuration.company.person = {};
                this.configuration.person.firstName = name.value;
                this.configuration.company.person.name = name.value;
                name.removeError();
                this.dispatchEvent(new Event(EVENT.CHANGE));
            });

            table.addCell(name, 2).style.height = '50px';
            if (nameValue === '') {
                name.addError('Este campo es obligatorio');
            }
            table.addRow();

            let surname1 = createInput(this.id + 'personSurname1', MSG.SURNAME + ' 1');
            surname1.setValue(surname1Value);
            surname1.addEventListener(EVENT.CHANGE, () => {
                if (!this.configuration.person) this.configuration.person = {};
                if (!this.configuration.company.person) this.configuration.company.person = {};
                this.configuration.person.firstSurname = surname1.value;
                this.configuration.company.person.surname1 = surname1.value;
                surname1.removeError();
                this.dispatchEvent(new Event(EVENT.CHANGE));
            });
            table.addCell(surname1, 1).style.height = '50px';
            if (surname1Value === '') {
                surname1.addError('Este campo es obligatorio');
            }

            let surname2 = createInput(this.id + 'personSurname2', MSG.SURNAME + ' 2');
            surname2.setValue(surname2Value);
            surname2.addEventListener(EVENT.CHANGE, () => {
                if (!this.configuration.person) this.configuration.person = {};
                if (!this.configuration.company.person) this.configuration.company.person = {};
                this.configuration.person.secondSurname = surname2.value;
                this.configuration.company.person.surname2 = surname2.value;
                surname2.removeError();
                this.dispatchEvent(new Event(EVENT.CHANGE));
            });
            table.addCell(surname2, 1).style.height = '50px';
            if (surname2Value === '') {
                surname2.addError('Este campo es obligatorio');
            }

            table.addRow();
        } else {
            let name = createInput(this.id + 'companyName', MSG.NAME);
            name.setValue(this.configuration.company.name);
            name.disabled = true;
            name.readonly = true;
            table.addCell(name, 2).style.height = '50px';

            table.addRow();
        }
    }

    buildAdministration(table) {
        const enterprise = this.configuration.company.id;
        let administration = createSelect(this.ADMINISTRATION, 'Administración');
        table.addCell(administration, 2).style.height = '50px';
        administration.setOptions(Object.values(ADMINISTRATIONS));
        administration.setValue(this.communicationConfiguration.getAdministration().value);
        administration.disabled = !this.communicationConfiguration.getAdministration().isUnknown()
            && this.communicationConfiguration.getAdministrationData()
            && this.communicationConfiguration.getAdministrationData().id;
        administration.addEventListener(EVENT.CHANGE, () => {
            this.communicationConfiguration.setAdministration(new Administration(administration.value), enterprise);
            this.dispatchEvent(new Event(EVENT.CHANGE));
            this.reload();
        });
        if (this.communicationConfiguration.getAdministration().isUnknown()) {
            administration.addError('Debe seleccionar una administración');
        }
        table.addRow();
    }

    buildNoSif(table) {
        const enterprise = this.configuration.company.id;
        if(!this.isSpainCompany() && !this.communicationConfiguration.isNoSif()) {
            this.communicationConfiguration.setNoSif(true, enterprise);
            this.dispatchEvent(new Event(EVENT.CHANGE));
        }
        let issueInvoice = createSwitch("emitefacturas", 'La empresa emite facturas oficiales con la aplicación');
        issueInvoice.checked = !this.communicationConfiguration.isNoSif();
        if (!this.communicationConfiguration.isNoSif() && !this.communicationConfiguration.hasCommunication()) {
            this.communicationConfiguration.setNoSif(!issueInvoice.isChecked(), enterprise);
        }
        issueInvoice.disabled = !this.isSpainCompany();
        // issueInvoice.disabled = !this.communicationConfiguration.isNoSif() && this.communicationConfiguration.hasCommunication();
        issueInvoice.addEventListener(EVENT.CHANGE, () => {
            this.communicationConfiguration.setNoSif(!issueInvoice.isChecked(), enterprise);
            this.dispatchEvent(new Event(EVENT.CHANGE));
            this.reload();
        });
        table.addCell(issueInvoice, 2).style.height = '50px';
        table.addRow();
    }

    buildFacturae(table) {
        let facturae = createSwitch(this.FACTURAE, 'Facturae');
        facturae.checked = this.configuration.eInvoice;
        facturae.addEventListener(EVENT.CHANGE, () => {
            this.configuration.eInvoice = facturae.isChecked();
            this.dispatchEvent(new Event(EVENT.CHANGE));
        });
        table.addCell(facturae, 1).style.height = '50px';
        facturae.setWidth('215px');
        table.addRow();
    }


    buildCommunication(table, id, title, active, future, data, futureData, activeFn, dateFn) {
        // let span = this.createSpan();
        // span.innerHTML = title;
        // span.style.fontWeight = 'bold';
        // table.addCell(span, 2).style.height = '30px';
        // table.addRow();

        const communicationActiveId = id + CONSTANT.ACTIVE.initCap();
        let communicationActive = createSwitch(communicationActiveId, future ? title + " " + MSG.PROGRAMMED : MSG.ACTIVATE + " " + title);
        communicationActive.checked = active || future;
        communicationActive.disabled = (active && data && data.id) ? true : false;
        communicationActive.addEventListener(EVENT.CHANGE, () => {
            activeFn(communicationActive.isChecked());
            this.dispatchEvent(new Event(EVENT.CHANGE));
            this.reload();
        });
        table.addCell(communicationActive, 1).style.height = '50px';
        communicationActive.setWidth('215px');

        if (active || future) {
            const communicationIncludeDateId = id + CONSTANT.INCLUDE_DATE.initCap();
            let communicationIncludeDate = createDate(communicationIncludeDateId, 'Fecha Inclusión ' + title);
            if (data && data.id) {
                communicationIncludeDate.disabled = true;
                communicationIncludeDate.readonly = true;
            }
            table.addCell(communicationIncludeDate, 1).style.height = '50px';
            communicationIncludeDate.setDate(future ? futureData.startDate : data.startDate);
            communicationIncludeDate.addEventListener(EVENT.CHANGE, () => {
                dateFn(communicationIncludeDate.getDate());
                this.dispatchEvent(new Event(EVENT.CHANGE));
                this.reload();
            });

        }
        // else { 
        //     const communicationExemptionId = id + CONSTANT.EXEMPTION.initCap();
        //     let exemption = createSelect(communicationExemptionId, MSG.EXEMPTION_CAUSE);
        //     table.addCell(exemption, 1).style.height = '50px';
        // }
        table.addRow();
    }

    buildTicketBai(table) {
        const enterprise = this.configuration.company.id;
        let active = this.communicationConfiguration.isTbai();
        let future = this.communicationConfiguration.willBeTbai();
        this.buildCommunication(table, this.TBAI, MSG.TICKETBAI, active, future, this.communicationConfiguration.getTbaiData(),
            this.communicationConfiguration.getFutureTbaiData(), (value) => this.communicationConfiguration.setTbai(value, enterprise),
            (date) => {
                let check = this.communicationConfiguration.setTbaiDate(date, enterprise)
                if (!check.valid) this.showError(check.message);
            }
        );
    }

    buildLroe(table) {
        const enterprise = this.configuration.company.id;
        let active = this.communicationConfiguration.isLroe();
        let future = this.communicationConfiguration.willBeLroe();
        this.buildCommunication(table, this.LROE, MSG.LROE + " / " + MSG.TICKETBAI, active, future, this.communicationConfiguration.getLroeData(),
            this.communicationConfiguration.getFutureLroeData(), (value) => this.communicationConfiguration.setLroe(value, enterprise),
            (date) => {
                let check = this.communicationConfiguration.setLroeDate(date, enterprise)
                if (!check.valid) this.showError(check.message);
            });
    }

    buildSii(table) {
        const enterprise = this.configuration.company.id;
        let active = this.communicationConfiguration.isSii();
        let future = this.communicationConfiguration.willBeSii();
        this.buildCommunication(table, this.SII, MSG.SII, active, future, this.communicationConfiguration.getSiiData(),
            this.communicationConfiguration.getFutureSiiData(), (value) => this.communicationConfiguration.setSii(value, enterprise),
            (date) => {
                let check = this.communicationConfiguration.setSiiDate(date, enterprise)
                if (!check.valid) this.showError(check.message);
            });
    }

    buildVerifactu(table) {
        const enterprise = this.configuration.company.id;
        const document = this.configuration.company.document;
        let active = this.communicationConfiguration.isVerifactu();
        let future = this.communicationConfiguration.willBeVerifactu();
        this.buildCommunication(table, this.VERIFACTU, MSG.VERIFACTU, active, future, this.communicationConfiguration.getVerifactuData(),
            this.communicationConfiguration.getFutureVerifactuData(), (value) => this.communicationConfiguration.setVerifactu(value, enterprise),
            (date) => {
                let check = this.communicationConfiguration.setVerifactuDate(date, enterprise)
                if (!check.valid) this.showError(check.message);
            });
    }

    buildNoVerifactu(table) {
        const enterprise = this.configuration.company.id;
        let active = this.communicationConfiguration.isNoVerifactu();
        let future = this.communicationConfiguration.willBeNoVerifactu();
        this.buildCommunication(table, this.NO_VERIFACTU, MSG.NO_VERIFACTU, active, future, this.communicationConfiguration.getNoVerifactuData(),
            this.communicationConfiguration.getFutureNoVerifactuData(), (value) => this.communicationConfiguration.setNoVerifactu(value, enterprise),
            (date) => {
                let check = this.communicationConfiguration.setNoVerifactuDate(date, enterprise)
                if (!check.valid) this.showError(check.message);
            });
    }

    showError(error) {
        this.showToast({
            type: CONSTANT.ERROR,
            message: error
        });
    }

    showToast(object) {
        let toast = this.getElement(this.id + 'Toast');
        if (!toast) {
            toast = new AonToast();
            toast.id = this.id + 'Toast';
            this.appendChild(toast);
        }
        toast.start(object);
    }

    isSpainCompany() {
        return this.configuration.company.documentCountry === 'ES';
    }

    getConfiguration() {
        return this.configuration;
    }

    getCommunicationConfiguration() {
        return this.communicationConfiguration && this.communicationConfiguration instanceof InvoiceCommunicationConfiguration
            ? this.communicationConfiguration
            : new InvoiceCommunicationConfiguration(this.communicationConfiguration);
    }

    setConfiguration(configuration) {
        this.configuration = configuration;
        if (this.configuration.communication) {
            this.communicationConfiguration = this.configuration.communication instanceof InvoiceCommunicationConfiguration
                ? this.configuration.communication
                : new InvoiceCommunicationConfiguration(this.configuration.communication);
        }
    }
}
if (!window.customElements.get(TAG.AON_INVOICE_COMMUNICATION_CONFIGURATION)) {
    window.customElements.define(TAG.AON_INVOICE_COMMUNICATION_CONFIGURATION, AonInvoiceCommunicationConfiguration);
}