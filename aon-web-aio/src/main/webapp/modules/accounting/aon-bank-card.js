import { AonElement } from "../../components/AonElement.js";
import { getDomainUserRoles } from "../../services/companyService.js";
import { formatNumber } from "../../services/utils.js";
import { DomainUserRoles } from "../../models/DomainUserRoles.js";
import { CSS, MSG, TAG } from "../../environments/environments.js";
import { sortBy } from "../../services/utils.js";
import { getBanks } from "../../services/accountingService.js";
// import { AonDateUtils } from "../utils/AonDateUtils.js";

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

      content.appendChild(emptyMessage);
    } else {
      let maxLength = banks.length > 5 ? 5 : banks.length;
      let accumulatedBanks = 0;

      for (let index = 0; index < maxLength; index++) {
        const bank = banks[index];
        
        let row = this.createElement(TAG.DIV);
        row.className = "bank";

        let leftContent = this.createElement(TAG.DIV);
        leftContent.className = 'bank-detail';

        let description = this.createElement(TAG.SPAN);
        description.classList.add("bank-name");
        description.innerHTML = bank.alias;
        leftContent.appendChild(description);

        let date = this.createElement(TAG.SMALL);
        date.classList.add("aonBankCardDate");
        date.innerHTML = this.formatDateShort(bank.balanceDate);
        date.title = this.formatDate(bank.balanceDate);
        leftContent.appendChild(date);

        let rightContent = this.createElement(TAG.DIV);
        rightContent.className = 'bank-amount';
        rightContent.innerHTML = this.formatNumber(bank.balance);

        accumulatedBanks += bank.balance;

        row.appendChild(leftContent);
        row.appendChild(rightContent);

        content.appendChild(row);
      }

      // Create others row
      let total = banks.reduce((t, bank) => t + bank.balance, 0);
      if(banks.length > 5){
        let row = this.createElement(TAG.DIV);
        row.className = "bank";

        let leftContent = this.createElement(TAG.DIV);
        leftContent.className = 'bank-detail'

        let description = this.createElement(TAG.SPAN);
        description.classList.add("bank-name");
        description.innerHTML = "Otros";
        leftContent.appendChild(description);

        let rightContent = this.createElement(TAG.DIV);
        rightContent.className = 'bank-amount';
        rightContent.innerHTML = formatNumber(total - accumulatedBanks, 2, 2, "EUR");

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
    if (!parent){
      return;
    }
    while (parent.firstChild) {
      parent.removeChild(parent.firstChild);
    }
  }

  formatNumber(number){
    return number || number === 0 ? formatNumber(number, 2, 2, "EUR") : "No disponible";
  }

  getTotal(banks){
    let total = banks.reduce((t, bank) => t + bank.balance, 0);
    return total > 0 ? formatNumber(total, 2, 2, "EUR") : "No disponible";
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
      MSG.SUNDAY,
      MSG.MONDAY,
      MSG.TUESDAY,
      MSG.WEDNESDAY,
      MSG.THURSDAY,
      MSG.FRIDAY,
      MSG.SATURDAY
    ];
    return dayNames[date.getDay()];
  }
  
  getMonthName(date) {
    const monthNames = [
      MSG.JANUARY,
      MSG.FEBRUARY,
      MSG.MARCH,
      MSG.APRIL,
      MSG.MAY,
      MSG.JUNE,
      MSG.JULY,
      MSG.AUGUST,
      MSG.SEPTEMBER,
      MSG.OCTOBER,
      MSG.NOVEMBER,
      MSG.DECEMBER
    ];
    return monthNames[date.getMonth()];
  }

  padWithZero(num) {
    return num.toString().padStart(2, "0");
  }

}
window.customElements.define("aon-bank-card", AonBankCard);
