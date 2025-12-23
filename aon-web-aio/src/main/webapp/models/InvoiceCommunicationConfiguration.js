import { CONSTANT } from "../environments/environments";
import { AonDateUtils } from "../modules/utils/AonDateUtils";
import { isPersonaFisica } from "../services/documentUtils";
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

    getData = (history) => { // history: array of EnterpriseData
        return history.find(d => d.startDate && d.startDate <= this.getToday() && (!d.endDate || d.endDate > this.getToday()));
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
            this._administrationHistory.push(this.newEnterpriseData(EnterpriseDataNames.ICC_ADMINISTRATION, enterprise));
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

    newEnterpriseData = (name, enterprise, date) => {
        return new EnterpriseData({
            name,
            enterprise,
            expression: "",
            startDate: date || new Date(),
            updated: true
        });
    }

    endHistory = (history, date) => {
        let auxHistory = structuredClone(history);   
        auxHistory.forEach((element, index)=> {
            if(!element.endDate) {
                auxHistory[index].endDate = date || new Date();
                auxHistory[index].updated = true;
            }
        });
        return auxHistory;
    }

    endOtherHistories = (h, date) => {
        if(!h.includes(CONSTANT.TBAI)) {
            this._tbaiDataHistory = this.endHistory(this.tbaiDataHistory, date);
            this.tbaiData = this.getData(this.getTbaiDataHistory());
        }
        if(!h.includes(CONSTANT.LROE)) {
            this._lroeDataHistory = this.endHistory(this.lroeDataHistory, date);
            this.lroeData = this.getData(this.getLroeDataHistory());
        }
        if(!h.includes(CONSTANT.SII)) {
            this._siiDataHistory = this.endHistory(this.siiDataHistory, date);
            this.siiData = this.getData(this.getSiiDataHistory());
        }
        if(!h.includes(CONSTANT.VERIFACTU)) {
            this._verifactuDataHistory = this.endHistory(this.verifactuDataHistory, date);
            this.verifactuData = this.getData(this.getVerifactuDataHistory());
        }
        if(!h.includes(CONSTANT.NO_VERIFACTU)) {
            this._noVerifactuDataHistory = this.endHistory(this.noVerifactuDataHistory, date);
            this.noVerifactuData = this.getData(this.getNoVerifactuDataHistory());
        }
        if(!h.includes(CONSTANT.SIF)) {
            this._sifDataHistory = this.endHistory(this.sifDataHistory, date);
            this.sifData = this.getData(this.getSifDataHistory());
        }
        if(!h.includes(CONSTANT.NO_SIF)) {
            this._noSifDataHistory = this.endHistory(this.noSifDataHistory, date);
            this.noSifData = this.getData(this.getNoSifDataHistory());
        }
    }

    undefinedHistories = (h) => {
        if(!h.includes(CONSTANT.TBAI)) {
            this._tbaiDataHistory = undefined;
            this.tbaiData = this.getData(this.getTbaiDataHistory());
        }
        if(!h.includes(CONSTANT.LROE)) {
            this._lroeDataHistory = undefined;
            this.lroeData = this.getData(this.getLroeDataHistory());
        }
        if(!h.includes(CONSTANT.SII)) {
            this._siiDataHistory = undefined;
            this.siiData = this.getData(this.getSiiDataHistory());
        }
        if(!h.includes(CONSTANT.VERIFACTU)) {
            this._verifactuDataHistory = undefined;
            this.verifactuData = this.getData(this.getVerifactuDataHistory());
        }
        if(!h.includes(CONSTANT.NO_VERIFACTU)) {
            this._noVerifactuDataHistory = undefined;
            this.noVerifactuData = this.getData(this.getNoVerifactuDataHistory());
        }
        if(!h.includes(CONSTANT.SIF)) {
            this._sifDataHistory = undefined;
            this.sifData = this.getData(this.getSifDataHistory());
        }
        if(!h.includes(CONSTANT.NO_SIF)) {
            this._noSifDataHistory = undefined;
            this.noSifData = this.getData(this.getNoSifDataHistory());
        }
    }

    setHistoryStartDate = (history, date) => {
        let auxHistory = structuredClone(history);
        auxHistory.forEach((element, index) => {
            if(!element.endDate || element.endDate > date) {
                auxHistory[index].startDate = date;
                auxHistory[index].updated = true;
            }
        });
        return auxHistory;
    }

    getToday = () => {
        const now = new Date();
        return new Date(now.getFullYear(), now.getMonth(), now.getDate());
    }


    // Specific methods for TBAI communication type

    setTbai(tbai, enterprise) {
        if(tbai && !this.isTbai()) {
            this.tbaiData = this.newEnterpriseData(EnterpriseDataNames.ICC_TBAI, enterprise);
            this._tbaiDataHistory = this.tbaiDataHistory ? structuredClone(this.tbaiDataHistory) : [];
            this._tbaiDataHistory.push(this.tbaiData);
            this.setTbaiDate(this.getToday());
        } else if(!tbai && this.isTbai()) {
            this.tbaiData = undefined;
            this._tbaiDataHistory = this.endHistory(this.tbaiDataHistory);
            if(!this.hasCommunication()) {
                this.setSif(true, enterprise);
            }
        }
    }

    setTbaiDate(date) {
       let check = this.checkTbaiDate(date)
        if(this.isTbai() && check.valid) {
            this._tbaiDataHistory = this.setHistoryStartDate(this.getTbaiDataHistory(), date);
            if(date >= this.getToday().addDay(1)) {
                this.undefinedHistories([CONSTANT.TBAI, CONSTANT.SII]);
                this.tbaiData = undefined;
            } else {
                this.tbaiData.startDate = date;
                this.tbaiData.updated = true;
            }
            this.endOtherHistories([CONSTANT.TBAI, CONSTANT.SII], date);
        }
        return check;
    }

    checkTbaiDate(date) {
        const selectedDate = date;
        const nowDate = this.getToday();

        if (selectedDate > nowDate.addDay(1)) {
            return {valid: false, message: 'La fecha seleccionada no puede ser posterior a la fecha actual.'};
        }
        if (selectedDate < nowDate.addDay(-1)) {
            return {valid: false, message: 'La fecha seleccionada no puede ser anterior a la fecha actual.'};
        }
        return { valid: true, message: '' };    
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
        return this.was(this.getTbaiDataHistory());
    }

    willBeTbai = () => {
        return this.willBe(this.getTbaiDataHistory());
    }

    // Specific methods for LROE communication type

    setLroe(lroe, enterprise) {
        if(lroe && !this.isLroe()) {
            this.lroeData = this.newEnterpriseData(EnterpriseDataNames.ICC_LROE, enterprise);
            this._lroeDataHistory = this.lroeDataHistory ? structuredClone(this.lroeDataHistory) : [];
            this._lroeDataHistory.push(this.lroeData);
            this.setLroeDate(this.getToday());
        } else if(!lroe && this.isLroe()) {
            this.lroeData = undefined;
            this._lroeDataHistory = this.endHistory(this.lroeDataHistory);
            if(!this.hasCommunication()) {
                this.setSif(true, enterprise);
            }
        }
    }

    setLroeDate(date) {
       let check = this.checkLroeDate(date)
        if(this.isLroe() && check.valid) {
            this._lroeDataHistory = this.setHistoryStartDate(this.getLroeDataHistory(), date);
            if(date >= this.getToday().addDay(1)) {
                this.undefinedHistories([CONSTANT.LROE]);
                this.lroeData = undefined;
            } else {
                this.lroeData.startDate = date;
                this.lroeData.updated = true;
            }
            this.endOtherHistories([CONSTANT.LROE], date);
        }
        return check;
    }

    checkLroeDate(date) {
        const selectedDate = date;
        const nowDate = this.getToday();

        if (selectedDate > nowDate.addDay(1)) {
            return {valid: false, message: 'La fecha seleccionada no puede ser posterior a la fecha actual.'};
        }
        if (selectedDate < nowDate.addDay(-1)) {
            return {valid: false, message: 'La fecha seleccionada no puede ser anterior a la fecha actual.'};
        }
        return { valid: true, message: '' };    
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
        return this.was(this.getLroeDataHistory());
    }

    willBeLroe = () => {
        return this.willBe(this.getLroeDataHistory());
    }

    // Specific methods for Verifactu communication type

    setVerifactu(verifactu, enterprise, document) {
        if(verifactu && !this.isVerifactu()) {
            this.verifactuData = this.newEnterpriseData(EnterpriseDataNames.ICC_VERIFACTU, enterprise);
            this._verifactuDataHistory = this.verifactuDataHistory ? structuredClone(this.verifactuDataHistory) : [];
            this._verifactuDataHistory.push(this.verifactuData);
            this.setVerifactuDate(this.getToday(), document);
         } else if(!verifactu && this.isVerifactu()) {
            this.verifactuData = undefined;
            this._verifactuDataHistory = this.endHistory(this.verifactuDataHistory);
            if(!this.hasCommunication()) {
                this.setSif(true, enterprise);
            }
        }
    }

    setVerifactuDate(date, document) {
       let check = this.checkVerifactuDate(date, document)
        if(this.isVerifactu() && check.valid) {
            this._verifactuDataHistory = this.setHistoryStartDate(this.getVerifactuDataHistory(), date);
            if(date >= this.getToday().addDay(1)) {
                this.undefinedHistories([CONSTANT.VERIFACTU]);
                this.verifactuData = undefined;
            } else {
                this.verifactuData.startDate = date;
                this.verifactuData.updated = true;
                this.siiData = undefined;
                this.noVerifactuData = undefined;
            }
            this.endOtherHistories([CONSTANT.VERIFACTU], date);
        }
        return check;
    }

    checkVerifactuDate(date, document) {
        const selectedDate = date;
        const nowDate = this.getToday();
        const maxDate = isPersonaFisica(document) ? new Date(2026, 6, 1) : new Date(2026, 0, 1);

        if (nowDate < maxDate && selectedDate > maxDate) {
            return {valid: false, message: isPersonaFisica(document)
                ? 'La fecha es posterior al 1 de Julio de 2026.'
                : 'La fecha es posterior al 1 de Enero de 2026.'};
        }
        if (selectedDate < nowDate.addDay(-1)) {
            return {valid: false, message: 'La fecha seleccionada no puede ser anterior a la fecha actual.'};
        }
        return { valid: true, message: '' };
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
        return this.was(this.getVerifactuDataHistory());
    }
    
    willBeVerifactu = () => {
        return this.willBe(this.getVerifactuDataHistory());
    }

    // Specific methods for No Verifactu communication type

    setNoVerifactu(noVerifactu, enterprise, document) {
        if(noVerifactu && !this.isNoVerifactu()) {
            this.noVerifactuData = this.newEnterpriseData(EnterpriseDataNames.ICC_NO_VERIFACTU, enterprise);
            this._noVerifactuDataHistory = this.noVerifactuDataHistory ? structuredClone(this.noVerifactuDataHistory) : [];
            this._noVerifactuDataHistory.push(this.noVerifactuData);
            this.setNoVerifactuDate(this.getToday(), document);
        } else if(!noVerifactu && (this.isNoVerifactu() || this.willBeNoVerifactu())) {
            this.noVerifactuData = undefined;
            this._noVerifactuDataHistory = this.endHistory(this.noVerifactuDataHistory);
            this.undefinedHistories([CONSTANT.NO_VERIFACTU]);


            if(!this.hasCommunication()) {
                this.setSif(true, enterprise);
            }
        }
    }
    
    setNoVerifactuDate(date, document) {
        let check = this.checkNoVerifactuDate(date, document)
        if(this.isNoVerifactu() && check.valid) {
            this._noVerifactuDataHistory = this.setHistoryStartDate(this.getNoVerifactuDataHistory(), date);
            if(date >= this.getToday().addDay(1)) {
                this.undefinedHistories([CONSTANT.NO_VERIFACTU]);
                this.noVerifactuData = undefined;
            } else {
                this.noVerifactuData.startDate = date;
                this.noVerifactuData.updated = true;
            }
            this.endOtherHistories([CONSTANT.NO_VERIFACTU], date);
        }
        return check;
    }

    checkNoVerifactuDate(date, document) {
        const selectedDate = date;
        const now = new Date();
        const nowDate = new Date(now.getFullYear(), now.getMonth(), now.getDate());
        const maxDate = isPersonaFisica(document) ? new Date(2026, 6, 1) : new Date(2026, 0, 1);
        if (now < maxDate && selectedDate > maxDate) {
            return {valid: false, message: isPersonaFisica(document)
                ? 'La fecha es posterior al 1 de Julio de 2026.'
                : 'La fecha es posterior al 1 de Enero de 2026.'};
        }
        if (selectedDate < nowDate.addDay(-1)) {
            return {valid: false, message: 'La fecha seleccionada no puede ser anterior a la fecha actual.'};
        }

        return { valid: true, message: '' };
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
        return this.was(this.getNoVerifactuDataHistory());
    }

    willBeNoVerifactu = () => {
        return this.willBe(this.getNoVerifactuDataHistory());
    }

    // Specific methods for SII communication type

    setSii(sii, enterprise) {
        if(sii && !this.isSii()) {
            this.siiData = this.newEnterpriseData(EnterpriseDataNames.ICC_SII, enterprise);
            this._siiDataHistory = this.siiDataHistory ? structuredClone(this.siiDataHistory) : [];
            this._siiDataHistory.push(this.siiData);
            this.setSiiDate(this.getToday());
        } else if(!sii && this.isSii()) {
            this.siiData = undefined;
            this._siiDataHistory = this.endHistory(this.siiDataHistory);
            if(!this.hasCommunication()) {
                this.setSif(true, enterprise);
            }
        }
    }

    setSiiDate(date) {
       let check = this.checkSiiDate(date)
        if(this.isSii() && check.valid) {
            this._siiDataHistory = this.setHistoryStartDate(this.getSiiDataHistory(), date);
            if(date >= this.getToday().addDay(1)) {
                this.undefinedHistories([CONSTANT.SII, CONSTANT.TBAI]);
                this.siiData = undefined;
            } else {
                this.siiData.startDate = date;
                this.siiData.updated = true;
            }
            this.endOtherHistories([CONSTANT.SII, CONSTANT.TBAI], date);
        }
        return check;
    }

    checkSiiDate(date) {
        const selectedDate = date;
        const now = new Date();
        const nowDate = new Date(now.getFullYear(), now.getMonth(), now.getDate());
        if (selectedDate > nowDate.addDay(1)) {
            return {valid: false, message: 'La fecha seleccionada no puede ser posterior a la fecha actual.'};
        }
        if (selectedDate < nowDate.addDay(-1)) {
            return {valid: false, message: 'La fecha seleccionada no puede ser anterior a la fecha actual.'};
        }
        return { valid: true, message: '' };    
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

    setSif(sif, enterprise) {
        if(sif && !this.isSif()) {
            this.sifData = this.newEnterpriseData(EnterpriseDataNames.ICC_FACTURAE, enterprise);
            this._sifDataHistory = this.sifDataHistory ? structuredClone(this.sifDataHistory) : [];
            this._sifDataHistory.push(this.sifData);
            this.setSifDate(this.getToday());
        } else if(!sif && this.isSif()) {
            this.sifData = undefined;
            this._sifDataHistory = this.endHistory(this.sifDataHistory);
        }
    }
    
    setSifDate(date) {
        let check = this.checkSifDate(date)
        if(this.isSif() && check.valid) {
            this._sifDataHistory = this.setHistoryStartDate(this.getSifDataHistory(), date);
            if(date >= this.getToday().addDay(1)) {
                this.undefinedHistories([CONSTANT.SIF]);
                this.sifData = undefined;
            } else {
                this.sifData.startDate = date;
                this.sifData.updated = true;
            }
            this.endOtherHistories([CONSTANT.SIF], date);
        }
        return check;
    }

    checkSifDate(date) {
        const selectedDate = date;
        const now = new Date();
        const nowDate = new Date(now.getFullYear(), now.getMonth(), now.getDate());

        if (selectedDate > nowDate.addDay(1)) {
            return {valid: false, message: 'La fecha seleccionada no puede ser posterior a la fecha actual.'};
        }
        if (selectedDate < nowDate.addDay(-1)) {
            return {valid: false, message: 'La fecha seleccionada no puede ser anterior a la fecha actual.'};
        }
        return { valid: true, message: '' };    
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
    
    setNoSif = (noSif, enterprise) => {
        if(noSif && !this.isNoSif()) {
            this.noSifData = this.newEnterpriseData(EnterpriseDataNames.ICC_NO_SIF, enterprise);
            this._noSifDataHistory = this.noSifDataHistory ? structuredClone(this.noSifDataHistory) : [];
            this._noSifDataHistory.push(this.noSifData);
            this.setNoSifDate(this.getToday());
        } else if(!noSif && this.isNoSif()) {
            this.noSifData = undefined;
            this._noSifDataHistory = this.endHistory(this.noSifDataHistory);
            if(!this.hasCommunication()) {
                this.setSif(true, enterprise);
            }
        }
    }

    setNoSifDate(date) {
        let check = this.checkNoSifDate(date)
        if(this.isNoSif() && check.valid) {
            this._noSifDataHistory = this.setHistoryStartDate(this.getNoSifDataHistory(), date);
            if(date >= this.getToday().addDay(1)) {
                this.undefinedHistories([CONSTANT.NO_SIF]);
                this.noSifData = undefined;
            } else {
                this.noSifData.startDate = date;
                this.noSifData.updated = true;
            }
            this.endOtherHistories([CONSTANT.NO_SIF], date);
        }
        return check;
    }

    checkNoSifDate(date) {
        const selectedDate = date;
        const now = new Date();
        const nowDate = new Date(now.getFullYear(), now.getMonth(), now.getDate());

        if (selectedDate > nowDate) {
            return {valid: false, message: 'La fecha seleccionada no puede ser posterior a la fecha actual.'};
        }
        if (selectedDate < nowDate) {
            return {valid: false, message: 'La fecha seleccionada no puede ser anterior a la fecha actual.'};
        }
        return { valid: true, message: '' };
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