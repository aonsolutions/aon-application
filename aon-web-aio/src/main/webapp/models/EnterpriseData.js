
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
}