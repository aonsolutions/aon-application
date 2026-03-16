import { AonElement } from "../../components/AonElement.js";
import { AonDialog } from '../../components/aon-dialog.js';
import { EVENT, MSG, TAG, CONSTANT } from "../../environments/environments.js";
import { EnterpriseData, EnterpriseDataNames } from "../../models/EnterpriseData.js";
import { AonBasicTable } from "../../components/aon-basic-table.js";
import { getInvoiceCommunicationConfig, updateICC } from "../../services/invoiceService.js";
import { createCard, createDate, createSelect, CreateComponent } from "../../components/CreateComponent.js";
import { InvoiceCommunicationConfig } from "../../models/InvoiceCommunicationConfig.js";
import { Administration, ADMINISTRATIONS } from "../../models/Administration.js";
import { ExemptType, EXEMPT_TYPES } from "../../models/ExemptType.js";
import { AonDateUtils } from '../utils/AonDateUtils.js';
import { AonCheckbox } from '../../components/aon-checkbox.js';

export class AonInvoiceCommunicationConfig extends AonElement {

    /** @type {InvoiceCommunicationConfig} */ 
    icc;
    infoDate = new Date();
    builders = [];
    NO_VERIFACTU_BUILDER;
    VERIFACTU_BUILDER;
    SII_BUILDER;
    TBAI_BUILDER;
    LROE_BUILDER;
    SIF_BUILDER;

    EDIT = "edit";
    ENABLE = "enable";
    DISABLE = "disable";

    async connectedCallback() {
        this.initialize();
        await this.initializeConfiguration();
        this.build();
    }

    initialize() {
        this.id = this.id || 'aonInvoiceConfigurationCommunication';
        this.DIV = this.id + 'Div';

        this.COMMUNICATION_CARD = this.id + 'CommunicationCard';
        this.COMMUNICATION_CARD_DIV = this.COMMUNICATION_CARD + 'Div';
        this.COMMUNICATION_CARD_TABLE = this.COMMUNICATION_CARD + 'Table';
        this.COMMUNICATION_CARD_DATA_TABLE = this.COMMUNICATION_CARD + 'DataTable';
        this.COMMUNICATION_CARD_OPTIONS_DIV = this.COMMUNICATION_CARD + 'OptionsDiv';
        this.DIALOG = this.COMMUNICATION_CARD + 'Dialog';
        this.DIALOG_TABLE = this.DIALOG + 'Table';
        this.DIALOG_ADMINISTRATION = this.DIALOG + 'Administration';
        this.DIALOG_START_DATE = this.DIALOG_TABLE + 'StartDate';
        this.DIALOG_END_DATE = this.DIALOG_TABLE + 'EndDate';
        this.DIALOG_EXEMPT_TYPE = this.DIALOG_TABLE + 'ExemptType';
        this.DIALOG_ACCEPT_CHECK  = this.DIALOG_TABLE + 'AcceptCheck';
    }

    DATA_NAME_TO_MSG = {
        [EnterpriseDataNames.ICC_NO_VERIFACTU] : MSG.NO_VERIFACTU,
        [EnterpriseDataNames.ICC_VERIFACTU]    : MSG.VERIFACTU,
        [EnterpriseDataNames.ICC_SII]          : MSG.SII,
        [EnterpriseDataNames.ICC_TBAI]         : MSG.TICKETBAI,
        [EnterpriseDataNames.ICC_LROE]         : MSG.LROE,
        [EnterpriseDataNames.ICC_SIF]          : MSG.SIF,
    };

    DATA_NAME_TO_BUILDER = () => ({
        [EnterpriseDataNames.ICC_NO_VERIFACTU] : this.NO_VERIFACTU_BUILDER,
        [EnterpriseDataNames.ICC_VERIFACTU]    : this.VERIFACTU_BUILDER,
        [EnterpriseDataNames.ICC_SII]          : this.SII_BUILDER,
        [EnterpriseDataNames.ICC_TBAI]         : this.TBAI_BUILDER,
        [EnterpriseDataNames.ICC_LROE]         : this.LROE_BUILDER,
        [EnterpriseDataNames.ICC_SIF]          : this.SIF_BUILDER,
    });

    ACTION_TO_MSG = {
        [this.EDIT]    : 'Editar ',
        [this.ENABLE]  : 'Activar ',
        [this.DISABLE] : 'Desactivar ',
    };
    async initializeConfiguration() {
        this.icc = await getInvoiceCommunicationConfig();
        if (this.icc) {
            this.icc = this.icc instanceof InvoiceCommunicationConfig
                ? this.icc
                : new InvoiceCommunicationConfig(this.icc);
        }
        this.NO_VERIFACTU_BUILDER = {
            name : EnterpriseDataNames.ICC_NO_VERIFACTU,
            active : this.icc.isNoVerifactu(this.infoDate),
            administrations: [ADMINISTRATIONS.COMMON_TERRITORY, ADMINISTRATIONS.CANARIAS],
            optId : this.NO_VERIFACTU,
            optMsg : MSG.NO_VERIFACTU,
        };
        this.VERIFACTU_BUILDER = {
            name : EnterpriseDataNames.ICC_VERIFACTU,
            active : this.icc.isVerifactu(this.infoDate),
            administrations: [ADMINISTRATIONS.COMMON_TERRITORY, ADMINISTRATIONS.CANARIAS],
            optId : this.VERIFACTU,
            optMsg : MSG.VERIFACTU,
        };
        this.SII_BUILDER = {
            name : EnterpriseDataNames.ICC_SII,
            active : this.icc.isSii(this.infoDate),
            administrations: [ADMINISTRATIONS.COMMON_TERRITORY, ADMINISTRATIONS.CANARIAS, ADMINISTRATIONS.ALAVA
                , ADMINISTRATIONS.GIPUZKOA, ADMINISTRATIONS.NAVARRA],
            optId : this.SII,
            optMsg : MSG.SII,
        };
        this.TBAI_BUILDER = {
            name : EnterpriseDataNames.ICC_TBAI,
            active : this.icc.isTbai(this.infoDate),
            administrations: [ADMINISTRATIONS.ALAVA, ADMINISTRATIONS.GIPUZKOA],
            optId : this.TBAI,
            optMsg : MSG.TICKETBAI,
        };
        this.LROE_BUILDER = {
            name : EnterpriseDataNames.ICC_LROE,
            active : this.icc.isLroe(this.infoDate),
            administrations: [ADMINISTRATIONS.BIZKAIA],
            optId : this.LROE,
            optMsg : MSG.LROE,
        };
        this.SIF_BUILDER = {
            name : EnterpriseDataNames.ICC_SIF,
            active : this.icc.isSif(this.infoDate),
            administrations: [ADMINISTRATIONS.NAVARRA],
            optId : this.SIF,
            optMsg : MSG.SIF,
        };
        this.builders = [
            this.NO_VERIFACTU_BUILDER,
            this.VERIFACTU_BUILDER,
            this.SII_BUILDER,
            this.TBAI_BUILDER,
            this.LROE_BUILDER,
            this.SIF_BUILDER
        ];
    }

    build() {
        let div = this.createElement(TAG.DIV);
        div.id = this.DIV;
        div.style.display = 'flex';
        div.style.width = '100%';
        this.appendChild(div);
        this.buildCard(div);
    }

    showAndReload() {
        this.showMessage(MSG.SAVED_DATA);
        this.reload();
    }

    async showErrorAndReload(e) {
        this.showError(e);
        this.reload();
    }

    async reload() {
        await this.initializeConfiguration();
        this.reloadCard();
    }

    reloadCard() {
        let parent = this.getElement(this.DIV);
        this.clearElement(parent);
        this.buildCard(parent);
    }

    buildCard(parent) {
        let card = createCard(this.COMMUNICATION_CARD, MSG.INVOICE_COMMUNICATION);
        card.style.width = '80%';
        parent.appendChild(card);

        let content = this.createElement(TAG.DIV);
        content.id = this.COMMUNICATION_CARD_DIV;
        card.setContent(content);

        this.buildDataTable(content);

        let table = new AonBasicTable();
        table.id = this.COMMUNICATION_CARD_TABLE;
        content.appendChild(table);
        this.buildOptions( content );
    }

    buildDataTable(content) {
        let table = new AonBasicTable();
        table.id = this.COMMUNICATION_CARD_DATA_TABLE;
        content.appendChild(table);
        
        table.addRow();

        let cellAdmSpan = this.createSpan();
        cellAdmSpan.innerHTML = MSG.ADMINISTRATION;
        let cellAdm = table.addHeaderCell(cellAdmSpan);
        cellAdm.style.width = 'auto';
        cellAdm.style.minWidth = '300px';

        let cellComSpan = this.createSpan();
        cellComSpan.innerHTML = MSG.COMMUNICATION;
        let cellCom = table.addHeaderCell(cellComSpan);
        cellCom.style.width = '300px';

        let cellFromSpan = this.createSpan();
        cellFromSpan.innerHTML = MSG.FROM;
        let cellFrom = table.addHeaderCell(cellFromSpan);
        cellFrom.style.width = '150px';

        let cellToSpan = this.createSpan();
        cellToSpan.innerHTML = MSG.TO;
        let cellTo = table.addHeaderCell(cellToSpan);
        cellTo.style.width = '150px';

        let cellExemptionSpan = this.createSpan();
        cellExemptionSpan.style.width = "150px";
        cellExemptionSpan.innerHTML = MSG.EXEMPTION;
        let cellExemption = table.addHeaderCell(cellExemptionSpan);
        cellExemption.style.width = '150px';

        let cellTestSpan = this.createSpan();
        let cellTest = table.addHeaderCell(cellTestSpan);
        cellTest.style.width = '30px';

        let cellEditSpan = this.createSpan();
        let cellEdit = table.addHeaderCell(cellEditSpan);
        cellEdit.style.width = '20px';

        this.icc?.data
            .filter( cd => !cd.isAdministration() )
            .forEach( cd => this.addDataRow(table, cd));
    }

    addDataRow(tab, cd) {
        tab.addRow();

        let admSpan = this.createSpan();
        admSpan.style.whiteSpace = "nowrap";
        admSpan.innerHTML = cd.administration.name;
        tab.addCell(admSpan);        

        let comSpan = this.createSpan();
        comSpan.innerHTML = this.DATA_NAME_TO_MSG[cd.name] ?? cd.name;
        tab.addCell(comSpan);        

        let startDateSpan = this.createSpan();
        startDateSpan.innerHTML = AonDateUtils.formatDate(cd.startDate);
        tab.addCell(startDateSpan);
        
        let endDateSpan = this.createSpan();
        endDateSpan.innerHTML =  cd.endDate
            ?AonDateUtils.formatDate(cd.endDate)
            :"En adelante";
        tab.addCell(endDateSpan);

        let exemptTypeSpan = this.createSpan();
        exemptTypeSpan.innerHTML = cd.exemptType
            ?cd.exemptType.name
            :"------";
        let cellExempt = tab.addCell(exemptTypeSpan);
        cellExempt.style.textOverflow = "ellipsis";
        cellExempt.style.overflow = "hidden";
        cellExempt.style.whiteSpace = "nowrap";
        cellExempt.style.maxWidth = "150px";


        let testSpan  = this.createSpan();
        testSpan.innerHTML = cd.test?`(T)`:'';
        tab.addCell(testSpan);
        
        let icon = CreateComponent.createAonIconButton({
            attributes:{
                id:tab.id+cd.id+"Button",
                icon:"edit",
                title:`${MSG.EDIT}`,
            },
            events:{
                click: () => {
                    const admons = this.DATA_NAME_TO_BUILDER()[cd.name]?.administrations ?? [];
                    this.showDialog(this.EDIT, admons, cd);
                }
            }
            });
        tab.addCell(icon);

        let disableIcon = CreateComponent.createAonIconButton({
            attributes: {
                id: tab.id + cd.id + "DisableButton",
                icon: "delete_sweep",
                title: this.ACTION_TO_MSG[this.DISABLE] ,
            },
            events: {
                click: () => {
                    if (cd.endDate) {
                        let administration = cd.administration?.value;
                        let startDate = cd.startDate;
                        let startDateString = startDate
                            ?startDate.toLocaleDateString('en-CA') + 'T00:00:00.000Z'
                            :undefined;
                        let endDateString = cd.endDate.toLocaleDateString('en-CA') + 'T00:00:00.000Z';
                        let payload = {
                            action : this.DISABLE,
                            id: cd?.id,
                            administration : administration,
                            name : cd.name,
                            startDate: startDateString,
                            endDate: endDateString,
                            exemptType: cd.exemptType?.value
                        };
                        this.submitICC( payload );

                    } else {
                        const admons = this.DATA_NAME_TO_BUILDER()[cd.name]?.administrations ?? [];
                        this.showDialog(this.DISABLE, admons, cd);
                    }
                }
            }
        });
        tab.addCell(disableIcon);
    }

    buildOptions( content ) {
        let optionsDiv = this.createDiv();
        optionsDiv.id = this.COMMUNICATION_CARD_OPTIONS_DIV;
        optionsDiv.style.marginTop = '30px';
        content.appendChild(optionsDiv);
        this.builders
            .filter( builder => !builder.active )
            .forEach( builder => this.buildOption(optionsDiv, builder));
    }

    buildOption( container, builder ) {
        let optSpan  = this.createSpan();
        container.appendChild(optSpan);
        let enterpriseData = new EnterpriseData();
        enterpriseData.name = builder.name;
        enterpriseData.exemptType = builder.exemptType;
        enterpriseData.startDate = new Date();

        let switcher = this.createSpan( builder.optId);
        switcher.innerHTML = "[Activar "+builder.optMsg +"]";
        switcher.style.fontWeight = 'bold';
        switcher.style.textDecoration = 'underline';
        switcher.style.marginLeft = '10px';
        switcher.style.cursor = 'pointer';
        optSpan.appendChild(switcher);
        switcher.addEventListener(EVENT.CLICK, () => {
            this.showDialog( 
                this.ENABLE,
                builder.administrations,
                enterpriseData
            );
        });
    }

    showDialog( actionToPerform, administrations, cd) {
        let parent = this.getElement(this.DIV);
        let dialog = this.getElement(this.DIALOG);

        if (!dialog) {
            dialog = new AonDialog();
            dialog.id = this.DIALOG;
            dialog.type = "other";
            dialog.autoclose = false;
            if(!this.isMobile()) dialog.width = '600px';
            parent.appendChild(dialog);
        }
        dialog.clear();

        let div = this.createElement(TAG.DIV);
        div.style.margin = '15px';

        dialog.setContent(div);

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
        let administration = createSelect(this.DIALOG_ADMINISTRATION, 'Administración en la que tributa la empresa');
        tab.addCell(administration);
        administration.setOptions(administrations);
        let admon = cd.administration?.value;
        if (!admon) {
            if (this.icc.isAlava(this.infoDate)) admon = ADMINISTRATIONS.ALAVA.value;
            else if (this.icc.isGipuzkoa(this.infoDate)) admon = ADMINISTRATIONS.GIPUZKOA.value;
            else if (this.icc.isBizkaia(this.infoDate)) admon = ADMINISTRATIONS.BIZKAIA.value;
            else if (this.icc.isNavarra(this.infoDate)) admon = ADMINISTRATIONS.NAVARRA.value;
            else if (this.icc.isCanarias(this.infoDate)) admon = ADMINISTRATIONS.CANARIAS.value;
            else if (this.icc.isCommonTerritory(this.infoDate)) admon = ADMINISTRATIONS.COMMON_TERRITORY.value;
        }
        if (admon && administrations.some( a => a.value === admon )) {
            administration.setValue(admon);
        }
        administration.addEventListener(EVENT.CHANGE, () => {
            cd.administration = new Administration(administration.getValue());
            dialog.setTitle(this.getDialogTitle( actionToPerform, cd));
        });

        tab.addRow();
        let startDate  = createDate( this.DIALOG_START_DATE, MSG.FROM);
        if (this.EDIT === actionToPerform) {
            startDate.setDate(cd.startDate);
        } else {
            startDate.setDate(this.infoDate);
        }
        tab.addCell(startDate)
        
        if (this.DISABLE === actionToPerform || this.EDIT === actionToPerform) {
            tab.addRow();
            let endDate  = createDate( this.DIALOG_END_DATE, MSG.TO);
            endDate.setDate(cd.endDate);
            tab.addCell(endDate);
        }

        let exemptTypeSelect = createSelect(this.DIALOG_EXEMPT_TYPE, 'Causa de la exención');

        tab.addRow();
        tab.addCell(exemptTypeSelect, 2).style.height = '50px';
        exemptTypeSelect.setOptions(Object.values(EXEMPT_TYPES));
        exemptTypeSelect.setValue(cd.exemptType?.value ?? EXEMPT_TYPES.EMPTY.value);
        exemptTypeSelect.addEventListener(EVENT.CHANGE, () => {
            cd.exemptType = new ExemptType(exemptTypeSelect.getValue());
            dialog.setTitle(this.getDialogTitle( actionToPerform, cd));
        });

        dialog.setTitle(this.getDialogTitle(actionToPerform, cd));
        dialog.addCancelAction(() => this.reload());
        dialog.addAcceptAction(() => {
            let administration = this.getElement(this.DIALOG_ADMINISTRATION).value;
            let date = this.getElement(this.DIALOG_START_DATE).date;
            let startDateString = date
                ?date.toLocaleDateString('en-CA') + 'T00:00:00.000Z'
                :undefined;
            let endDate = (this.DISABLE === actionToPerform || this.EDIT === actionToPerform) 
                ? this.getElement(this.DIALOG_END_DATE).date 
                : undefined;
            let endDateString = endDate
                ?endDate.toLocaleDateString('en-CA') + 'T00:00:00.000Z'
                :undefined;
            let exemptType = new ExemptType(this.getElement(this.DIALOG_EXEMPT_TYPE).value);
            let payload = {
                action : actionToPerform,
                id: cd?.id,
                administration : administration,
                name : cd.name,
                startDate: startDateString,
                endDate: endDateString,
                exemptType: exemptType?.value
            };
            this.submitICC(payload);
        });
        dialog.getButtonAccept().disabled = true;

        let conditions = this.createDiv();
        div.appendChild(conditions);
        let table = new AonBasicTable();
        table.style.top = '20px';
        table.style.position = 'relative';
        conditions.appendChild(table);
        table.addRow();

        let checkBox = new AonCheckbox();
        checkBox.id = this.DIALOG_ACCEPT_CHECK;
        let td = table.addCell(checkBox)
        checkBox.addEventListener(EVENT.CHANGE, () => {
            dialog.getButtonAccept().disabled = !checkBox.isChecked();
        });
        td.style.width = '15px';
        let span3 = this.createElement(TAG.SPAN);
        span3.innerHTML = 'He leido y acepto las <a target="_blank" class="aonLink" href="http://aonsolutions.es/docs/aon_condiciones_generales_del_contrato.pdf">CONDICIONES GENERALES</a> del contrato de licencia de software y los términos <a target="_blank" class="aonLink" href="https://aonsolutions.es/docs/Aon-Declaracion%20Responsable%20VeriFactu.pdf">DECLARACIÓN RESPONSABLE del SIF</a> (Sistema Informático de facturación)';
        table.addCell(span3);

        dialog.open();
    }

    getDialogTitle(actionToPerform, enterpriseData) {
        let title = (this.ACTION_TO_MSG[actionToPerform] ?? '')
            + (this.DATA_NAME_TO_MSG[enterpriseData.name] ?? '');
        title = title.toUpperCase();
        if (enterpriseData.isExempt()) {
            title = `${title} con exención`;
        }
        let adm = new Administration( enterpriseData.administration?.value );
        if (adm && adm.name) {
            title += ` en ${adm.name}`;
        }
        return title;
    }

    // ---------------------------------------
    // ----------------- [ ENABLE METHODS ] --
    // ---------------------------------------    
    submitICC(payload) { 
        console.log("Payload a enviar: ", payload);
        updateICC(payload)
            .then( icc => this.showAndReload())
            .catch( e => this.showErrorAndReload(e));
    }
}

if (!window.customElements.get(TAG.AON_INVOICE_COMMUNICATION_CONFIG)) {
    window.customElements.define(TAG.AON_INVOICE_COMMUNICATION_CONFIG, AonInvoiceCommunicationConfig);
}
