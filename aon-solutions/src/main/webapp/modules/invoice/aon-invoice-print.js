import {AonElement} from '../../components/AonElement.js';

import '../../components/aon-date.js';
import '../../components/aon-input.js';
import '../../components/aon-slider.js';
import '../../components/aon-viewer.js';
import '../../components/aon-switch.js';
import '../../components/aon-card.js';

import { EVENT, MSG } from "../../environments/environments.js";
import { getPrintInvoiceConfiguration, savePrintInvoiceConfiguration } from '../../services/invoiceService.js';
import { getReader } from '../../services/utils.js';

import * as LS from '../../services/localStorageService.js';

export class AonInvoicePrint extends AonElement {

  DATA;
  DATA_CARD;
  FILE;
  INPUTFILE;

  printConfiguration;

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
    this.innerHTML = this.isMobile() ? 
        `
          <input id="${this.INPUTFILE}" style='display:none;' type='file' name='file'>
          <div style="display:block;">
            <div id="${this.DATA}" class="aonSubContent" style="width:100%">
              <aon-card id="${this.DATA_CARD}" title="${MSG.FILE_DATA}"> </aon-card>
            </div>
            <div id="${this.FILE}" class="aonSubContent">

            </div>
          </div>
        `
      : `
      <input id="${this.INPUTFILE}" style='display:none;' type='file' name='file'>
      <div style="display:flex;">
        <div id="${this.DATA}" class="aonSubContent" style="width:100%">
          <aon-card id="${this.DATA_CARD}" title="${MSG.FILE_DATA}"> </aon-card>
        </div>
        <div id="${this.FILE}" class="aonSubContent">

        </div>
      </div>
    `;
    getPrintInvoiceConfiguration().then(r => {
      this.printConfiguration = r;
      this.build();
    })
  }

  initialize() {
    this.id = this.id || 'aonInvoicePrintConfiguration';
    this.DATA = this.id + 'Data';
    this.DATA_CARD = this.DATA + 'Card';
    this.FILE = this.id + 'File';
    this.INPUTFILE = this.id + 'InputFile';
  }

  build() {
    let fileDiv = this.getElement(this.FILE);
    fileDiv.style.display = 'block';
    fileDiv.style.width = this.isMobile() ? '100%' : '50%';

    this.reloadFile();

    let dataDiv = this.getElement(this.DATA);
    dataDiv.style.width = this.isMobile() ? '100%' : '50%';

		if(localStorage.getItem('aon_solutions') === undefined || localStorage.getItem('aon_solutions') === null) {
      let offset1 = fileDiv.getBoundingClientRect();
      fileDiv.style.height = `calc(100vh - ${offset1.top + 2}px)`;

      let offset2 = dataDiv.getBoundingClientRect();
  		dataDiv.style.height = `calc(100vh - ${offset2.top + 2}px)`;
    }
    this.buildData();
  }

  save() {
    savePrintInvoiceConfiguration(this.printConfiguration);
  }

  reloadFile() {
    let objeto = {
      domain_id: LS.getDomainId(),
      domain_name: LS.getDomainName(),
      login: LS.getDomainLogin()
    };
    let fileDiv = this.getElement(this.FILE);
    let json = btoa(JSON.stringify(objeto));
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
    header.setValue(this.printConfiguration.header);
    header.addEventListener('change', () => {
      this.printConfiguration.header = header.value;
      this.save();
      this.reloadFile();
    });

    let tr2 = document.createElement('tr');
    table.appendChild(tr2);

    let tdFooter = document.createElement('td');
    tdFooter.setAttribute('colspan', '2');
    tdFooter.innerHTML = `<aon-slider id="aonInvoicePrintConfigurationFooter" title="${MSG.FOOTER}" min="0" max="200"></aon-slider>`;
    tr2.appendChild(tdFooter);
    let footer = document.getElementById('aonInvoicePrintConfigurationFooter');
    footer.setValue(this.printConfiguration.footer);
    footer.addEventListener('change', () => {
      this.printConfiguration.footer = footer.value;
      this.save();
      this.reloadFile();
    });

    let tr4 = document.createElement('tr');
    table.appendChild(tr4);

    let tdBackground= document.createElement('td');
    tdBackground.setAttribute('colspan', '2');
    tdBackground.innerHTML = `
      <span style="font-size: 14px;opacity: 0.87;"> Subir Fondo </span>
      <aon-icon-button id="aonInvoicePrintConfigurationBackground" icon="add_photo_alternate"></aon-icon-button>
      <aon-icon-button id="aonInvoicePrintConfigurationBackgroundRemove" icon="clear"></aon-icon-button>
    `;
    tr4.appendChild(tdBackground);
    let background = document.getElementById('aonInvoicePrintConfigurationBackground');

    let backgroundRemove = document.getElementById('aonInvoicePrintConfigurationBackgroundRemove');
    backgroundRemove.addEventListener('click', ()=> {
      this.printConfiguration.backgroundRemove = true;
      this.save();
      this.reloadFile();
    });

    let input = this.getElement(this.INPUTFILE);
    input.addEventListener('change', () => {
      getReader(input.files[0]).then(f => {
        this.printConfiguration.backgroundRemove = false;
        this.printConfiguration.background = f;
        this.save();
        this.reloadFile();
      });
    });

    background.addEventListener(EVENT.CLICK, () => {
      input.click();
    })


    let tr3 = document.createElement('tr');
    table.appendChild(tr3);

    let tdAdjust= document.createElement('td');
    tdAdjust.setAttribute('colspan', '2');
    tdAdjust.innerHTML = `<aon-switch id="aonInvoicePrintConfigurationAdjust" title="${MSG.BACKGROUND_ADJUST}"></aon-switch>`;
    tr3.appendChild(tdAdjust);
    let adjust = document.getElementById('aonInvoicePrintConfigurationAdjust');
    adjust.checked = this.printConfiguration.adjust;
    adjust.setWidth('135px');
    adjust.addEventListener('change', () => {
      this.printConfiguration.adjust = adjust.checked;
      this.save();
      this.reloadFile()
    });

    let tr5 = document.createElement('tr');
    table.appendChild(tr5);

    let tdDetailed= document.createElement('td');
    tdDetailed.setAttribute('colspan', '2');
    tdDetailed.innerHTML = `<aon-switch id="aonInvoicePrintConfigurationDetailed" title="${MSG.DETAILED}"></aon-switch>`;
    tr5.appendChild(tdDetailed);
    let detailed = document.getElementById('aonInvoicePrintConfigurationDetailed');
    detailed.checked = this.printConfiguration.detailed;
    detailed.setWidth('135px');
    detailed.addEventListener('change', () => {
      this.printConfiguration.detailed = detailed.checked;
      this.save();
      this.reloadFile()
    });
  }

}

window.customElements.define('aon-invoice-print',  AonInvoicePrint);
