import { EnterpriseData } from "./EnterpriseData";

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

    hasCommunication(atDate) {
        return this.data
            .filter(ed => !ed.isNoSif())
            .some(ed => ed.inRange(atDate || new Date()));
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

    needCertificate(atDate) {
        return this.hasCommunication(atDate) 
            && !this.icc.isSif(atDate) 
		    && !this.icc.isNoVerifactu(atDate);
    }
}