import { Administration } from "./Administration";
import { EnterpriseData, EnterpriseDataNames } from "./EnterpriseData";

export class InvoiceCommunicationConfiguration {

    administrationHistory; // array of EnterpriseData
    _administrationHistory; // updated array of EnterpriseData 
    administration; // Enum Administration 
    _administration; // updated Enum Administration

    tbaiDataHistory; // array of EnterpriseData
    _tbaiDataHistory; // updated array of EnterpriseData
    tbaiData; // EnterpriseData

    lroeDataHistory; // array of EnterpriseData
    _lroeDataHistory; // updated array of EnterpriseData
    lroeData; // EnterpriseData
    lroeRegistryDate; // Date

    verifactuDataHistory; // array of EnterpriseData
    _verifactuDataHistory; // updated array of EnterpriseData
    verifactuData; // EnterpriseData

    noVerifactuDataHistory; // array of EnterpriseData
    _noVerifactuDataHistory; // updated array of EnterpriseData
    noVerifactuData; // EnterpriseData

    siiDataHistory; // array of EnterpriseData
    _siiDataHistory; // updated array of EnterpriseData
    siiData; // EnterpriseData
    siiRegistryDate; // Date

    sifDataHistory; // array of EnterpriseData
    _sifDataHistory; // updated array of EnterpriseData
    sifData; // EnterpriseData

    noSifDataHistory; // array of EnterpriseData
    _noSifDataHistory; // updated array of EnterpriseData
    noSifData; // EnterpriseData

    defaultCertificate; // Integer
    
    constructor(data) {
        if(data) {
            this.administrationHistory = data.administrationHistory ? data.administrationHistory.map(ed => new EnterpriseData(ed)) : [];
            this.administration = data.administration ? new Administration(data.administration) : undefined;

            this.tbaiDataHistory = data.tbaiDataHistory ? data.tbaiDataHistory.map(ed => new EnterpriseData(ed)) : [];
            this.tbaiData =  data.tbaiData ? new EnterpriseData(data.tbaiData) : undefined;

            this.lroeDataHistory = data.lroeDataHistory ? data.lroeDataHistory.map(ed => new EnterpriseData(ed)) : [];
            this.lroeData = data.lroeData ? new EnterpriseData(data.lroeData) : undefined;
            this.lroeRegistryDate = data.lroeRegistryDate ? new Date(data.lroeRegistryDate) : undefined;

            this.verifactuDataHistory = data.verifactuDataHistory ? data.verifactuDataHistory.map(ed => new EnterpriseData(ed)) : [];
            this.verifactuData = data.verifactuData ? new EnterpriseData(data.verifactuData) : undefined;
            this.noVerifactuDataHistory = data.noVerifactuDataHistory ? data.noVerifactuDataHistory.map(ed => new EnterpriseData(ed)) : [];
            this.noVerifactuData = data.noVerifactuData ? new EnterpriseData(data.noVerifactuData) : undefined;

            this.siiDataHistory = data.siiDataHistory ? data.siiDataHistory.map(ed => new EnterpriseData(ed)) : [];
            this.siiData = data.siiData ? new EnterpriseData(data.siiData) : undefined;
            this.siiRegistryDate = data.siiRegistryDate ? new Date(data.siiRegistryDate) : undefined;

            this.sifDataHistory = data.sifDataHistory ? data.sifDataHistory.map(ed => new EnterpriseData(ed)) : [];
            this.sifData = data.sifData ? new EnterpriseData(data.sifData) : undefined;
            this.noSifDataHistory = data.noSifDataHistory ? data.noSifDataHistory.map(ed => new EnterpriseData(ed)) : [];
            this.noSifData = data.noSifData ? new EnterpriseData(data.noSifData) : undefined;

            this.defaultCertificate = data.defaultCertificate;
        }
    }
    
    // Generic methods for EnterpriseData checks

    is = (data) => { // data: EnterpriseData
        return data && (!data.endDate || data.endDate > new Date()); 
    };

    isTest = (data) => { // data: EnterpriseData
        return this.is(data) && data.expression && data.expression.toLowerCase().includes("test");
    }

    was = (history) => { // history: array of EnterpriseData
        return history && history.length > 0 && history.some(d => 
            d.startDate && d.startDate < new Date());
    }

    willBe = (history) => { // history: array of EnterpriseData
        return history && history.length > 0 && history.some(d => 
            d.startDate && d.startDate > new Date());
    }

    getFutureData = (history) => { // history: array of EnterpriseData
        if(this.willBe(history)) {
            return history.find(d => d.startDate && d.startDate > new Date());
        }
        return null;
    }
    
    // Specific methods for Administration

    getAdministrationHistory = () => {
        return this._administrationHistory || this.administrationHistory;
    }
    
    getAdministration = () => {
        return this._administration || this.administration;
    }

    setAdministration = (administration) => {
        if(administration instanceof Administration && administration.value != this.administration.value) {
            this._administration = administration;
            this._administrationHistory = this.endHistory(this.administrationHistory);
            this._administrationHistory.push(this.newEnterpriseData(EnterpriseDataNames.ICC_ADMINISTRATION));
        } else if(administration instanceof Administration && administration.value === this.administration.value) {
            this._administration = undefined;
            this._administrationHistory = undefined;
        }
    }

    updateHistoryByAdministration = (administration) => {
        if(administration instanceof Administration) {
            if(administration.isAlava()) {
                
            } else if(administration.isBizkaia()) {

            } else if(administration.isGipuzkoa()) {

            } else if(administration.isNavarra()) {

            } else if(administration.isCommonTerritory()) {

            } else if(administration.isCanarias()) {

            } else {
                
            }            
        }
    }

    newEnterpriseData = (name, date) => {
        return new EnterpriseData({
            name,
            expression: "",
            startDate: date || new Date(),
            updated: true
        });
    }

    endHistory = (history, date) => {
        let auxHistory = history;   
        auxHistory.forEach((element, index)=> {
            if(!element.endDate) {
                auxHistory[index].endDate = date || new Date();
                auxHistory[index].updated = true;
            }
        });
        return auxHistory;
    }

    // Specific methods for TBAI communication type


    setTbai(tbai) {
        if(tbai && !this.isTbai()) {
            this.tbaiData = this.newEnterpriseData(EnterpriseDataNames.ICC_TBAI);
            this._tbaiDataHistory = this.tbaiDataHistory || [];
            this._tbaiDataHistory.push(this.tbaiData);

            this._siiDataHistory = this.endHistory(this.siiDataHistory);
            this._lroeDataHistory = this.endHistory(this.lroeDataHistory);
            this._verifactuDataHistory = this.endHistory(this.verifactuDataHistory);
            this._noVerifactuDataHistory = this.endHistory(this.noVerifactuDataHistory);
            this._sifDataHistory = this.endHistory(this.sifDataHistory);            
        } else if(!tbai && this.isTbai()) {
            this.tbaiData = undefined;
            this._tbaiDataHistory = this.endHistory(this.tbaiDataHistory);
            if(!this.hasCommunication()) {
                this.setSif(true);
            }
        }
    }

    setTbaiDate(date) {
        if(this.isTbai()) {
            this.tbaiData.startDate = date;
            this.tbaiData.updated = true;
        }
    }

    getTbaiDataHistory = () => {
        return this._tbaiDataHistory || this.tbaiDataHistory;
    }
   
    getTbaiData = () => {
        return this.tbaiData;
    }

    getFutureTbaiData = () => {
        return this.getFutureData(this.getTbaiDataHistory()); 
    }

    isTbai = () => {
        return this.is(this.tbaiData);
    }

    isTbaiTest = () => {
        return this.isTest(this.tbaiData);
    }

    wasTbai = () => {
        return this.was(this.tbaiDataHistory);
    }

    willBeTbai = () => {
        return this.willBe(this.tbaiDataHistory);
    }

    // Specific methods for LROE communication type

    setLroe(lroe) {
        if(lroe && !this.isLroe()) {
            this.lroeData = this.newEnterpriseData(EnterpriseDataNames.ICC_LROE);
            this._lroeDataHistory = this.lroeDataHistory || [];
            this._lroeDataHistory.push(this.lroeData);

            this._tbaiDataHistory = this.endHistory(this.tbaiDataHistory);
            this._siiDataHistory = this.endHistory(this.siiDataHistory);
            this._verifactuDataHistory = this.endHistory(this.verifactuDataHistory);
            this._noVerifactuDataHistory = this.endHistory(this.noVerifactuDataHistory);
            this._sifDataHistory = this.endHistory(this.sifDataHistory);            
        } else if(!lroe && this.isLroe()) {
            this.lroeData = undefined;
            this._lroeDataHistory = this.endHistory(this.lroeDataHistory);
            if(!this.hasCommunication()) {
                this.setSif(true);
            }
        }
    }

    setLroeDate(date) {
        if(this.isLroe()) {
            this.lroeData.startDate = date;
            this.lroeData.updated = true;
        }
    }

    getLroeDataHistory = () => {
        return this._lroeDataHistory || this.lroeDataHistory;
    }
   
    getLroeData = () => {
        return this.lroeData;
    }

    getFutureLroeData = () => {
        return this.getFutureData(this.getLroeDataHistory()); 
    }

    isLroe = () => {
        return this.is(this.lroeData);
    }

    isLroeTest = () => {
        return this.isTest(this.lroeData);
    }

    wasLroe = () => {
        return this.was(this.lroeDataHistory);
    }

    willBeLroe = () => {
        return this.willBe(this.lroeDataHistory);
    }

    // Specific methods for Verifactu communication type

    setVerifactu(verifactu) {
        if(verifactu && !this.isVerifactu()) {
            this.verifactuData = this.newEnterpriseData(EnterpriseDataNames.ICC_VERIFACTU);
            this._verifactuDataHistory = this.verifactuDataHistory || [];
            this._verifactuDataHistory.push(this.verifactuData);

            this._tbaiDataHistory = this.endHistory(this.tbaiDataHistory);
            this._siiDataHistory = this.endHistory(this.siiDataHistory);
            this._lroeDataHistory = this.endHistory(this.lroeDataHistory);
            this._noVerifactuDataHistory = this.endHistory(this.noVerifactuDataHistory);
            this._sifDataHistory = this.endHistory(this.sifDataHistory);            
        } else if(!verifactu && this.isVerifactu()) {
            this.verifactuData = undefined;
            this._verifactuDataHistory = this.endHistory(this.verifactuDataHistory);
            if(!this.hasCommunication()) {
                this.setSif(true);
            }
        }
    }

    setVerifactuDate(date) {
        let check = this.checkVerifactuDate(date)
        if(this.isVerifactu() && check.valid) {
            this.verifactuData.startDate = date;
            this.verifactuData.updated = true;
        }
        return check
    }

    checkVerifactuDate(date) {
        const selectedDate = date;
        const now = new Date();
        const nowDate = new Date(now.getFullYear(), now.getMonth(), now.getDate());
        const maxDate = isPersonaFisica(this.configuration.company.document) ? new Date(2026, 6, 1) : new Date(2026, 0, 1);

        if (now < maxDate && selectedDate > maxDate) {
            return {valid: false, message: isPersonaFisica(this.configuration.company.document)
                ? 'La fecha es posterior al 1 de Julio de 2026.'
                : 'La fecha es posterior al 1 de Enero de 2026.'};
        }
        if (selectedDate < nowDate) {
            return {valid: false, message: 'La fecha seleccionada no puede ser anterior a la fecha actual.'};
        }
        return { valid: true, message: '' };
    }
    
    getVerifactuDate() {
        const now = new Date();
        const date = new Date(2026, 0, 1);
        const nowDate = new Date(now.getFullYear(), now.getMonth(), now.getDate());
        return date > nowDate ? date : nowDate;
    }

    getVerifactuDataHistory = () => {
        return this._verifactuDataHistory || this.verifactuDataHistory;
    }
   
    getVerifactuData = () => {
        return this.verifactuData;
    }

    getFutureVerifactuData = () => {
        return this.getFutureData(this.getVerifactuDataHistory()); 
    }

    isVerifactu = () => {
        return this.is(this.verifactuData);
    }

    isVerifactuTest = () => {
        return this.isTest(this.verifactuData);
    }

    wasVerifactu = () => {
        return this.was(this.verifactuDataHistory);
    }
    
    willBeVerifactu = () => {
        return this.willBe(this.verifactuDataHistory);
    }

    // Specific methods for No Verifactu communication type

    setNoVerifactu(noVerifactu) {
        if(noVerifactu && !this.isNoVerifactu()) {
            this.noVerifactuData = this.newEnterpriseData(EnterpriseDataNames.ICC_NO_VERIFACTU);
            this._noVerifactuDataHistory = this.noVerifactuDataHistory || [];
            this._noVerifactuDataHistory.push(this.noVerifactuData);

            this._tbaiDataHistory = this.endHistory(this.tbaiDataHistory);
            this._siiDataHistory = this.endHistory(this.siiDataHistory);
            this._lroeDataHistory = this.endHistory(this.lroeDataHistory);
            this._verifactuDataHistory = this.endHistory(this.verifactuDataHistory);
            this._sifDataHistory = this.endHistory(this.sifDataHistory);            
        } else if(!noVerifactu && this.isNoVerifactu()) {
            this.noVerifactuData = undefined;
            this._noVerifactuDataHistory = this.endHistory(this.noVerifactuDataHistory);
            if(!this.hasCommunication()) {
                this.setSif(true);
            }
        }
    }
    
    setNoVerifactuDate(date) {
        if(this.isNoVerifactu()) {
            this.noVerifactuData.startDate = date;
            this.noVerifactuData.updated = true;
        }
    }

    getNoVerifactuDataHistory = () => {
        return this._noVerifactuDataHistory || this.noVerifactuDataHistory;
    }

    getNoVerifactuData = () => {
        return this.noVerifactuData;
    }

    getFutureNoVerifactuData = () => {
        return this.getFutureData(this.getNoVerifactuDataHistory()); 
    }

    isNoVerifactu = () => {
        return this.is(this.noVerifactuData);
    }

    wasNoVerifactu = () => {
        return this.was(this.noVerifactuDataHistory);
    }

    willBeNoVerifactu = () => {
        return this.willBe(this.noVerifactuDataHistory);
    }

    // Specific methods for SII communication type

    setSii(sii) {
        if(sii && !this.isSii()) {
            this.siiData = this.newEnterpriseData(EnterpriseDataNames.ICC_SII);
            this._siiDataHistory = this.siiDataHistory || [];
            this._siiDataHistory.push(this.siiData);

            this._tbaiDataHistory = this.endHistory(this.tbaiDataHistory);
            this._lroeDataHistory = this.endHistory(this.lroeDataHistory);
            this._verifactuDataHistory = this.endHistory(this.verifactuDataHistory);
            this._noVerifactuDataHistory = this.endHistory(this.noVerifactuDataHistory);
            this._sifDataHistory = this.endHistory(this.sifDataHistory);            
        } else if(!sii && this.isSii()) {
            this.siiData = undefined;
            this._siiDataHistory = this.endHistory(this.siiDataHistory);
            if(!this.hasCommunication()) {
                this.setSif(true);
            }
        }
    }

    setSiiDate(date) {
        if(this.isSii()) {
            this.siiData.startDate = date;
            this.siiData.updated = true;
        }
    }
    
    getSiiDataHistory = () => {
        return this._siiDataHistory || this.siiDataHistory;
    }
    
    getSiiData = () => {
        return this.siiData;
    }

    getFutureSiiData = () => {
        return this.getFutureData(this.getSiiDataHistory()); 
    }

    isSii = () => {
        return this.is(this.siiData);
    }

    isSiiTest = () => {
        return this.isTest(this.siiData);
    }

    wasSii = () => {
        return this.was(this.siiDataHistory);
    }

    willBeSii = () => {
        return this.willBe(this.siiDataHistory);
    }

    // Specific methods for SIF communication type

    setSif(sif) {
        if(sif && !this.isSif()) {
            this.sifData = this.newEnterpriseData(EnterpriseDataNames.ICC_FACTURAE);
            this._sifDataHistory = this.sifDataHistory || [];
            this._sifDataHistory.push(this.sifData);
            
            this._tbaiDataHistory = this.endHistory(this.tbaiDataHistory);
            this._siiDataHistory = this.endHistory(this.siiDataHistory);
            this._lroeDataHistory = this.endHistory(this.lroeDataHistory);
            this._verifactuDataHistory = this.endHistory(this.verifactuDataHistory);
            this._noVerifactuDataHistory = this.endHistory(this.noVerifactuDataHistory);            
        } else if(!sif && this.isSif()) {
            this.sifData = undefined;
            this._sifDataHistory = this.endHistory(this.sifDataHistory);
        }
    }
    
    setSifDate(date) {
        if(this.isSif()) {
            this.sifData.startDate = date;
            this.sifData.updated = true;
        }
    }
    
    getSifDataHistory = () => {
        return this._sifDataHistory || this.sifDataHistory;
    }
    
    getSifData = () => {
        return this.sifData;
    }

    isSif = () => {
        return this.is(this.sifData);
    }

    wasSif = () => {
        return this.was(this.sifDataHistory);
    }

    willBeSif = () => {
        return this.willBe(this.sifDataHistory);
    }

    // Specific methods for No SIF communication type
    
    setNoSif = (noSif) => {
        if(noSif && !this.isNoSif()) {
            this.noSifData = this.newEnterpriseData(EnterpriseDataNames.ICC_NO_SIF);
            this._noSifDataHistory = this.noSifDataHistory || [];
            this._noSifDataHistory.push(this.noSifData);

            this._tbaiDataHistory = this.endHistory(this.tbaiDataHistory);
            this._siiDataHistory = this.endHistory(this.siiDataHistory);
            this._lroeDataHistory = this.endHistory(this.lroeDataHistory);
            this._verifactuDataHistory = this.endHistory(this.verifactuDataHistory);
            this._noVerifactuDataHistory = this.endHistory(this.noVerifactuDataHistory);
            this._sifDataHistory = this.endHistory(this.sifDataHistory);            
        } else if(!noSif && this.isNoSif()) {
            this.noSifData = undefined;
            this._noSifDataHistory = this.endHistory(this.noSifDataHistory);
            if(!this.hasCommunication()) {
                this.setSif(true);
            }
        }
    }

    setNoSifDate(date) {
        if(this.isNoSif()) {
            this.noSifData.startDate = date;
            this.noSifData.updated = true;
        }
    }

    getNoSifDataHistory = () => {
        return this._noSifDataHistory || this.noSifDataHistory;
    }
    
    getNoSifData = () => {
        return this.noSifData;
    }

    isNoSif = () => {
        return this.is(this.noSifData);
    }

    wasNoSif = () => {
        return this.was(this.getNoSifDataHistory());
    }

    willBeNoSif = () => {
        return this.willBe(this.getNoSifDataHistory());
    }

    // ADMINISTRATION

    isAlava() {
        return this.getAdministration().isAlava();
    }

    isBizkaia() {
        return this.getAdministration().isBizkaia();
    }

    isGipuzkoa() {
        return this.getAdministration().isGipuzkoa();
    }

    isNavarra() {
        return this.getAdministration().isNavarra();
    }

    isCommonTerritory() {
        return this.getAdministration().isCommonTerritory();
    }

    isCanarias() {
        return this.getAdministration().isCanarias();
    }

    isUnknown() {
        return this.getAdministration().isUnknown();
    }

    // 

    hasCommunication = () => {
        return !this.isNoSif() && (this.isTbai() || this.isLroe() || this.isVerifactu() || this.isNoVerifactu() || this.isSii() || this.isSif()); 
    }

    // TO JSON

    toJSON = () => {
        return {
            administrationHistory: this.getAdministrationHistory(),
            administration: this.getAdministration().value,
            tbaiDataHistory: this.getTbaiDataHistory(),
            tbaiData: this.getTbaiData(),
            lroeDataHistory: this.getLroeDataHistory(),
            lroeData: this.getLroeData(),
            lroeRegistryDate: this.lroeRegistryDate,
            verifactuDataHistory: this.getVerifactuDataHistory(),
            verifactuData: this.getVerifactuData(),
            noVerifactuDataHistory: this.getNoVerifactuDataHistory(),
            noVerifactuData: this.getNoVerifactuData(),
            siiDataHistory: this.getSiiDataHistory(),
            siiData: this.getSiiData(),
            siiRegistryDate: this.siiRegistryDate,
            sifDataHistory: this.getSifDataHistory(),
            sifData: this.getSifData(),
            defaultCertificate: this.defaultCertificate
        };
    }

}