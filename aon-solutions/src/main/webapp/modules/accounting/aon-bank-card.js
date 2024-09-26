import { AonElement } from "../../components/AonElement.js";
import { getDomainUserRoles } from "../../services/companyService.js";
import { formatNumber } from "../../services/utils.js";
import { DomainUserRoles } from "../../models/DomainUserRoles.js";
import { CSS } from "../../environments/environments.js";
import { sortBy } from "../../services/utils.js";
import { TAG } from "../../environments/environments.js";
import { getBanks } from "../../services/accountingService.js";
import { AonDateUtils } from "../utils/AonDateUtils.js";

export class AonBankCard extends AonElement {
  
  dur;
  companyRegistry;
  BANKS = [];
  
  constructor(companyR) {
    super();
    this.companyRegistry = companyR;
  }

  connectedCallback() {
    this.initialize();
    getDomainUserRoles({ reload: true }).then((r) => {
      this.dur = new DomainUserRoles(r);
      this.build();
    });
  }

  initialize() {
    this.id = "aonBankCard";
  }

  getDur() {
    return this.dur;
  }

  build() {
    this.paintView();
    this.buildToolbar();
  }

  paintView() {
    this.style.display = "flex";
    this.style.flexDirection = "column";
    this.style.justifyContent = "space-between";
    this.style.height = "100%";

    let cardContent = this.createElement(TAG.DIV);
    cardContent.className = CSS.AON_FLEX_COLUMN;
    cardContent.id = "bankCardTable";
    this.appendChild(cardContent);

    let totalDiv = this.createElement(TAG.DIV);
    totalDiv.id = "bankTotalDiv";
    this.appendChild(totalDiv);
  }

  buildToolbar() {
    this.getCompanyBanks().then(companyBanks => {
      this.getTable(companyBanks);
    });
  }

  async getCompanyBanks() {
    if(!this.BANKS.length){
      try {
        const banks = await getBanks({id: this.companyRegistry});

        if(banks){
          this.BANKS = sortBy(banks,'alias')
            .filter((bank) => bank.active == true);
        }
        
        return this.BANKS;

      } catch (error) {
        console.error(error);
        this.showError(error);
      }
    } else {
      return this.BANKS;
    }
  }

  getTable(banks) {
    let content = this.getElement("bankCardTable");
    this.removeAllChildNodes(content);

    if(banks && banks.length === 0){
      let emptyMessage = this.createElement(TAG.DIV);
      emptyMessage.innerHTML = "No existen bancos";
      emptyMessage.style.fontWeight = "bold";

      content.style.height = "100%";
      content.appendChild(emptyMessage);

      let bankCard = this.getElement("bankCard");
      bankCard.style.display = "none";
      
    } else {

      let maxLength = banks.length > 5 ? 5 : banks.length;
      let accumulatedBanks = 0;

      for (let index = 0; index < maxLength; index++) {
        const bank = banks[index];
        
        let row = this.createElement(TAG.DIV);
        row.className = CSS.AON_FLEX;
        row.style.justifyContent = "space-between";
        row.style.width = "100%";
        row.style.borderBottom = "1px solid #ddd";
        row.style.padding = "0.5rem 0";

        let leftContent = this.createElement(TAG.DIV);
        leftContent.className = CSS.AON_FLEX;
        leftContent.style.alignContent = "center";
        leftContent.style.flexDirection = "column";

        let description = this.createElement(TAG.SPAN);
        description.style.fontSize = "1rem";
        description.style.color = "var(--aonAccounting)";
        description.style.fontWeight = "500";
        description.innerHTML = bank.alias;
        leftContent.appendChild(description);

        let date = this.createElement(TAG.SPAN);
        date.classList.add("aonBankCardDate");
        date.style.fontSize = ".7rem";
        date.innerHTML = this.formatDateShort(bank.balanceDate);
        date.title = this.formatDate(bank.balanceDate);
        leftContent.appendChild(date);

        let rightContent = this.createElement(TAG.DIV);
        rightContent.className = CSS.AON_FLEX;
        rightContent.style.alignItems = "center";
        rightContent.style.gap = "1rem";

        let amount = this.createElement(TAG.SPAN);
        amount.style.fontWeight = "bold";
        amount.style.minWidth = "5rem";
        amount.style.textAlign = "right";
        amount.innerHTML = this.formatNumber(bank.balance);
        rightContent.appendChild(amount);

        accumulatedBanks += bank.balance;

        row.appendChild(leftContent);
        row.appendChild(rightContent);

        content.appendChild(row);
      }

      // Create others row
      let total = banks.reduce((t, bank) => t + bank.balance, 0);
      if(banks.length > 5){
        let row = this.createElement(TAG.DIV);
        row.className = CSS.AON_FLEX;
        row.style.justifyContent = "space-between";
        row.style.width = "100%";
        row.style.borderBottom = "1px solid #ddd";
        row.style.padding = "0.4rem 0";

        let leftContent = this.createElement(TAG.DIV);
        leftContent.className = CSS.AON_FLEX;
        leftContent.style.alignItems = "center";
        leftContent.style.gap = "1rem";

        let description = this.createElement(TAG.SPAN);
        description.style.fontSize = "1rem";
        description.style.color = "var(--aonAccounting)";
        description.style.fontWeight = "500";
        description.innerHTML = "Otros";
        leftContent.appendChild(description);

        let rightContent = this.createElement(TAG.DIV);
        rightContent.className = CSS.AON_FLEX;
        rightContent.style.alignItems = "center";
        rightContent.style.gap = "1rem";

        let amount = this.createElement(TAG.SPAN);
        amount.style.fontWeight = "bold";
        amount.style.minWidth = "5rem";
        amount.style.textAlign = "right";
        amount.innerHTML = formatNumber(total - accumulatedBanks, 2, "EUR");
        rightContent.appendChild(amount);

        row.appendChild(leftContent);
        row.appendChild(rightContent);

        content.appendChild(row);
      }

      const bankTotalDiv = this.getElement("bankTotalDiv");
      bankTotalDiv.className = CSS.AON_CARD_TOTAL;
      bankTotalDiv.classList.add(CSS.AON_BANK_CARD_TOTAL);
      bankTotalDiv.innerHTML = this.getTotal(banks);
    }
  }

  removeAllChildNodes(parent) {
    while (parent.firstChild) {
        parent.removeChild(parent.firstChild);
    }
  }

  formatNumber(number){
    return number || number === 0 ? formatNumber(number, 2, "EUR") : "No disponible";
  }

  getTotal(banks){
    let total = banks.reduce((t, bank) => t + bank.balance, 0);
    return total > 0 ? formatNumber(total, 2, "EUR") : "No disponible";
  }

  formatDate(inputDate) {
    inputDate = inputDate ? new Date(inputDate) : new Date();

    const today = new Date();
    today.setHours(0, 0, 0, 0);
    const yesterday = new Date(today);
    yesterday.setDate(today.getDate() - 1);
    yesterday.setHours(0, 0, 0, 0);
    const sixDaysAgo = new Date(today);
    sixDaysAgo.setDate(today.getDate() - 7);
    sixDaysAgo.setHours(0, 0, 0, 0);
  
    if (this.isSameDay(inputDate, today)) {
      return `Actualizado hoy. ${this.getDayName(inputDate)} a las ${this.formatTime(inputDate)}`;
    } else if (this.isSameDay(inputDate, yesterday)) {
      return `Actualizado ayer, ${this.getDayName(inputDate)}`;
    } else if (inputDate > sixDaysAgo) {
      const dayDiff = Math.floor((today - inputDate) / (1000 * 60 * 60 * 24));
      return `Actualizado el ${this.getDayName(inputDate)} (Hace ${dayDiff} días)`
    } else {
      const dayDiff = Math.floor((today - inputDate) / (1000 * 60 * 60 * 24));
      const formattedDate = `${this.padWithZero(inputDate.getDate())} ${this.getMonthName(inputDate)}`;
      return `Actualizado el ${formattedDate} (Hace ${dayDiff} días)`;
    }
  }

  formatDateShort(inputDate) {
    inputDate = inputDate ? new Date(inputDate) : new Date();

    const today = new Date();
    today.setHours(0, 0, 0, 0);
    const yesterday = new Date(today);
    yesterday.setDate(today.getDate() - 1);
    yesterday.setHours(0, 0, 0, 0);
    const sixDaysAgo = new Date(today);
    sixDaysAgo.setDate(today.getDate() - 7);
    sixDaysAgo.setHours(0, 0, 0, 0);
  
    if (this.isSameDay(inputDate, today)) {
      return `Act. hoy. ${this.getDayName(inputDate)} a las ${this.formatTime(inputDate)}`;
    } else if (this.isSameDay(inputDate, yesterday)) {
      return `Act. ayer, ${this.getDayName(inputDate)}`;
    } else if (inputDate > sixDaysAgo) {
      const dayDiff = Math.floor((today - inputDate) / (1000 * 60 * 60 * 24));
      return `Act. el ${this.getDayName(inputDate)} (Hace ${dayDiff} días)`
    } else {
      const dayDiff = Math.floor((today - inputDate) / (1000 * 60 * 60 * 24));
      const formattedDate = `${this.padWithZero(inputDate.getDate())} ${this.getMonthName(inputDate)}`;
      return `Act. el ${formattedDate} (Hace ${dayDiff} días)`;
    }
  }
  
  isSameDay(date1, date2) {
    return (
      date1.getDate() === date2.getDate() &&
      date1.getMonth() === date2.getMonth() &&
      date1.getFullYear() === date2.getFullYear()
    );
  }
  
  formatTime(date) {
    const hours = this.padWithZero(date.getHours());
    const minutes = this.padWithZero(date.getMinutes());
    return `${hours}:${minutes}`;
  }
  
  getDayName(date) {
    const dayNames = [
      "Domingo",
      "Lunes",
      "Martes",
      "Miércoles",
      "Jueves",
      "Viernes",
      "Sábado",
    ];
    return dayNames[date.getDay()];
  }
  
   getMonthName(date) {
      const monthNames = [
        "Enero",
        "Febrero",
        "Marzo",
        "Abril",
        "Mayo",
        "Junio",
        "Julio",
        "Agosto",
        "Septiembre",
        "Octubre",
        "Noviembre",
        "Diciembre"
      ];
      return monthNames[date.getMonth()];
    }
  
  padWithZero(num) {
    return num.toString().padStart(2, "0");
  }

}
window.customElements.define("aon-bank-card", AonBankCard);
