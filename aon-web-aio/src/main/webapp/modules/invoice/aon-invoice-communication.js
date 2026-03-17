import { AonBasicTable } from "../../components/aon-basic-table.js";
import { AonIconButton } from "../../components/aon-icon-button.js";
import { AonElement } from "../../components/AonElement.js";
import { createCard, createSelect } from "../../components/CreateComponent.js";
import { AON_ICONS, CSS, EVENT, MATERIAL_ICONS, MSG, TAG } from "../../environments/environments.js";
import { ADMINISTRATIONS } from "../../models/Administration.js";
import { getCommunicationStatusColor, getCommunicationStatusLabel, INVOICE_COMMUNITACTION_TYPES, InvoiceCommunicationConfig, isCommunicationStatusOk } from "../../models/InvoiceCommunicationConfig.js";
import { getAeatCertificates } from "../../services/documentalService.js";
import { communicateInvoice, getCommunicationHistory, getInvoiceCommunicationConfig } from "../../services/invoiceService.js";

export class AonInvoiceCommunication extends AonElement {

    /** @type {InvoiceCommunicationConfig} */ 
    icc;
    invoice; // Invoice id.
    CARD;
    TABLE;
    COMMUNICATION_TABLE;

    connectedCallback() {
        this.initialize();
        this.build();
    }

    initialize() {
        this.id = this.id || "aonInvoiceCommunication";
        this.CARD = this.id + "Card";
        this.TABLE = this.CARD + "Table";
        this.COMMUNICATION_TABLE = this.CARD + "CommunicationTable";
        this.initializeConfiguration();
    }

    build() {
        let communicationDiv = this.createElement(TAG.DIV);
        communicationDiv.id = this.COMMUNICATION;
        communicationDiv.className = CSS.AON_BLOCK;
        this.appendChild(communicationDiv);
        this.buildCommunicationCard(communicationDiv);
    }
    
    async initializeConfiguration() {
        this.icc = await getInvoiceCommunicationConfig();
        if (this.icc) {
            this.icc = this.icc instanceof InvoiceCommunicationConfig
                ? this.icc
                : new InvoiceCommunicationConfig(this.icc);
        }
    }

    buildCommunicationCard(parent) {
        let title = "Detalle de comunicaciones de la factura";
        let card = createCard(this.CARD, title, parent);

        let table = this.getElement(this.TABLE);
        if (!table) {
            table = new AonBasicTable();
            table.id = this.TABLE;
            card.setContent(table);
        }
        table.removeRows();

        getCommunicationHistory(this.invoice).then(hist => {
            let keys = Object.keys(hist ?? {});
            if (keys.length === 0) {
                table.addRow();

                let span1 = this.createElement(TAG.SPAN);
                span1.innerHTML = MSG.NO_DATA;
                table.addCell(span1);
            } else {
                for (let i = 0; i < keys.length; i++) {
                    let key = keys[i];

                    let info = hist[key].communicationInfo;
                    let ok = isCommunicationStatusOk(info.communicationType, info.communicationStatus);

                    table.addRow();

                    let typeButton = this.getCommunicationTypeIcon(info.communicationType);
                    typeButton.id = key + i + "button";
                    if (info.checkUrl) {
                        typeButton.title = "Comprobar";
                        typeButton.addEventListener(EVENT.CLICK, () => open(info.checkUrl));
                    } else {
                        typeButton.style.cursor = "none";
                    }
                    table.addCell(typeButton);

                    let span1 = this.createElement(TAG.SPAN);
                    span1.innerHTML = key;
                    table.addCell(span1);

                    let icon = this.getCommunicationStatusIcon(info.communicationType, info.communicationStatus);
                    table.addCell(icon);

                    let span2 = this.createElement(TAG.SPAN);
                    span2.innerHTML = getCommunicationStatusLabel(info.communicationType, info.communicationStatus);
                    span2.style.color = getCommunicationStatusColor(info.communicationType, info.communicationStatus);
                    table.addCell(span2);

                    let infoDate = info.modification_date ?
                        info.modification_date : info.creation_date;
                    let span3 = this.createElement(TAG.SPAN);
                    span3.innerHTML = infoDate ? this.formatDate(infoDate) : "";
                    table.addCell(span3);

                    let infoUser = info.modification_user ?
                        info.modification_user : info.creation_user;
                    let span4 = this.createElement(TAG.SPAN);
                    span4.innerHTML = infoUser ? infoUser : "";
                    table.addCell(span4);

                    if(!ok ) {
                        let communicateButton = new AonIconButton();
                        communicateButton.icon = MATERIAL_ICONS.SEND;
                        communicateButton.title = "Comunicar";
                        communicateButton.addEventListener(EVENT.CLICK, () => this.communicateInvoice(info.communicationType));
                        table.addCell(communicateButton);
                    }

                    let commHist = hist[key].communicationHistory;

                    if (commHist && commHist.length > 0) {
                        table.addRow();

                        let dummySpan = this.createElement(TAG.SPAN);
                        table.addCell(dummySpan);

                        let histTable = this.getElement(this.COMMUNICATION_TABLE + "_" + key);
                        if (!histTable) {
                            histTable = new AonBasicTable();
                            histTable.id = this.COMMUNICATION_TABLE + "_" + key;
                            table.addCell(histTable, "5");
                        }
                        histTable.removeRows();

                        for (let x = 0; x < commHist.length; x++) {
                            let hist = commHist[x];
                            this.printCommunicationHistory(histTable, hist, i);
                        }

                    }
                }
            }
        });
    }

    printCommunicationHistory(table, commHist, i) {
        table.addRow();

        let span1 = this.createElement(TAG.SPAN);
        span1.innerHTML = commHist.operation;
        table.addCell(span1);

        let icon = this.createElement(TAG.I);
        icon.className = "material-icons";
        icon.title = getCommunicationStatusLabel(commHist.type, commHist.status);
        icon.style.color = getCommunicationStatusColor(commHist.type, commHist.status);
        icon.innerHTML = commHist.ok ? MATERIAL_ICONS.CHECK_CIRCLE : MATERIAL_ICONS.ERROR;
        table.addCell(icon);

        let span2 = this.createElement(TAG.SPAN);
        span2.innerHTML = commHist.date ? this.formatDate(commHist.date) : "";
        table.addCell(span2);

        let span3 = this.createElement(TAG.SPAN);
        span3.innerHTML = commHist.creation_user ? commHist.creation_user : "";
        table.addCell(span3);

        let requestDownload = new AonIconButton();
        requestDownload.title = "Descargar petición";
        requestDownload.icon = MATERIAL_ICONS.FILE_UPLOAD;
        requestDownload.addEventListener(EVENT.CLICK, () => {
            open(commHist.requestUrl, '_blank');
        });
        table.addCell(requestDownload);

        let responseDownload = new AonIconButton();
        responseDownload.icon = MATERIAL_ICONS.FILE_DOWNLOAD;
        responseDownload.title = "Descargar respuesta";
        responseDownload.addEventListener(EVENT.CLICK, () => {
            open(commHist.responseUrl, '_blank');
        });
        table.addCell(responseDownload);

        if (commHist.responseMessages && commHist.responseMessages.length > 0) {
            let showErrors = new AonIconButton();
            showErrors.icon = MATERIAL_ICONS.CHAT_ERROR;
            showErrors.title = "Mostrar errores";
            showErrors.style.color = "red";
            table.addCell(showErrors);

            let historyRowNumber = table.addRow();
            let historyTr = table.getRow(historyRowNumber);
            historyTr.style.display = "none";
            showErrors.addEventListener(EVENT.CLICK, () => {
                historyTr.style.display = historyTr.style.display == "none" ? "contents" : "none";
            });
            let ul = this.createElement(TAG.UL);
            ul.classList.add(CSS.AON_UL);
            ul.style.width = '100%';
            for (let i = 0; i < commHist.responseMessages.length; i++) {
                let item = commHist.responseMessages[i];
                let li = this.createElement(TAG.LI);
                li.style.paddingLeft = "20px";
                li.style.backgrounColor = 'transparent !important';
                let errSpan = this.createElement(TAG.SPAN);
                errSpan.innerHTML = item;
                li.appendChild(errSpan);
                ul.appendChild(li);
            }
            table.addCell(ul, "7");
        } else {
            let dummySpan = this.createElement(TAG.SPAN);
            table.addCell(dummySpan);
        }

    }

    formatDate(isoString) {
        const d = new Date(isoString);
        const pad = n => n.toString().padStart(2, "0");
        const day = pad(d.getDate());
        const month = pad(d.getMonth() + 1);
        const year = d.getFullYear();
        const hours = pad(d.getHours());
        const mins = pad(d.getMinutes());
        const secs = pad(d.getSeconds());
        return `${day}/${month}/${year} ${hours}:${mins}:${secs}`;
    }

    getCommunicationTypeIcon(type) {
        let typeIconButton = new AonIconButton();
        let atDate = this.invoice ? this.invoice.date : new Date();
        if (INVOICE_COMMUNITACTION_TYPES.SII === type || INVOICE_COMMUNITACTION_TYPES.TBAI === type) {
            if (this.icc.isAlava(atDate)) typeIconButton.aonIcon = AON_ICONS.AON_ARABA;
            else if (this.icc.isBizkaia(atDate)) typeIconButton.aonIcon = AON_ICONS.AON_BIZKAIA;
            else if (this.icc.isGipuzkoa(atDate)) typeIconButton.aonIcon = AON_ICONS.AON_GIPUZKOA;
            else if (this.icc.isNavarra(atDate)) typeIconButton.aonIcon = AON_ICONS.AON_NAVARRA;
            else if (this.icc.isCommonTerritory(atDate)) typeIconButton.aonIcon = AON_ICONS.AON_AEAT;
            else if (this.icc.isCanarias(atDate)) typeIconButton.aonIcon = AON_ICONS.AON_CANARY;
        } else if (INVOICE_COMMUNITACTION_TYPES.LROE === type) {
            typeIconButton.aonIcon = AON_ICONS.AON_BIZKAIA;
        } else if (INVOICE_COMMUNITACTION_TYPES.SERES === type) {
            typeIconButton.aonIcon = AON_ICONS.SERES;
        } else if (INVOICE_COMMUNITACTION_TYPES.EMAIL === type) {
            typeIconButton.icon = MATERIAL_ICONS.MAIL;
        } else if (INVOICE_COMMUNITACTION_TYPES.CLOSING === type) {
            typeIconButton.icon = MATERIAL_ICONS.LOCK;
        } else if (INVOICE_COMMUNITACTION_TYPES.VERIFACTU === type || INVOICE_COMMUNITACTION_TYPES.NO_VERIFACTU === type) {
            typeIconButton.aonIcon = AON_ICONS.AON_AEAT;
        } else if (INVOICE_COMMUNITACTION_TYPES.SIF === type) {
            typeIconButton.icon = MATERIAL_ICONS.SIF;
        }
        if (!typeIconButton.icon) {
            typeIconButton.icon = MATERIAL_ICONS.QUESTION_MARK;
        }
        return typeIconButton;
    }

    getCommunicationStatusIcon(type, status) {
        let icon = this.createElement(TAG.I);
        icon.className = "material-icons";
        icon.title = getCommunicationStatusLabel(type, status);
        icon.style.color = getCommunicationStatusColor(type, status);
        icon.innerHTML = isCommunicationStatusOk(type, status) 
            ? MATERIAL_ICONS.CHECK_CIRCLE 
            : MATERIAL_ICONS.ERROR;
        return icon;
    }

    communicateInvoice(type) {
		if (INVOICE_COMMUNITACTION_TYPES.LROE === type 
         || INVOICE_COMMUNITACTION_TYPES.VERIFACTU === type 
         || INVOICE_COMMUNITACTION_TYPES.TBAI === type 
         || INVOICE_COMMUNITACTION_TYPES.SII === type) {	
			this.certificateDialog((certificate) => this.communicatingInvoice(type, certificate));
		} else  this.communicatingInvoice(type);
	}

    certificateDialog(action) {
        let dialog = this.getApplication().getDialog();
        dialog.clear();
        if(!this.isMobile()) dialog.width = '400px';
        dialog.setTitle("Comunicar factura");
    
        let certSelect = createSelect("cert", MSG.CERTIFICATE);
        certSelect.setAlias("id", "name");	
    
        getAeatCertificates().then(certs => certSelect.setOptions(certs));
        dialog.setContent(certSelect);
        dialog.addAcceptAction(() => action(certSelect.value));
        dialog.open();
    }
    
    communicatingInvoice(type, certificate) {
        console.log("CERTIFICADO: " + certificate);
        let div = this.createDiv();
        let dialog = this.getApplication().getDialog();
        dialog.clear();
        if(!this.isMobile()) dialog.width = '400px';
        dialog.setTitle("Comunicando factura");
        dialog.setContent(div);
        dialog.open();
   
        let icDiv = this.createDiv("invoiceCommunicationDiv" + i);
        icDiv.innerHTML = invoice.reference + " - comunicando...";
        div.appendChild(icDiv);
        let data = {
            type,
            invoice,
            certificate
        }
        communicateInvoice(data).then(() => {
            icDiv.innerHTML = invoice.reference + " - comunicada con éxito";
            icDiv.style.color = "green";
            icDiv.style.fontWeight = "bold";
        }).catch(e => {
            icDiv.innerHTML = invoice.reference + " - ERROR: " + e.message;
            icDiv.style.color = "red";
            icDiv.style.fontWeight = "bold";
        });
    }
}
if (!window.customElements.get(TAG.AON_INVOICE_COMMUNICATION)) {
    window.customElements.define(TAG.AON_INVOICE_COMMUNICATION, AonInvoiceCommunication);
}