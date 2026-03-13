import { AonBasicTable } from "../../components/aon-basic-table.js";
import { AonIconButton } from "../../components/aon-icon-button.js";
import { AonElement } from "../../components/AonElement.js";
import { createCard, createSelect } from "../../components/CreateComponent.js";
import { AON_ICONS, CSS, EVENT, MATERIAL_ICONS, MSG, TAG } from "../../environments/environments.js";
import { getAeatCertificates } from "../../services/documentalService.js";
import { communicateInvoice, getCommunicationHistory } from "../../services/invoiceService.js";

export class AonInvoiceCommunication extends AonElement {

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
    }

    build() {
        let communicationDiv = this.createElement(TAG.DIV);
        communicationDiv.id = this.COMMUNICATION;
        communicationDiv.className = CSS.AON_BLOCK;
        this.appendChild(communicationDiv);
        this.buildCommunicationCard(communicationDiv);

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
                    let ok = this.isCommunicationStatusOk(info.communicationType, info.communicationStatus);

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

                    let icon = this.createElement(TAG.I);
                    icon.className = "material-icons";
                    icon.title = this.getCommunicationStatusLabel(info.communicationType, info.communicationStatus);
                    icon.style.color = this.getCommunicationStatusColor(info.communicationType, info.communicationStatus);
                    icon.innerHTML = ok ? MATERIAL_ICONS.CHECK_CIRCLE : MATERIAL_ICONS.ERROR;
                    table.addCell(icon);

                    let span2 = this.createElement(TAG.SPAN);
                    span2.innerHTML = this.getCommunicationStatusLabel(info.communicationType, info.communicationStatus);
                    span2.style.color = this.getCommunicationStatusColor(info.communicationType, info.communicationStatus);
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
        icon.title = this.getCommunicationStatusLabel(commHist.type, commHist.status);
        icon.style.color = this.getCommunicationStatusColor(commHist.type, commHist.status);
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
        requestDownload.icon = MATERIAL_ICONS.FILE_DOWNLOAD;
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
        if ("SII" === type || "TBAI" === type) {
            let administration = this.configuration ? this.configuration.administration : '';
            if ("ALAVA" === administration) typeIconButton.aonIcon = AON_ICONS.AON_ARABA;
            else if ("BIZKAIA" === administration) typeIconButton.aonIcon = AON_ICONS.AON_BIZKAIA;
            else if ("GIPUZKOA" === administration) typeIconButton.aonIcon = AON_ICONS.AON_GIPUZKOA;
            else if ("NAVARRA" === administration) typeIconButton.aonIcon = AON_ICONS.AON_NAVARRA;
            else if ("COMMON_TERRITORY" === administration) typeIconButton.aonIcon = AON_ICONS.AON_AEAT;
            else if ("CANARIAS" === administration) typeIconButton.aonIcon = AON_ICONS.AON_CANARY;
        } else if ("LROE" === type) {
            typeIconButton.aonIcon = AON_ICONS.AON_BIZKAIA;
        } else if ("SERES" === type) {
            typeIconButton.aonIcon = AON_ICONS.SERES;
        } else if ("EMAIL" === type) {
            typeIconButton.icon = MATERIAL_ICONS.MAIL;
        } else if ("CLOSING" === type) {
            typeIconButton.icon = MATERIAL_ICONS.LOCK;
        } else if ("VERIFACTU" === type || "NO_VERIFACTU" === type) {
            typeIconButton.aonIcon = AON_ICONS.AON_AEAT;
        } else if ("SIF" === type) {
            typeIconButton.icon = MATERIAL_ICONS.SIF;
        }
        if (!typeIconButton.icon) {
            typeIconButton.icon = MATERIAL_ICONS.QUESTION_MARK;
        }
        return typeIconButton;
    }

    getCommunicationStatusLabel(type, status) {
        if ("PENDING" === status && type === "NO_VERIFACTU") return "Aceptada";
        else if("PENDING" === status) return "Pendiente";
        else if ("ACCEPTED" === status) return "Aceptada";
        else if ("ACCEPTED_WITH_ERRORS" === status) return "Aceptada con errores";
        else if ("EXTERNALLY_COMMUNICATED" === status) return "Com. Externamente";
        else if ("WRONG" === status) return "Incorrecta";
        else return "Sin Estado";
    }

    getCommunicationStatusColor(type, status) {
        if ("PENDING" === status && type === "NO_VERIFACTU") return "green";
        else if ("PENDING" === status) return "orange";
        else if ("ACCEPTED" === status) return "green";
        else if ("ACCEPTED_WITH_ERRORS" === status) return "yellow";
        else if ("EXTERNALLY_COMMUNICATED" === status) return "blue";
        else if ("WRONG" === status) return "red"
        else return "gray";
    }


    isCommunicationStatusOk(type, status) {
        if ("PENDING" === status && type === "NO_VERIFACTU") return true;
        else if ("PENDING" === status) return false;
        else if ("ACCEPTED" === status) return true;
        else if ("ACCEPTED_WITH_ERRORS" === status) return true;
        else if ("EXTERNALLY_COMMUNICATED" === status) return true;
        else if ("WRONG" === status) return false;
        else return false;
    }

    communicateInvoice(type) {
		if("LROE" === type || "VERIFACTU" === type || "TBAI" === type || "SII" === type) {	
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