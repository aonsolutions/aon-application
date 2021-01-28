import {AonElement} from '../../components/AonElement.js';
import {deleteFile, getCategories, getScopes, getTags, updateFile} from '../../services/service.js';

import '../../components/aon-date.js';
import '../../components/aon-input.js';
import '../../components/aon-slider.js';
import '../../components/aon-viewer.js';
import '../../components/aon-switch.js';
import '../../components/aon-card.js';

import * as CONSTANT from "../../environments/constants.js";
import * as MSG from "../../environments/msg.js";
import * as MATERIAL_ICONS from "../../environments/materialIcons.js";

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
    this.id = this.id || 'aonInvoicePrintConfiguration';
    this.DATA = this.id + 'Data';
    this.DATA_CARD = this.DATA + 'Card';
    this.FILE = this.id + 'File';
  }

  connectedCallback () {
    this.innerHTML = `
      <div style="display:flex;">
        <div id="${this.DATA}" class="aonSubContent" style="width:100%">
          <aon-card id="${this.DATA_CARD}" title="${MSG.AON_MSG_FILE_DATA}"> </aon-card>
        </div>
        <div id="${this.FILE}" class="aonSubContent">

        </div>
      </div>
    `;

    this.build();
  }

  build() {
    let fileDiv = this.getElement(this.FILE);
    fileDiv.style.display = 'block';
    fileDiv.style.width = '50%';
		//fileDiv.innerHTML = `<aon-viewer type="${this.document.file.type}" file="${this.document.file.url}" width="${fileDiv.offsetWidth}"></aon-viewer>`;

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
		tdHeader.innerHTML = `<aon-slider id="header" title="${MSG.AON_MSG_HEADER}" min="0" max="200"></aon-slider>`;
		tr.appendChild(tdHeader);
		let header = document.getElementById('header');

    let tr2 = document.createElement('tr');
    table.appendChild(tr2);

    let tdFooter = document.createElement('td');
    tdFooter.setAttribute('colspan', '2');
    tdFooter.innerHTML = `<aon-slider id="footer" title="${MSG.AON_MSG_FOOTER}" min="0" max="200"></aon-slider>`;
    tr2.appendChild(tdFooter);
    let footer = document.getElementById('footer');

    let tr3 = document.createElement('tr');
    table.appendChild(tr3);

    let tdAdjust= document.createElement('td');
    tdAdjust.setAttribute('colspan', '1');
    tdAdjust.innerHTML = `<aon-switch id="adjust" title="${MSG.AON_MSG_BACKGROUND_ADJUST}"></aon-switch>`;
    tr3.appendChild(tdAdjust);
    let adjust = document.getElementById('adjust');

    let tdBackground= document.createElement('td');
    tdBackground.setAttribute('colspan', '1');
    tdBackground.innerHTML = `<aon-icon-button id="background" icon="add_photo_alternate"></aon-icon-button>`;
    tr3.appendChild(tdBackground);
    let background = document.getElementById('background');

    let tr4 = document.createElement('tr');
    table.appendChild(tr4);

    let tdDetailed= document.createElement('td');
    tdDetailed.setAttribute('colspan', '2');
    tdDetailed.innerHTML = `<aon-switch id="detailed" title="${MSG.AON_MSG_DETAILED}"></aon-switch>`;
    tr4.appendChild(tdDetailed);
    let detailed = document.getElementById('detailed');
  }

}

window.customElements.define('aon-invoice-print',  AonInvoicePrint);
