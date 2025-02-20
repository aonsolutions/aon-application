import { getCountry } from "../../../services/country.js";
import { getBank } from "./banks.js";
export class BankAccount {

    iban;
    country;
    bank;
    bankCode;
    bic;

    constructor(iban) {
        this.iban = iban || '';
        if(this.iban.length > 2) {
            this.country = getCountry(this.iban.substring(0, 2)); 
        }

        if(this.isValidIban() && this.country.getIso2() === 'ES'){
            this.bankCode = this.iban.substring(4,8);
            let b = getBank(this.bankCode);
            this.bank = b.bank;
            this.bic = b.bic;
        }
    }

    getCountry() {
        return this.country;
    }

    setCountry(country) {
        this.country = country;
        return this;
    }

    getIban() {
        return this.iban;
    }

    setIban(iban) { 
        this.iban = iban;
        return this;
    }

    getBankCode() {
        return this.bankCode;
    }

    setBankCode(bankCode) {
        this.bankCode = bankCode;
        return this;
    }

    getBank() {
        return this.bank || '';
    }

    setBank(bank) {
        return this.bank;
    }

    getBic() {
        return this.bic;
    }

    setBic(bic) {
        this.bic = bic;
        return this; 
    }


    isValidIban() {
    	if (!this.isValidIbanLength()) return false;
        
		let control = this.calculateIbanControlDigit();
		return control != null && control === this.getIban().substring(2,4);
	}

    isValidIbanLength() {
	    if (!this.getCountry()) return false;
		return this.getIban().length == this.getCountry().getIbanLength();
	}

    calculateIbanControlDigit() {
		let ibanNumber = this.getIbanAsNumber();
		let control = this.calculateMod97(ibanNumber);
        let ctr = 98 - control;
        return ctr < 10 ? '0' + ctr : ctr + '';
	}

    calculateMod97(ibanNumber) {
        var remainder = ibanNumber,
            block;

        while (remainder.length > 2){
            block = remainder.slice(0, 9);
            remainder = parseInt(block, 10) % 97 + remainder.slice(block.length);
        }

        return parseInt(remainder, 10) % 97;
    }

    getIbanAsNumber() {
        let iban = this.getIban();
        iban = iban.toUpperCase();
        iban = iban.substr(4) + iban.substr(0,2) + '00';
        var A = 'A'.charCodeAt(0),
        Z = 'Z'.charCodeAt(0);
        return iban.split('').map(function(n){
            var code = n.charCodeAt(0);
            if (code >= A && code <= Z){
                // A = 10, B = 11, ... Z = 35
                return code - A + 10;
            } else {
                return n;
            }
        }).join('');
    }
}
