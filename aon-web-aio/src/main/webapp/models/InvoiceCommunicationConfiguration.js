import { CONSTANT } from "../environments/environments";
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
    tbaiInvoice;

    lroeDataHistory; // array of EnterpriseData
    _lroeDataHistory; // updated array of EnterpriseData
    lroeData; // EnterpriseData
    lroeRegistryDate; // Date
    lroeInvoice;

    verifactuDataHistory; // array of EnterpriseData
    _verifactuDataHistory; // updated array of EnterpriseData
    verifactuData; // EnterpriseData
    verifactuInvoice;

    noVerifactuDataHistory; // array of EnterpriseData
    _noVerifactuDataHistory; // updated array of EnterpriseData
    noVerifactuData; // EnterpriseData
    noVerifactuInvoice;

    siiDataHistory; // array of EnterpriseData
    _siiDataHistory; // updated array of EnterpriseData
    siiData; // EnterpriseData
    siiRegistryDate; // Date
    siiInvoice;

    sifDataHistory; // array of EnterpriseData
    _sifDataHistory; // updated array of EnterpriseData
    sifData; // EnterpriseData
    sifInvoice;

    noSifDataHistory; // array of EnterpriseData
    _noSifDataHistory; // updated array of EnterpriseData
    noSifData; // EnterpriseData

    defaultCertificate; // Integer

    constructor(data) {
        if (data) {
            this.administrationHistory = data.administrationHistory ? data.administrationHistory.map(ed => new EnterpriseData(ed)) : [];
            this.administration = data.administration ? new Administration(data.administration) : undefined;

            this.tbaiDataHistory = data.tbaiDataHistory ? data.tbaiDataHistory.map(ed => new EnterpriseData(ed)) : [];
            this.tbaiData = data.tbaiData ? new EnterpriseData(data.tbaiData) : undefined;
            this.tbaiInvoice = data.tbaiInvoice;

            this.lroeDataHistory = data.lroeDataHistory ? data.lroeDataHistory.map(ed => new EnterpriseData(ed)) : [];
            this.lroeData = data.lroeData ? new EnterpriseData(data.lroeData) : undefined;
            this.lroeRegistryDate = data.lroeRegistryDate ? new Date(data.lroeRegistryDate) : undefined;
            this.lroeInvoice = data.lroeInvoice;

            this.verifactuDataHistory = data.verifactuDataHistory ? data.verifactuDataHistory.map(ed => new EnterpriseData(ed)) : [];
            this.verifactuData = data.verifactuData ? new EnterpriseData(data.verifactuData) : undefined;
            this.verifactuInvoice = data.verifactuInvoice;

            this.noVerifactuDataHistory = data.noVerifactuDataHistory ? data.noVerifactuDataHistory.map(ed => new EnterpriseData(ed)) : [];
            this.noVerifactuData = data.noVerifactuData ? new EnterpriseData(data.noVerifactuData) : undefined;
            this.noVerifactuInvoice = data.noVerifactuInvoice;

            this.siiDataHistory = data.siiDataHistory ? data.siiDataHistory.map(ed => new EnterpriseData(ed)) : [];
            this.siiData = data.siiData ? new EnterpriseData(data.siiData) : undefined;
            this.siiRegistryDate = data.siiRegistryDate ? new Date(data.siiRegistryDate) : undefined;
            this.siiInvoice = data.siiInvoice;

            this.sifDataHistory = data.sifDataHistory ? data.sifDataHistory.map(ed => new EnterpriseData(ed)) : [];
            this.sifData = data.sifData ? new EnterpriseData(data.sifData) : undefined;
            this.sifInvoice = data.sifInvoice;

            this.noSifDataHistory = data.noSifDataHistory ? data.noSifDataHistory.map(ed => new EnterpriseData(ed)) : [];
            this.noSifData = data.noSifData ? new EnterpriseData(data.noSifData) : undefined;

            this.defaultCertificate = data.defaultCertificate;
        }
    }

    // Generic methods for EnterpriseData checks

    is = (data) => { // data: EnterpriseData
        return (data && (!data.endDate || data.endDate > new Date())) ? true : false;
    };

    isTest = (data) => { // data: EnterpriseData
        return this.is(data) && data.expression && data.expression.toLowerCase().includes("test");
    }

    was = (history, year) => { // history: array of EnterpriseData
        if (year) {
            return history && history.length > 0 && history.some(d =>
                d.startDate && d.startDate.getFullYear() >= year &&
                ((d.endDate && d.endDate.getFullYear() <= year) || !d.endDate));
        }
        return history && history.length > 0 && history.some(d =>
            d.startDate && d.startDate < new Date());
    }

    willBe = (history) => { // history: array of EnterpriseData
        return history && history.length > 0 && history.some(d =>
            d.startDate && d.startDate > new Date() && !d.removed);
    }

    getData = (history) => { // history: array of EnterpriseData
        return history.find(d => d.startDate && d.startDate <= new Date() && (!d.endDate || d.endDate > new Date()));
    }

    getFutureData = (history) => { // history: array of EnterpriseData
        if (this.willBe(history)) {
            return history.find(d => d.startDate && d.startDate > new Date() && !d.removed);
        }
        return null;
    }

    // Specific methods for Administration

    getAdministrationHistory = () => {
        return this._administrationHistory || this.administrationHistory;
    }

    getAdministrationData = () => {
        return this.getData(this.getAdministrationHistory());
    }


    getAdministration = () => {
        return this._administration || this.administration;
    }

    setAdministration = (administration, enterprise) => {
        if (administration instanceof Administration && administration.value != this.administration.value) {
            this._administration = administration;
            this._administrationHistory = this.endHistory(this.administrationHistory);
            let administrationData = this.newEnterpriseData(EnterpriseDataNames.ICC_ADMINISTRATION, enterprise);
            administrationData.expression = administration.value;
            this._administrationHistory.push(administrationData);
        } else if (administration instanceof Administration && administration.value === this.administration.value) {
            this._administration = undefined;
            this._administrationHistory = undefined;
        }
    }

    updateHistoryByAdministration = (administration) => {
        if (administration instanceof Administration) {
            if (administration.isAlava()) {

            } else if (administration.isBizkaia()) {

            } else if (administration.isGipuzkoa()) {

            } else if (administration.isNavarra()) {

            } else if (administration.isCommonTerritory()) {

            } else if (administration.isCanarias()) {

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
        auxHistory.forEach((element, index) => {
            if (!element.endDate || element.endDate > (date || new Date())) {
                auxHistory[index].endDate = date || new Date();
                auxHistory[index].updated = true;
            }
        });
        return auxHistory;
    }

    deleteHistory = (history) => {
        let auxHistory = structuredClone(history);
        auxHistory.forEach((element, index) => {
            if (!element.endDate || element.endDate > new Date()) {
                auxHistory[index].removed = true;
            }
        });
        return auxHistory;
    }

    endOtherHistories = (h, date) => {
        if (!h.includes(CONSTANT.TBAI)) {
            this._tbaiDataHistory = this.willBeTbai() ? this.deleteHistory(this.tbaiDataHistory) : this.endHistory(this.tbaiDataHistory, date);
            this.tbaiData = this.getData(this.getTbaiDataHistory());
        }
        if (!h.includes(CONSTANT.LROE)) {
            this._lroeDataHistory = this.willBeLroe() ? this.deleteHistory(this.lroeDataHistory) : this.endHistory(this.lroeDataHistory, date);
            this.lroeData = this.getData(this.getLroeDataHistory());
        }
        if (!h.includes(CONSTANT.SII)) {
            this._siiDataHistory = this.willBeSii() 
                ? this.deleteHistory(this.siiDataHistory) 
                : this.endHistory(this.siiInvoice ? this.getSiiDataHistory() : this.siiDataHistory, date);
            this.siiData = this.getData(this.getSiiDataHistory());
        }
        if (!h.includes(CONSTANT.VERIFACTU)) {
            this._verifactuDataHistory = this.willBeVerifactu() 
                ? this.deleteHistory(this.verifactuDataHistory) 
                : this.endHistory(this.verifactuInvoice ? this.getVerifactuDataHistory() : this.verifactuDataHistory, date);
            this.verifactuData = this.getData(this.getVerifactuDataHistory());
        }
        if (!h.includes(CONSTANT.NO_VERIFACTU)) {
            this._noVerifactuDataHistory = this.willBeNoVerifactu() 
                ? this.deleteHistory(this.noVerifactuDataHistory) 
                : this.endHistory(this.noVerifactuInvoice ? this.getNoVerifactuDataHistory() : this.noVerifactuDataHistory, date);
            this.noVerifactuData = this.getData(this.getNoVerifactuDataHistory());
        }
        if (!h.includes(CONSTANT.SIF)) {
            this._sifDataHistory = this.willBeSif() ? this.deleteHistory(this.sifDataHistory) : this.endHistory(this.sifDataHistory, date);
            this.sifData = this.getData(this.getSifDataHistory());
        }
        // if(!h.includes(CONSTANT.NO_SIF)) {
        //     this._noSifDataHistory = this.willBeNoSif() ? this.deleteHistory(this.noSifDataHistory) : this.endHistory(this.noSifDataHistory, date);
        //     // this.noSifData = this.getData(this.getNoSifDataHistory());
        // }
    }

    undefinedHistories = (h) => {
        if (!h.includes(CONSTANT.TBAI)) {
            this._tbaiDataHistory = undefined;
            this.tbaiData = this.getData(this.getTbaiDataHistory());
        }
        if (!h.includes(CONSTANT.LROE)) {
            this._lroeDataHistory = undefined;
            this.lroeData = this.getData(this.getLroeDataHistory());
        }
        if (!h.includes(CONSTANT.SII)) {
            this._siiDataHistory = undefined;
            this.siiData = this.getData(this.getSiiDataHistory());
        }
        if (!h.includes(CONSTANT.VERIFACTU)) {
            this._verifactuDataHistory = undefined;
            this.verifactuData = this.getData(this.getVerifactuDataHistory());
        }
        if (!h.includes(CONSTANT.NO_VERIFACTU)) {
            this._noVerifactuDataHistory = undefined;
            this.noVerifactuData = this.getData(this.getNoVerifactuDataHistory());
        }
        if (!h.includes(CONSTANT.SIF)) {
            this._sifDataHistory = undefined;
            this.sifData = this.getData(this.getSifDataHistory());
        }
        // if(!h.includes(CONSTANT.NO_SIF)) {
        //     this._noSifDataHistory = undefined;
        //     this.noSifData = this.getData(this.getNoSifDataHistory());
        // }
    }

    setHistoryStartDate = (history, date) => {
        let auxHistory = structuredClone(history);
        auxHistory.forEach((element, index) => {
            if (!element.endDate || element.endDate > date) {
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
        if (tbai && !this.isTbai()) {
            this.tbaiData = this.newEnterpriseData(EnterpriseDataNames.ICC_TBAI, enterprise);
            this._tbaiDataHistory = this.tbaiDataHistory ? structuredClone(this.tbaiDataHistory) : [];
            this._tbaiDataHistory.push(this.tbaiData);
            this.setTbaiDate(this.getToday(), enterprise);
        } else if (!tbai && this.isTbai()) {
            this.tbaiData = undefined;
            this._tbaiDataHistory = this.endHistory(this.tbaiDataHistory);
            this.undefinedHistories([CONSTANT.TBAI, CONSTANT.SII]);
            if (!this.hasCommunication()) {
                this.setSif(true, enterprise);
            }
        }
    }

    setTbaiDate(date, enterprise) {
        date = new Date(date.getFullYear(), date.getMonth(), date.getDate(), 12);
        let check = this.checkTbaiDate(date)
        if (this.isTbai() && check.valid) {
            this._tbaiDataHistory = this.setHistoryStartDate(this.getTbaiDataHistory(), date);
            if (date >= this.getToday().addDay(1)) {
                this.undefinedHistories([CONSTANT.TBAI, CONSTANT.SII]);
                this.tbaiData = undefined;
                if (this.isNoSif()) {
                    this.setSif(true, enterprise);
                }
            } else {
                this.tbaiData.startDate = date;
                this.tbaiData.updated = true;
            }
            this.endOtherHistories([CONSTANT.TBAI, CONSTANT.SII], date);
            if (!this.hasCommunication()) {
                this.setSif(true, enterprise, CONSTANT.LROE);
            }
        }
        return check;
    }

    checkTbaiDate(date) {
        const selectedDate = date;
        const nowDate = this.getToday();

        if (selectedDate > nowDate.addDay(1)) {
            return { valid: false, message: 'La fecha seleccionada no puede ser posterior a la fecha actual.' };
        }
        if (selectedDate < nowDate.addDay(-1)) {
            return { valid: false, message: 'La fecha seleccionada no puede ser anterior a la fecha actual.' };
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
        if (lroe && !this.isLroe()) {
            this.lroeData = this.newEnterpriseData(EnterpriseDataNames.ICC_LROE, enterprise);
            this._lroeDataHistory = this.lroeDataHistory ? structuredClone(this.lroeDataHistory) : [];
            this._lroeDataHistory.push(this.lroeData);
            this.setLroeDate(this.getToday(), enterprise);
        } else if (!lroe && this.isLroe()) {
            this.lroeData = undefined;
            this._lroeDataHistory = this.endHistory(this.lroeDataHistory);
            this.undefinedHistories([CONSTANT.LROE]);
            if (!this.hasCommunication() && !this.isNoSif()) {
                this.setSif(true, enterprise);
            }
        }
    }

    setLroeDate(date, enterprise) {
        date = new Date(date.getFullYear(), date.getMonth(), date.getDate(), 12);
        let check = this.checkLroeDate(date)
        if (this.isLroe() && check.valid) {
            this._lroeDataHistory = this.setHistoryStartDate(this.getLroeDataHistory(), date);
            if (date >= this.getToday().addDay(1)) {
                this.undefinedHistories([CONSTANT.LROE]);
                this.lroeData = undefined;
                if (this.isNoSif()) {
                    this.setSif(true, enterprise);
                }
            } else {
                this.lroeData.startDate = date;
                this.lroeData.updated = true;
            }
            this.endOtherHistories([CONSTANT.LROE], date);
            if (!this.hasCommunication()) {
                this.setSif(true, enterprise, CONSTANT.LROE);
            }
        }
        return check;
    }

    checkLroeDate(date) {
        const selectedDate = date;
        const nowDate = this.getToday();

        if (selectedDate > nowDate.addDay(1)) {
            return { valid: false, message: 'La fecha seleccionada no puede ser posterior a la fecha actual.' };
        }
        if (selectedDate < nowDate.addDay(-1)) {
            return { valid: false, message: 'La fecha seleccionada no puede ser anterior a la fecha actual.' };
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

    setVerifactu(verifactu, enterprise) {
        if (verifactu && !this.isVerifactu()) {
            this.verifactuData = this.newEnterpriseData(EnterpriseDataNames.ICC_VERIFACTU, enterprise);
            this._verifactuDataHistory = this.verifactuDataHistory ? structuredClone(this.verifactuDataHistory) : [];
            this._verifactuDataHistory.push(this.verifactuData);
            this.setVerifactuDate(this.getDefaultVerifactuDate(), enterprise);
        } else if (!verifactu && this.isVerifactu()) {
            this.verifactuData = undefined;
            this._verifactuDataHistory = this.endHistory(this.verifactuDataHistory);
            this.undefinedHistories([CONSTANT.VERIFACTU]);
            if (!this.hasCommunication()) {
                this.setSif(true, enterprise);
            }
        } else if (!verifactu && this.willBeVerifactu()) {
            this.verifactuData = undefined;
            this._verifactuDataHistory = this.deleteHistory(this.verifactuDataHistory);
            this.undefinedHistories([CONSTANT.VERIFACTU]);
            if (!this.hasCommunication()) {
                this.setSif(true, enterprise);
            }
        }
    }

    setVerifactuDate(date, enterprise) {
        date = new Date(date.getFullYear(), date.getMonth(), date.getDate(), 12);
        let check = this.checkVerifactuDate(date);
        if ((this.isVerifactu() || this.willBeVerifactu()) && check.valid) {
            this._verifactuDataHistory = this.setHistoryStartDate(this.getVerifactuDataHistory(), date);
            if (date >= this.getToday().addDay(1)) {
                let arr = [CONSTANT.VERIFACTU];
                if ((this.isSii() && this.siiInvoice) || (this.wasSii(new Date().getFullYear()) && this.siiInvoice)) {
                    arr.push(CONSTANT.SII);
                }
                if ((this.isNoVerifactu() && this.noVerifactuInvoice) || (this.wasNoVerifactu(new Date().getFullYear()) && this.noVerifactuInvoice)) {
                    arr.push(CONSTANT.NO_VERIFACTU);
                }
                this.undefinedHistories(arr);
                this.verifactuData = undefined;
                if (!this.hasCommunication()) {
                    this.setSif(true, enterprise, CONSTANT.VERIFACTU);
                    this._sifDataHistory = this.endHistory(this.getSifDataHistory(), date);
                }
            } else {
                this.verifactuData.startDate = date;
                this.verifactuData.updated = true;
            }
            this.endOtherHistories([CONSTANT.VERIFACTU], date);
            if (!this.hasCommunication()) {
                this.setSif(true, enterprise, CONSTANT.VERIFACTU);
            }
        }
        return check;
    }

    checkVerifactuDate(date) {
        const selectedDate = date;
        const now = new Date();
        const nowDate = new Date(now.getFullYear(), now.getMonth(), now.getDate());
        const maxDate = new Date(2025, 0, 1, 23, 59, 59);
        if (now < maxDate && selectedDate > maxDate) {
            return { valid: false, message: 'La fecha es posterior al 1 de Enero de 2026.' };
        }
        if (selectedDate < nowDate.addDay(-1)) {
            return { valid: false, message: 'La fecha seleccionada no puede ser anterior a la fecha actual.' };
        }
        if ((selectedDate.getMonth() !== 0 || selectedDate.getDate() !== 1) && selectedDate > nowDate.addDay(1)) {
            return { valid: false, message: 'La fecha seleccionada no puede ser posterior a la fecha actual si no es 1 de Enero del próximo año.' };
        }
        return { valid: true, message: '' };
    }

    getDefaultVerifactuDate() {
        const now = new Date();
        const nowDate = new Date(now.getFullYear(), now.getMonth(), now.getDate(), 12);
        let arr = [CONSTANT.VERIFACTU];
        if ((this.isSii() && this.siiInvoice) || (this.wasSii(new Date().getFullYear()) && this.siiInvoice)) {
            arr.push(CONSTANT.SII);
        }
        if ((this.isNoVerifactu() && this.noVerifactuInvoice) || (this.wasNoVerifactu(new Date().getFullYear()) && this.noVerifactuInvoice)) {
            arr.push(CONSTANT.NO_VERIFACTU);
        }
        this.undefinedHistories(arr);
        if (this.isNoVerifactu() || this.wasNoVerifactu(now.getFullYear()) || (this.isSii() && this.siiInvoice) || (this.wasSii(now.getFullYear()) && this.siiInvoice)) {
            let date = new Date(now.getFullYear() + 1, 0, 1, 12);
            return date;
        }
        let date = new Date(2026, 0, 1, 12);
        if (now >= date) {
            return nowDate;
        }
        return date;
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

    wasVerifactu = (year) => {
        return this.was(this.getVerifactuDataHistory(), year);
    }

    willBeVerifactu = () => {
        return this.willBe(this.getVerifactuDataHistory());
    }

    // Specific methods for No Verifactu communication type

    setNoVerifactu(noVerifactu, enterprise) {
        if (noVerifactu && !this.isNoVerifactu()) {
            this.noVerifactuData = this.newEnterpriseData(EnterpriseDataNames.ICC_NO_VERIFACTU, enterprise);
            this._noVerifactuDataHistory = this.noVerifactuDataHistory ? structuredClone(this.noVerifactuDataHistory) : [];
            this._noVerifactuDataHistory.push(this.noVerifactuData);
            this.setNoVerifactuDate(this.getDefaultNoVerifactuDate(), enterprise);
        } else if (!noVerifactu && (this.isNoVerifactu())) {
            this.noVerifactuData = undefined;
            this._noVerifactuDataHistory = this.endHistory(this.noVerifactuDataHistory);
            this.undefinedHistories([CONSTANT.NO_VERIFACTU]);
            if (!this.hasCommunication()) {
                this.setSif(true, enterprise);
            }
        } else if (!noVerifactu && this.willBeNoVerifactu()) {
            this.noVerifactuData = undefined;
            this._noVerifactuDataHistory = this.deleteHistory(this.noVerifactuDataHistory);
            this.undefinedHistories([CONSTANT.NO_VERIFACTU]);
            if (!this.hasCommunication()) {
                this.setSif(true, enterprise);
            }
        }
    }

    setNoVerifactuDate(date, enterprise) {
        date = new Date(date.getFullYear(), date.getMonth(), date.getDate(), 12);
        let check = this.checkNoVerifactuDate(date)
        if ((this.isNoVerifactu() || this.willBeNoVerifactu()) && check.valid) {
            this._noVerifactuDataHistory = this.setHistoryStartDate(this.getNoVerifactuDataHistory(), date);
            if (date >= this.getToday().addDay(1)) {
                let arr = [CONSTANT.NO_VERIFACTU];
                if ((this.isSii() && this.siiInvoice) || (this.wasSii(new Date().getFullYear()) && this.siiInvoice)) {
                    arr.push(CONSTANT.SII);
                }
                if ((this.isVerifactu() && this.verifactuInvoice) || (this.wasVerifactu(new Date().getFullYear()) && this.verifactuInvoice)) {
                    arr.push(CONSTANT.VERIFACTU);
                }
                this.undefinedHistories(arr);
                this.noVerifactuData = undefined;
                if (!this.hasCommunication()) {
                    this.setSif(true, enterprise, CONSTANT.NO_VERIFACTU);
                    this._sifDataHistory = this.endHistory(this.getSifDataHistory(), date);
                }
            } else {
                this.noVerifactuData.startDate = date;
                this.noVerifactuData.updated = true;
            }
            let h = this.willBeNoVerifactu() && this.isSif() ? [CONSTANT.NO_VERIFACTU, CONSTANT.SIF, CONSTANT.NO_SIF] : [CONSTANT.NO_VERIFACTU];
            this.endOtherHistories(h, date);
            if (!this.hasCommunication()) {
                this.setSif(true, enterprise, CONSTANT.NO_VERIFACTU);
            }
        }
        return check;
    }

    checkNoVerifactuDate(date) {
        const selectedDate = date;
        const now = new Date();
        const nowDate = new Date(now.getFullYear(), now.getMonth(), now.getDate());
        const maxDate = new Date(2026, 0, 1, 23, 59, 59);
        if (now < maxDate && selectedDate > maxDate) {
            return { valid: false, message: 'La fecha es posterior al 1 de Enero de 2026.' };
        }
        if (selectedDate < nowDate.addDay(-1)) {
            return { valid: false, message: 'La fecha seleccionada no puede ser anterior a la fecha actual.' };
        }
        if ((selectedDate.getMonth() !== 0 || selectedDate.getDate() !== 1) && selectedDate > nowDate.addDay(1)) {
            return { valid: false, message: 'La fecha seleccionada no puede ser posterior a la fecha actual si no es 1 de Enero del próximo año.' };
        }
        return { valid: true, message: '' };
    }

    getDefaultNoVerifactuDate() {
        const now = new Date();
        const nowDate = new Date(now.getFullYear(), now.getMonth(), now.getDate(), 12);
        let arr = [CONSTANT.NO_VERIFACTU];
        if ((this.isSii() && this.siiInvoice) || (this.wasSii(new Date().getFullYear()) && this.siiInvoice)) {
            arr.push(CONSTANT.SII);
        }
        if ((this.isVerifactu() && this.verifactuInvoice) || (this.wasVerifactu(new Date().getFullYear()) && this.verifactuInvoice)) {
            arr.push(CONSTANT.VERIFACTU);
        }
        this.undefinedHistories(arr);
        if ((this.isVerifactu() && this.verifactuInvoice) || (this.wasVerifactu(now.getFullYear()) && this.verifactuInvoice) || (this.isSii() && this.siiInvoice) || (this.wasSii(now.getFullYear()) && this.siiInvoice)) {
            let date = new Date(now.getFullYear() + 1, 0, 1, 12);
            return date;
        }

        let date = new Date(2026, 0, 1, 12);
        if (now >= date) {
            return nowDate;
        }
        return date;
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

    wasNoVerifactu = (year) => {
        return this.was(this.getNoVerifactuDataHistory(), year);
    }

    willBeNoVerifactu = () => {
        return this.willBe(this.getNoVerifactuDataHistory());
    }

    // Specific methods for SII communication type

    setSii(sii, enterprise) {
        if (sii && !this.isSii()) {
            this.siiData = this.newEnterpriseData(EnterpriseDataNames.ICC_SII, enterprise);
            this._siiDataHistory = this.siiDataHistory ? structuredClone(this.siiDataHistory) : [];
            this._siiDataHistory.push(this.siiData);
            this.setSiiDate(this.getToday(), enterprise);
        } else if (!sii && this.isSii()) {
            this.siiData = undefined;
            this._siiDataHistory = this.endHistory(this.siiDataHistory);
            this.undefinedHistories([CONSTANT.SII, CONSTANT.TBAI]);
            if (!this.hasCommunication() && !this.isNoSif()) {
                this.setSif(true, enterprise);
            }
        }
    }

    setSiiDate(date, enterprise) {
        date = new Date(date.getFullYear(), date.getMonth(), date.getDate(), 12);
        let check = this.checkSiiDate(date)
        if (this.isSii() && check.valid) {
            this._siiDataHistory = this.setHistoryStartDate(this.getSiiDataHistory(), date);
            if (date >= this.getToday().addDay(1)) {
                this.undefinedHistories([CONSTANT.SII, CONSTANT.TBAI]);
                this.siiData = undefined;
                if (this.isNoSif()) {
                    this.setSif(true, enterprise);
                }
            } else {
                this.siiData.startDate = date;
                this.siiData.updated = true;
            }
            this.endOtherHistories([CONSTANT.SII, CONSTANT.TBAI], date);
            if (!this.hasCommunication()) {
                this.setSif(true, enterprise, CONSTANT.SII);
            }
        }
        return check;
    }

    checkSiiDate(date) {
        const selectedDate = date;
        const now = new Date();
        const nowDate = new Date(now.getFullYear(), now.getMonth(), now.getDate());
        if (selectedDate > nowDate.addDay(1)) {
            return { valid: false, message: 'La fecha seleccionada no puede ser posterior a la fecha actual.' };
        }
        // if (selectedDate < nowDate.addDay(-1)) {
        //     return {valid: false, message: 'La fecha seleccionada no puede ser anterior a la fecha actual.'};
        // }
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

    wasSii = (year) => {
        return this.was(this.getSiiDataHistory(), year);
    }

    willBeSii = () => {
        return this.willBe(this.getSiiDataHistory());
    }

    // Specific methods for SIF communication type

    setSif(sif, enterprise, communicationType) {
        if (sif && !this.isSif()) {
            this.sifData = this.newEnterpriseData(EnterpriseDataNames.ICC_SIF, enterprise);
            this._sifDataHistory = this.sifDataHistory ? structuredClone(this.sifDataHistory) : [];
            this._sifDataHistory.push(this.sifData);
            this.setSifDate(this.getToday(), communicationType);
        } else if (!sif && this.isSif()) {
            this.sifData = undefined;
            this._sifDataHistory = this.endHistory(this.sifDataHistory);
            this.undefinedHistories([CONSTANT.SIF]);
        }
    }

    setSifDate(date, communicationType) {
        date = new Date(date.getFullYear(), date.getMonth(), date.getDate(), 12);
        let check = this.checkSifDate(date)
        if (this.isSif() && check.valid) {
            this._sifDataHistory = this.setHistoryStartDate(this.getSifDataHistory(), date);
            if (date >= this.getToday().addDay(1)) {
                this.undefinedHistories([CONSTANT.SIF]);
                this.sifData = undefined;
            } else {
                this.sifData.startDate = date;
                this.sifData.updated = true;
            }
            let h = [CONSTANT.SIF];
            if (communicationType) {
                h.push(communicationType);
            }
            this.endOtherHistories(h, date);
        }
        return check;
    }

    checkSifDate(date) {
        const selectedDate = date;
        const now = new Date();
        const nowDate = new Date(now.getFullYear(), now.getMonth(), now.getDate());

        if (selectedDate > nowDate.addDay(1)) {
            return { valid: false, message: 'La fecha seleccionada no puede ser posterior a la fecha actual.' };
        }
        if (selectedDate < nowDate.addDay(-1)) {
            return { valid: false, message: 'La fecha seleccionada no puede ser anterior a la fecha actual.' };
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
        if (noSif && !this.isNoSif()) {
            this.noSifData = this.newEnterpriseData(EnterpriseDataNames.ICC_NO_SIF, enterprise);
            this._noSifDataHistory = this.noSifDataHistory ? structuredClone(this.noSifDataHistory) : [];
            this._noSifDataHistory.push(this.noSifData);
            this.setNoSifDate(this.getToday());
        } else if (!noSif) {
            this.noSifData = undefined;
            this._noSifDataHistory = this.endHistory(this.noSifDataHistory);
            this.undefinedHistories([CONSTANT.NO_SIF]);

            if (this.wasSii(new Date().getFullYear())) {
                this.setSii(true, enterprise, CONSTANT.NO_SIF);
            } else if (!this.isSii() && this.wasVerifactu(new Date().getFullYear())) {
                this.setVerifactu(true, enterprise, CONSTANT.NO_SIF);
            } else if (!this.isSii() && this.wasNoVerifactu(new Date().getFullYear())) {
                this.setNoVerifactu(true, enterprise, CONSTANT.NO_SIF);
            } else if (this.isBizkaia()) {
                this.setLroe(true, enterprise, CONSTANT.NO_SIF);
            } else if (this.isAlava() || this.isGipuzkoa()) {
                this.setTbai(true, enterprise, CONSTANT.NO_SIF);
            } else if (this.isCommonTerritory() || this.isCanarias()) {
                this.setNoVerifactu(true, enterprise);
            } else if (!this.hasCommunication()) {
                this.setSif(true, enterprise, CONSTANT.NO_SIF);
            }
        }
    }

    setNoSifDate(date) {
        date = new Date(date.getFullYear(), date.getMonth(), date.getDate(), 12);
        let check = this.checkNoSifDate(date);
        if (this.isNoSif() && check.valid) {
            this._noSifDataHistory = this.setHistoryStartDate(this.getNoSifDataHistory(), date);
            if (date >= this.getToday().addDay(1)) {
                this.undefinedHistories([CONSTANT.NO_SIF]);
                this.noSifData = undefined;
            } else {
                this.noSifData.startDate = date;
                this.noSifData.updated = true;
            }
            this.endOtherHistories([CONSTANT.NO_SIF, CONSTANT.SII, CONSTANT.LROE], date);
        }
        return check;
    }

    checkNoSifDate(date) {
        const selectedDate = date;
        const now = new Date();
        const nowDate = new Date(now.getFullYear(), now.getMonth(), now.getDate());

        if (selectedDate > nowDate.addDay(1)) {
            return { valid: false, message: 'La fecha seleccionada no puede ser posterior a la fecha actual.' };
        }
        if (selectedDate < nowDate.addDay(-1)) {
            return { valid: false, message: 'La fecha seleccionada no puede ser anterior a la fecha actual.' };
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

    hasCommunicationByType = (type) => {
        if (type === "emitida") return !this.isNoSif() && (this.isTbai() || this.isLroe() || this.isVerifactu() || this.isNoVerifactu() || this.isSii() || this.isSif());
        else return this.isLroe() || this.isSii();
    }

    hasCommunication = () => {
        return !this.isNoSif() && (this.isTbai() || this.isLroe() || this.isVerifactu() || this.isNoVerifactu() || this.isSii() || this.isSif());
    }

    willBeCommunication = () => {
        return this.willBeTbai() || this.willBeLroe() || this.willBeVerifactu() || this.willBeNoVerifactu() || this.willBeSii() || this.willBeSif();
    }

    // TO JSON

    toJSON = () => {
        return {
            administrationHistory: this.getAdministrationHistory(),
            administration: this.getAdministration().value,
            tbaiDataHistory: this.getTbaiDataHistory(),
            tbaiData: this.getTbaiData(),
            tbaiInvoice: this.tbaiInvoice,
            lroeDataHistory: this.getLroeDataHistory(),
            lroeData: this.getLroeData(),
            lroeRegistryDate: this.lroeRegistryDate,
            lroeInvoice: this.lroeInvoice,
            verifactuDataHistory: this.getVerifactuDataHistory(),
            verifactuData: this.getVerifactuData(),
            verifactuInvoice: this.verifactuInvoice,
            noVerifactuDataHistory: this.getNoVerifactuDataHistory(),
            noVerifactuData: this.getNoVerifactuData(),
            noVerifactuInvoice: this.noVerifactuInvoice,
            siiDataHistory: this.getSiiDataHistory(),
            siiData: this.getSiiData(),
            siiRegistryDate: this.siiRegistryDate,
            siiInvoice: this.siiInvoice,
            sifDataHistory: this.getSifDataHistory(),
            sifData: this.getSifData(),
            sifInvoice: this.sifInvoice,
            noSifData: this.getNoSifData(),
            noSifDataHistory: this.getNoSifDataHistory(),
            defaultCertificate: this.defaultCertificate
        };
    }

}