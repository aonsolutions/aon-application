import { AonBasicTable } from "../../components/aon-basic-table.js";
import { AonIconButton } from "../../components/aon-icon-button.js";
import { AonIcon } from "../../components/aon-icon";
import { AonElement } from "../../components/AonElement.js";
import { createCard } from "../../components/CreateComponent.js";
import { AON_ICONS, CSS, EVENT, MATERIAL_ICONS, MSG, TAG } from "../../environments/environments.js";
import { getCommunicationHistory } from "../../services/invoiceService.js";

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

                    table.addRow();

                    let typeButton = this.getCommunicationTypeIcon(info.communicationType);
                    typeButton.id = key + i + "button";
                    if (info.checkUrl && typeof info.checkUrl === 'string' && info.checkUrl.startsWith('http')) {
                        typeButton.title = "Comprobar";
                        typeButton.addEventListener(EVENT.CLICK, () => open(info.checkUrl, '_blank'));
                    } else {
                        // typeButton.style.cursor = "none";
                    }
                    table.addCell(typeButton);

                    let span1 = this.createElement(TAG.SPAN);
                    span1.innerHTML = key;
                    table.addCell(span1);

                    let icon = new AonIcon();
                    icon.className = "icons-color";
                    icon.title = this.getCommunicationStatusLabel(info.communicationStatus);
                    icon.color = this.getCommunicationStatusColor(info.communicationStatus);
                    icon.icon = history.ok ? MATERIAL_ICONS.CHECK_CIRCLE : MATERIAL_ICONS.ERROR;
                    table.addCell(icon);

                    let span2 = this.createElement(TAG.SPAN);
                    span2.innerHTML = this.getCommunicationStatusLabel(info.communicationStatus);
                    span2.dataset.color = this.getCommunicationStatusColor(info.communicationStatus);
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

                    let dummySpan7 = this.createElement(TAG.SPAN);
                    table.addCell(dummySpan7);
                    let dummySpan8 = this.createElement(TAG.SPAN);
                    table.addCell(dummySpan8);
                    let dummySpan9 = this.createElement(TAG.SPAN);
                    table.addCell(dummySpan9);

                    let commHist = hist[key].communicationHistory;

                    if (commHist && commHist.length > 0) {
                        for(let x = 0; x < commHist.length; x++) {
                            table.addRow();
                
                            let dummySpan = this.createElement(TAG.SPAN);
                            table.addCell(dummySpan);
                
                            let histTable = this.getElement(this.COMMUNICATION_TABLE + "_" + key);
                            if(!histTable) {
                                histTable = new AonBasicTable();
                                histTable.id = this.COMMUNICATION_TABLE + "_" + key;
                                histTable.style.display = "flex";
                                histTable.style.paddingLeft = "1rem";
                                table.addCell(histTable, "8" );
                            } else {
                                histTable.removeRows();
                            }
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

        let icon = new AonIcon();
        icon.className = "icons-color";
        icon.title = this.getCommunicationStatusLabel(commHist.status);;
        icon.color = this.getCommunicationStatusColor(commHist.status);
        icon.icon = commHist.ok ? MATERIAL_ICONS.CHECK_CIRCLE : MATERIAL_ICONS.ERROR;
        table.addCell(icon);

        let span2 = this.createElement(TAG.SPAN);
        span2.innerHTML = commHist.date ? this.formatDate(commHist.date) : "";
        table.addCell(span2);

        let span3 = this.createElement(TAG.SPAN);
        span3.innerHTML = commHist.creation_user ?  commHist.creation_user : "";
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
        responseDownload.title= "Descargar respuesta";
        responseDownload.addEventListener(EVENT.CLICK, () => {
            open(commHist.responseUrl, '_blank');
        });
        table.addCell(responseDownload);

        if (commHist.responseMessages && commHist.responseMessages.length > 0) {
            let showErrors  = new AonIconButton();
            showErrors.icon = MATERIAL_ICONS.CHAT_ERROR;
            showErrors.title= "Mostrar errores";
            showErrors.dataset.color = "red";
            table.addCell(showErrors);

            let historyRowNumber = table.addRow();
            let historyTr = table.getRow(historyRowNumber);
            historyTr.style.display = "none";
            showErrors.addEventListener(EVENT.CLICK, () => {
                historyTr.style.display = historyTr.style.display == "none" ? "block" : "none";
            });
            let ul = this.createElement(TAG.UL);
            ul.classList.add(CSS.AON_UL);
            ul.style.width = '100%';
            for(let i = 0; i < commHist.responseMessages.length; i++) {
                let item = commHist.responseMessages[i];
                let li = this.createElement(TAG.LI);
                li.style.paddingLeft = "20px";
                li.style.backgrounColor = 'transparent !important';
                let errSpan = this.createElement(TAG.SPAN);
                errSpan.innerHTML = item;
                li.appendChild(errSpan);
                ul.appendChild(li);
            }
            table.addCell(ul);
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
        } else if ("VERIFACTU" === type) {
            typeIconButton.aonIcon = AON_ICONS.AON_AEAT;
        } else if ("SIF" === type) {
            typeIconButton.icon = MATERIAL_ICONS.SIF;
        }
        if (!typeIconButton.icon) {
            typeIconButton.icon = MATERIAL_ICONS.QUESTION_MARK;
        }
        return typeIconButton;
    }

    getCommunicationStatusLabel(status) {
        if ("PENDING" === status) return "Pendiente";
        else if ("ACCEPTED" === status) return "Aceptada";
        else if ("ACCEPTED_WITH_ERRORS" === status) return "Aceptada con errores";
        else if ("EXTERNALLY_COMMUNICATED" === status) return "Com. Externamente";
        else if ("WRONG" === status) return "Incorrecta";
        else return "Sin Estado";
    }

    getCommunicationStatusColor(status) {
        if ("PENDING" === status) return "orange";
        else if ("ACCEPTED" === status) return "green";
        else if ("ACCEPTED_WITH_ERRORS" === status) return "yellow";
        else if ("EXTERNALLY_COMMUNICATED" === status) return "blue";
        else if ("WRONG" === status) return "red"
        else return "gray";
    }
}
if (!window.customElements.get(TAG.AON_INVOICE_COMMUNICATION)) {
    window.customElements.define(TAG.AON_INVOICE_COMMUNICATION, AonInvoiceCommunication);
}