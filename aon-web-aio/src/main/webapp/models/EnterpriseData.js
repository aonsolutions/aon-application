
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
    name; // String
    expression; // String 
    startDate; // Date
    endDate; // Date

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
        }
    }

    inRange( atDate ) {
        if (!atDate) atDate = new Date();
		return this.startDate
			&& this.startDate <= atDate
			&& (this.endDate == undefined || this.endDate >= atDate)
		;
    }

    getExpressionObject() {
        try {
            return JSON.parse(this.expression);
        } catch (e) {
            if ("test" === this.expression) {
                return {"test": true};
            } else {
                return {"test": false};
            }
        }
    }

    isTest() {
        let expr = this.getExpressionObject();
        return expr.test === true;
    }

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

}