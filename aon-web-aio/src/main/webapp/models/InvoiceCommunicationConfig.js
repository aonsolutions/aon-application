import { Administration, ADMINISTRATIONS } from "./Administration";
import { EnterpriseData } from "./EnterpriseData";

export class InvoiceCommunicationConfig {

    /** @type {Administration} */ 
    administration;
    /** @type {EnterpriseData[]} */ 
    data; 

    constructor(data) {
        if (data) {
            this.administration = data.administration ? new Administration(data.administration) : ADMINISTRATIONS.UNKNOWN;
            this.data  = data.data ? data.data.map(ed => new EnterpriseData(ed)) : [];
        }
    }

    getAdministration = () => {
        return this.administration;
    }
    setAdministration = (administration) => {
        this.administration = administration;
    }

    isEmpty( value ) { return value == undefined || value == null || value.length == 0; }
    isNotEmpty( value ) { return !this.isEmpty(value); }

    // ADMINISTRATION
    isAlava() { return this.getAdministration().isAlava();}
    isBizkaia() { return this.getAdministration().isBizkaia();}
    isGipuzkoa() { return this.getAdministration().isGipuzkoa();}
    isNavarra() { return this.getAdministration().isNavarra();}
    isCommonTerritory() { return this.getAdministration().isCommonTerritory();}
    isCanarias() { return this.getAdministration().isCanarias();}
    isUnknown() { return this.getAdministration().isUnknown();}

    // NO SIF
	getNoSifStream() {return this.data.filter( ed => ed.isNoSif() );}
    /** type {EnterpriseData} */
	getNoSifData( atDate ) { return this.getNoSifStream().find(ed => ed.inRange(atDate)); }
	isNoSif( atDate ) { return !this.isEmpty(this.getNoSifData( atDate )); }

    // NO VERIFACTU
	getNoVerifactuStream() {return this.data.filter( ed => ed.isNoVerifactu() );}
    /** type {EnterpriseData} */
	getNoVerifactuData( atDate ) { return this.getNoVerifactuStream().find(ed => ed.inRange(atDate)); }
	isNoVerifactu( atDate ) { return this.isNotEmpty(this.getNoVerifactuData( atDate )); }

    // VERIFACTU
	getVerifactuStream() {return this.data.filter( ed => ed.isVerifactu() );}
    /** type {EnterpriseData} */
	getVerifactuData( atDate ) { return this.getVerifactuStream().find(ed => ed.inRange(atDate)); }
	isVerifactu( atDate ) { return this.isNotEmpty(this.getVerifactuData( atDate )); }

    // SII
	getSiiStream() {return this.data.filter( ed => ed.isSii() );}
    /** type {EnterpriseData} */
	getSiiData( atDate ) { return this.getSiiStream().find(ed => ed.inRange(atDate)); }
	isSii( atDate ) { return this.isNotEmpty(this.getSiiData( atDate )); }

    // TBAI
    getTbaiStream() {return this.data.filter( ed => ed.isTbai() );}
    /** type {EnterpriseData} */
    getTbaiData( atDate ) { return this.getTbaiStream().find(ed => ed.inRange(atDate)); } 
    isTbai( atDate ) { return this.isNotEmpty(this.getTbaiData( atDate )); }

    // LROE
    getLroeStream() {return this.data.filter( ed => ed.isLroe() );}
    /** type {EnterpriseData} */
    getLroeData( atDate ) { return this.getLroeStream().find(ed => ed.inRange(atDate)); } 
    isLroe( atDate ) { return this.isNotEmpty(this.getLroeData( atDate )); }
    

}