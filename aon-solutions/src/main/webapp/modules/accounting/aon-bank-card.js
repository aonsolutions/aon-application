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

    let maxLength = banks.length > 4 ? 4 : banks.length;

    for (let index = 0; index < maxLength; index++) {
      const bank = banks[index];
      
      let row = this.createElement(TAG.DIV);
      row.className = CSS.AON_FLEX;
      row.style.justifyContent = "space-between";
      row.style.width = "100%";
      row.style.borderBottom = "1px solid #ddd";
      row.style.padding = "1rem 0";

      let leftContent = this.createElement(TAG.DIV);
      leftContent.className = CSS.AON_FLEX;
      leftContent.style.alignContent = "center";
      leftContent.style.flexDirection = "column";

      let description = this.createElement(TAG.SPAN);
      description.style.fontSize = "1.1rem";
      description.style.color = "rgb(0, 36, 105)";
      description.style.fontWeight = "500";
      description.innerHTML = bank.alias;
      leftContent.appendChild(description);

      let date = this.createElement(TAG.SPAN);
      date.style.color = "rgb(120, 120, 133)";
      date.style.fontSize = ".8rem";
      date.innerHTML = this.formatDate(bank.balanceDate);
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

      row.appendChild(leftContent);
      row.appendChild(rightContent);

      content.appendChild(row);
    }

    const bankTotalDiv = this.getElement("bankTotalDiv");
    bankTotalDiv.className = CSS.AON_CARD_TOTAL;
    bankTotalDiv.innerHTML = this.getTotal(banks);
  }

  removeAllChildNodes(parent) {
    while (parent.firstChild) {
        parent.removeChild(parent.firstChild);
    }
  }

  formatDate(date){
    let dateFormat = date ? date : new Date(); 
    return AonDateUtils.getDayMonthOrFull(dateFormat);
  }

  formatNumber(number){
    let numberFormat = number && number > 0 ? number : 9999999.99; 
    return formatNumber(numberFormat, 2, "EUR");
  }

  getTotal(banks){
    let total = banks.reduce((t, bank) => t + bank.balance, 0);
    total = total > 0 ? total : 9999999.99;
    return formatNumber(total, 2, "EUR");
  }

}
window.customElements.define("aon-bank-card", AonBankCard);
