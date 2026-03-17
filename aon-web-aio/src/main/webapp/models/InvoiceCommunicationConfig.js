import { CONSTANT, MSG } from "../environments/environments";
import { EnterpriseData } from "./EnterpriseData";

export const INVOICE_COMMUNITACTION_TYPES = Object.freeze({
	LROE: 'LROE',
	TBAI: 'TBAI',
	SII: 'SII',
	SIF: 'SIF',
	VERIFACTU: 'VERIFACTU',
	NO_VERIFACTU: 'NO_VERIFACTU',
	FACTURAE: 'FACTURAE',
	SERES: 'SERES',
	EMAIL: 'EMAIL',
    NO_SIF: 'NO_SIF'
});

export class InvoiceCommunicationConfig {

    /** @type {EnterpriseData[]} */
    data;

    constructor(data) {
        if (data) {
            this.data = data.data ? data.data.map(ed => new EnterpriseData(ed)) : [];
        }
    }

    isEmpty(value)    { return value == undefined || value == null || value.length == 0; }
    isNotEmpty(value) { return !this.isEmpty(value); }

    isAlava(atDate)           { return this.data.filter(ed => ed.isAlava()).find(ed => ed.inRange(atDate)) != undefined; }
    isBizkaia(atDate)         { return this.data.filter(ed => ed.isBizkaia()).find(ed => ed.inRange(atDate)) != undefined; }
    isGipuzkoa(atDate)        { return this.data.filter(ed => ed.isGipuzkoa()).find(ed => ed.inRange(atDate)) != undefined; }
    isNavarra(atDate)         { return this.data.filter(ed => ed.isNavarra()).find(ed => ed.inRange(atDate)) != undefined; }
    isCommonTerritory(atDate) { return this.data.filter(ed => ed.isCommonTerritory()).find(ed => ed.inRange(atDate)) != undefined; }
    isCanarias(atDate)        { return this.data.filter(ed => ed.isCanarias()).find(ed => ed.inRange(atDate)) != undefined; }

    hasCommunication(atDate = new Date()) {
        return this.data
            .filter(ed => !ed.isNoSif())
            .some(ed => ed.inRange(atDate));
    }

    hasCommunicationByType = (emitida = true,atDate = new Date()) => {
        if (emitida) {
            return !this.isNoSif(atDate) 
                && (this.isTbai(atDate) 
                 || this.isLroe(atDate) 
                 || this.isVerifactu(atDate) 
                 || this.isNoVerifactu(atDate) 
                 || this.isSii(atDate) 
                 || this.isSif(atDate));
        } 
        return this.isLroe(atDate) || this.isSii(atDate);
    }

    // --------------------------------- [NO SIF]
    getNoSifStream()          { return this.data.filter(ed => ed.isNoSif()); }
    /** @type {EnterpriseData} */
    getNoSifData(atDate)      { return this.getNoSifStream().find(ed => ed.inRange(atDate)); }
    isNoSif(atDate)           { return !this.isEmpty(this.getNoSifData(atDate || new Date())); }

    // ------------------------------ [NO VERIFACTU]
    getNoVerifactuStream()        { return this.data.filter(ed => ed.isNoVerifactu()); }
    hasNoVerifactuHistory()       { return this.getNoVerifactuStream().length > 0; }
    /** @type {EnterpriseData} */
    getNoVerifactuData(atDate)    { return this.getNoVerifactuStream().find(ed => ed.inRange(atDate)); }
    isNoVerifactu(atDate)         { return this.isNotEmpty(this.getNoVerifactuData(atDate || new Date())); }

    // -------------------------------- [VERIFACTU]
    getVerifactuStream()      { return this.data.filter(ed => ed.isVerifactu()); }
    hasVerifactuHistory()     { return this.getVerifactuStream().length > 0; }
    /** @type {EnterpriseData} */
    getVerifactuData(atDate)  { return this.getVerifactuStream().find(ed => ed.inRange(atDate)); }
    isVerifactu(atDate)       { return this.isNotEmpty(this.getVerifactuData(atDate || new Date())); }

    // ------------------------------------ [SII]
    getSiiStream()            { return this.data.filter(ed => ed.isSii()); }
    hasSiiHistory()           { return this.getSiiStream().length > 0; }
    /** @type {EnterpriseData} */
    getSiiData(atDate)        { return this.getSiiStream().find(ed => ed.inRange(atDate)); }
    isSii(atDate)             { return this.isNotEmpty(this.getSiiData(atDate || new Date())); }

    // ----------------------------------- [TBAI]
    getTbaiStream()           { return this.data.filter(ed => ed.isTbai()); }
    hasTbaiHistory()          { return this.getTbaiStream().length > 0; }
    /** @type {EnterpriseData} */
    getTbaiData(atDate)       { return this.getTbaiStream().find(ed => ed.inRange(atDate)); }
    isTbai(atDate)            { return this.isNotEmpty(this.getTbaiData(atDate || new Date())); }

    // ----------------------------------- [LROE]
    getLroeStream()           { return this.data.filter(ed => ed.isLroe()); }
    hasLroeHistory()          { return this.getLroeStream().length > 0; }
    /** @type {EnterpriseData} */
    getLroeData(atDate)       { return this.getLroeStream().find(ed => ed.inRange(atDate)); }
    isLroe(atDate)            { return this.isNotEmpty(this.getLroeData(atDate || new Date())); }

    // ------------------------------------ [SIF]
    getSifStream()            { return this.data.filter(ed => ed.isSif()); }
    hasSifHistory()           { return this.getSifStream().length > 0; }
    /** @type {EnterpriseData} */
    getSifData(atDate)        { return this.getSifStream().find(ed => ed.inRange(atDate)); }
    isSif(atDate)             { return this.isNotEmpty(this.getSifData(atDate || new Date())); }

    hasCommunicationByType(type, atDate) {
        return (
            (type === "emitida"
                && (   this.isTbai(atDate)
                    || this.isLroe(atDate)
                    || this.isVerifactu(atDate)
                    || this.isNoVerifactu(atDate)
                    || this.isSif(atDate)
                    || this.isSii(atDate)
                )
            )
            ||
            (type === "recibida"
                && (   this.isLroe(atDate)
                    || this.isSii(atDate))
            )
        );
    }

    needCertificate(type, atDate) {
        return !this.isNoVerifactu(atDate)
            && !this.isSif(atDate) 
            && !this.isNoSif(atDate) 
		    && this.hasCommunicationByType(type, atDate);
    }
}

export const INVOICE_COMMUNICATION_STATUSES = Object.freeze({
    EMPTY: {
        value: 'EMPTY',
        name: '------------'
    },
	PENDING: {
        value: "PENDING",
        name: "Pendiente"
    },
	ACCEPTED: {
        value: "ACCEPTED",
        name: "Aceptada"
    },
	ACCEPTED_WITH_ERRORS: {
        value: "ACCEPTED_WITH_ERRORS",
        name: "Aceptada con Errores"
    },
	WRONG: {
        value: "WRONG",
        name: "Incorrecta"
    },
	CANCELLED: {
        value: "CANCELLED",
        name: "Anulada"
    },
	EXTERNALLY_COMMUNICATED: {
        value: "EXTERNALLY_COMMUNICATED",
        name: "Com. Externamente"
    }
});

export class InvoiceCommunicationStatus {
    value;
    name;

    constructor(value) { 
        Object.keys(INVOICE_COMMUNICATION_STATUSES).forEach(key => {
            if (INVOICE_COMMUNICATION_STATUSES[key].value === value) {
                this.value = INVOICE_COMMUNICATION_STATUSES[key].value;
                this.name = INVOICE_COMMUNICATION_STATUSES[key].name;
            }
        });
    }
}

export function getCommunicationTypeLabel(type) {
    if (INVOICE_COMMUNITACTION_TYPES.LROE === type) return MSG.LROE;
    else if (INVOICE_COMMUNITACTION_TYPES.SII === type) return MSG.SII;
    else if (INVOICE_COMMUNITACTION_TYPES.SIF === type) return MSG.SIF;
    else if (INVOICE_COMMUNITACTION_TYPES.VERIFACTU === type) return MSG.VERIFACTU;
    else if (INVOICE_COMMUNITACTION_TYPES.NO_VERIFACTU === type) return MSG.NO_VERIFACTU;
    else if (INVOICE_COMMUNITACTION_TYPES.FACTURAE === type) return MSG.FACTURAE;
    else if (INVOICE_COMMUNITACTION_TYPES.SERES === type) return MSG.SERES;
    else if (INVOICE_COMMUNITACTION_TYPES.EMAIL === type) return MSG.EMAIL;
    else if (INVOICE_COMMUNITACTION_TYPES.TBAI === type) return MSG.TICKETBAI;
    else return type;
}

export function getCommunicationStatusColor(type, status) {
    if (INVOICE_COMMUNICATION_STATUSES.PENDING.value === status && type === INVOICE_COMMUNITACTION_TYPES.NO_VERIFACTU) 
        return CONSTANT.GREEN;
    else if (INVOICE_COMMUNICATION_STATUSES.PENDING.value === status) return CONSTANT.ORANGE;
    else if (INVOICE_COMMUNICATION_STATUSES.ACCEPTED.value === status) return CONSTANT.GREEN;
    else if (INVOICE_COMMUNICATION_STATUSES.ACCEPTED_WITH_ERRORS.value === status) return CONSTANT.YELLOW;
    else if (INVOICE_COMMUNICATION_STATUSES.EXTERNALLY_COMMUNICATED.value === status) return CONSTANT.BLUE;
    else if (INVOICE_COMMUNICATION_STATUSES.WRONG.value === status) return CONSTANT.RED;
    return CONSTANT.GRAY;
}

export function getCommunicationStatusLabel(type, status) {
    if (INVOICE_COMMUNICATION_STATUSES.PENDING.value === status && type === INVOICE_COMMUNITACTION_TYPES.NO_VERIFACTU) 
        return MSG.ARCHIVED;
    else if (INVOICE_COMMUNICATION_STATUSES.ACCEPTED.value === status && type === INVOICE_COMMUNITACTION_TYPES.SIF) 
        return MSG.ARCHIVED;
    else if(INVOICE_COMMUNICATION_STATUSES.PENDING.value === status) return MSG.PENDING;
    else if (INVOICE_COMMUNICATION_STATUSES.ACCEPTED.value === status) return MSG.ACCEPTED;
    else if (INVOICE_COMMUNICATION_STATUSES.ACCEPTED_WITH_ERRORS.value === status) return MSG.ACCEPTED_WITH_ERRORS;
    else if (INVOICE_COMMUNICATION_STATUSES.EXTERNALLY_COMMUNICATED.value === status) return MSG.EXTERNALLY_COMMUNICATED;
    else if (INVOICE_COMMUNICATION_STATUSES.WRONG.value === status) return MSG.WRONG;
    return "";
}

export function isCommunicationStatusOk(type, status) {
    if (INVOICE_COMMUNICATION_STATUSES.PENDING.value === status && type === INVOICE_COMMUNITACTION_TYPES.NO_VERIFACTU) 
        return true;
    else if (INVOICE_COMMUNICATION_STATUSES.PENDING.value === status) return false;
    else if (INVOICE_COMMUNICATION_STATUSES.ACCEPTED.value === status) return true;
    else if (INVOICE_COMMUNICATION_STATUSES.ACCEPTED_WITH_ERRORS.value === status) return true;
    else if (INVOICE_COMMUNICATION_STATUSES.EXTERNALLY_COMMUNICATED.value === status) return true;
    else if (INVOICE_COMMUNICATION_STATUSES.WRONG.value === status) return false;
    else return false;
}
