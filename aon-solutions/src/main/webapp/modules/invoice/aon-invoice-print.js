import {AonElement} from '../../components/AonElement.js';

import '../../components/aon-date.js';
import '../../components/aon-input.js';
import '../../components/aon-slider.js';
import '../../components/aon-viewer.js';
import '../../components/aon-switch.js';
import '../../components/aon-card.js';

import { MSG } from "../../environments/environments.js";

export class AonInvoicePrint extends AonElement {

  DATA;
  DATA_CARD;
  FILE;

  get id() {
    return this.getAttribute('id');
  }

  set id(id) {
    this.setAttribute('id', id);
  }

  constructor () {
    super();
  }

  connectedCallback () {
    this.initialize();
    this.innerHTML = `
      <div style="display:flex;">
        <div id="${this.DATA}" class="aonSubContent" style="width:100%">
          <aon-card id="${this.DATA_CARD}" title="${MSG.FILE_DATA}"> </aon-card>
        </div>
        <div id="${this.FILE}" class="aonSubContent">

        </div>
      </div>
    `;

    this.build();
  }

  initialize() {
    this.id = this.id || 'aonInvoicePrintConfiguration';
    this.DATA = this.id + 'Data';
    this.DATA_CARD = this.DATA + 'Card';
    this.FILE = this.id + 'File';
  }

  build() {
    let fileDiv = this.getElement(this.FILE);
    fileDiv.style.display = 'block';
    fileDiv.style.width = '50%';

    let json = btoa(JSON.stringify({}));
    let url = '/ms/api/download_invoice_pdf_ak?json=' + json;
    fileDiv.innerHTML = `<aon-viewer type="application/pdf" file="${url}" width="${fileDiv.offsetWidth}"></aon-viewer>`;

    let dataDiv = this.getElement(this.DATA);
    dataDiv.style.width = '50%';

		if(localStorage.getItem('aon_solutions') === undefined || localStorage.getItem('aon_solutions') === null) {
      let offset1 = fileDiv.getBoundingClientRect();
      fileDiv.style.height = `calc(100vh - ${offset1.top + 2}px)`;

      let offset2 = dataDiv.getBoundingClientRect();
  		dataDiv.style.height = `calc(100vh - ${offset2.top + 2}px)`;
    }

    this.buildData();
  }

  reloadFile() {
    let config =  {
      header: this.getElement('aonInvoicePrintConfigurationHeader').value,
      footer: this.getElement('aonInvoicePrintConfigurationFooter').value,
      detailed: this.getElement('aonInvoicePrintConfigurationDetailed').checked,
      adjust: this.getElement('aonInvoicePrintConfigurationAdjust').checked
    }
    let fileDiv = this.getElement(this.FILE);
    let json = btoa(JSON.stringify(config));
    let url = '/ms/api/download_invoice_pdf_ak?json=' + json;
    fileDiv.innerHTML = `<aon-viewer type="application/pdf" file="${url}" width="${fileDiv.offsetWidth}"></aon-viewer>`;

  }

  buildData() {
    let card = this.getElement(this.DATA_CARD);
    card.setContentHTML('');
		let table = document.createElement('table');
		table.style.width = '100%';
		card.setContent(table);

    let tr = document.createElement('tr');
    table.appendChild(tr);

    let tdHeader= document.createElement('td');
    tdHeader.setAttribute('colspan', '2');
		tdHeader.innerHTML = `<aon-slider id="aonInvoicePrintConfigurationHeader" title="${MSG.HEADER}" min="0" max="200"></aon-slider>`;
		tr.appendChild(tdHeader);
		let header = document.getElementById('aonInvoicePrintConfigurationHeader');
    header.addEventListener('change', () => this.reloadFile());

    let tr2 = document.createElement('tr');
    table.appendChild(tr2);

    let tdFooter = document.createElement('td');
    tdFooter.setAttribute('colspan', '2');
    tdFooter.innerHTML = `<aon-slider id="aonInvoicePrintConfigurationFooter" title="${MSG.FOOTER}" min="0" max="200"></aon-slider>`;
    tr2.appendChild(tdFooter);
    let footer = document.getElementById('aonInvoicePrintConfigurationFooter');
    footer.addEventListener('change', () => this.reloadFile());

    let tr3 = document.createElement('tr');
    table.appendChild(tr3);

    let tdAdjust= document.createElement('td');
    tdAdjust.setAttribute('colspan', '1');
    tdAdjust.innerHTML = `<aon-switch id="aonInvoicePrintConfigurationAdjust" title="${MSG.BACKGROUND_ADJUST}"></aon-switch>`;
    tr3.appendChild(tdAdjust);
    let adjust = document.getElementById('aonInvoicePrintConfigurationAdjust');
    adjust.addEventListener('change', () => this.reloadFile());

    let tdBackground= document.createElement('td');
    tdBackground.setAttribute('colspan', '1');
    tdBackground.innerHTML = `<aon-icon-button id="aonInvoicePrintConfigurationBackground" icon="add_photo_alternate"></aon-icon-button>`;
    tr3.appendChild(tdBackground);
    let background = document.getElementById('aonInvoicePrintConfigurationBackground');

    let tr4 = document.createElement('tr');
    table.appendChild(tr4);

    let tdDetailed= document.createElement('td');
    tdDetailed.setAttribute('colspan', '2');
    tdDetailed.innerHTML = `<aon-switch id="aonInvoicePrintConfigurationDetailed" title="${MSG.DETAILED}"></aon-switch>`;
    tr4.appendChild(tdDetailed);
    let detailed = document.getElementById('aonInvoicePrintConfigurationDetailed');
    detailed.addEventListener('change', () => this.reloadFile());
  }

}

window.customElements.define('aon-invoice-print',  AonInvoicePrint);
