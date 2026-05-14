import { Administration, ADMINISTRATIONS } from "./Administration.js";
import { EXEMPT_TYPES, ExemptType } from "./ExemptType.js";

export const EnterpriseDataNames = Object.freeze({
	ICC_ADMINISTRATION: 'ICC_ADMINISTRATION',
	ICC_LROE: 'ICC_LROE',
	ICC_SIF: 'ICC_SIF',
	ICC_SII: 'ICC_SII',
	ICC_TBAI: 'ICC_TBAI',
	ICC_SERES: 'ICC_SERES',
	ICC_EMAIL: 'ICC_EMAIL',
	ICC_VERIFACTU: 'ICC_VERIFACTU',
	ICC_NO_VERIFACTU: 'ICC_NO_VERIFACTU',
	ICC_FACTURAE: 'ICC_FACTURAE',
    ICC_NO_SIF: 'ICC_NO_SIF'
});

export class EnterpriseData {

    id; // Integer
    domain; // Integer
    enterprise; // Integer
    /** @type {Administration} */
    administration; 
    name; // String
    expression; // String 
    startDate; // Date
    endDate; // Date
    test; // boolean
    /** @type {ExemptType} */
    exemptType;

    updated; // boolean 

    constructor(data) {
        if (data) {
            this.id = data.id;
            this.domain = data.domain;
            this.enterprise = data.enterprise;
            this.name = data.name;
            this.expression = data.expression;
            this.startDate = data.startDate ? new Date(data.startDate) : undefined;
            this.endDate = data.endDate ? new Date(data.endDate) : undefined;
            this.updated = data.updated || false;
            this.exemptType = EXEMPT_TYPES.EMPTY;           
            this.administration = ADMINISTRATIONS.UNKNOWN;
            
            try {
                let exprObj = JSON.parse(this.expression);
                if (exprObj) {
                    this.administration = exprObj.administration
                        ? new Administration(exprObj.administration)
                        : ADMINISTRATIONS.UNKNOWN;
                    this.exemptType = exprObj.exemptType
                        ? new ExemptType(exprObj.exemptType)
                        : EXEMPT_TYPES.EMPTY;
                    this.test = exprObj.test === true;
                }

            } catch (e) {
                if (this.isAdministration()) {
                    this.administration = this.expression
                        ? new Administration(this.expression)
                        : ADMINISTRATIONS.UNKNOWN;
                } else if ("test" === this.expression) {
                    this.test = true;
                } else {
                    this.test = false;
                }
            }


        }
    }

    inRange( atDate ) {
        if (!atDate) atDate = new Date();
		return this.startDate
			&& this.startDate <= atDate
			&& (this.endDate == undefined || this.endDate >= atDate)
		;
    }

    isExempt() {
        return this.exemptType === null || this.exemptType?.name === EXEMPT_TYPES.EMPTY.name;
    }
    
    isAdministration() { return this.name === EnterpriseDataNames.ICC_ADMINISTRATION; }
    isNoSif() { return this.name === EnterpriseDataNames.ICC_NO_SIF; }
    isLroe() { return this.name === EnterpriseDataNames.ICC_LROE; }
    isSif() { return this.name === EnterpriseDataNames.ICC_SIF; }
    isSii() { return this.name === EnterpriseDataNames.ICC_SII; }
    isTbai() { return this.name === EnterpriseDataNames.ICC_TBAI; }
    isSeres() { return this.name === EnterpriseDataNames.ICC_SERES; }
    isEmail() { return this.name === EnterpriseDataNames.ICC_EMAIL; }
    isVerifactu() { return this.name === EnterpriseDataNames.ICC_VERIFACTU; }
    isNoVerifactu() { return this.name === EnterpriseDataNames.ICC_NO_VERIFACTU; }
    isFacturae() { return this.name === EnterpriseDataNames.ICC_FACTURAE; }

    isCommonTerritory() { return this.administration?.name === ADMINISTRATIONS.COMMON_TERRITORY.name; }
    isCanarias() { return this.administration?.name === ADMINISTRATIONS.CANARIAS.name; }
    isAlava() { return this.administration?.name === ADMINISTRATIONS.ALAVA.name; }
    isBizkaia() { return this.administration?.name === ADMINISTRATIONS.BIZKAIA.name; }
    isGipuzkoa() { return this.administration?.name === ADMINISTRATIONS.GIPUZKOA.name; }
    isNavarra() { return this.administration?.name === ADMINISTRATIONS.NAVARRA.name; }
    isUnknown() { return this.administration?.name === ADMINISTRATIONS.UNKNOWN.name; }

}