import {AonElement} from '../../components/AonElement.js';

import '../../components/aon-date.js';
import '../../components/aon-input.js';
import '../../components/aon-slider.js';
import '../../components/aon-viewer.js';
import '../../components/aon-switch.js';
import '../../components/aon-card.js';

import { EVENT, MSG, TAG } from "../../environments/environments.js";
import { getPrintInvoiceConfiguration, savePrintInvoiceConfiguration } from '../../services/invoiceService.js';
import { getReader } from '../../services/utils.js';

import * as LS from '../../services/localStorageService.js';
import { AonUpload } from '../../components/aon-upload.js';
import { AonViewer } from '../../components/aon-viewer.js';
import { getAttach } from '../../services/fileService.js';
import { AonSelect } from '../../components/aon-select.js';
import { Language } from '../../models/Language.js';
import { Theme } from './Themes.js';
import { AonIconButton } from '../../components/aon-icon-button.js';

export class AonInvoicePrint extends AonElement {

  DATA;
  DATA_CARD;
  FILE;
  VIEWER;
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
    this.getApplication().setDragAndDrop(false);
    this.innerHTML = this.isMobile() ? 
        `
          <div style="display:block;">
            <div id="${this.DATA}" class="aonSubContent" style="width:100%">
              <aon-card id="${this.DATA_CARD}" title="PERSONALIZAR FACTURA"> </aon-card>
            </div>
            <div id="${this.FILE}" class="aonSubContent">

            </div>
          </div>
        `
      : `
      <div style="display:flex; height:100%;">
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


  disconnectedCallback() {
    this.getApplication().setDragAndDrop(true);
  }

  initialize() {
    this.id = this.id || 'aonInvoicePrintConfiguration';
    this.DATA = this.id + 'Data';
    this.DATA_CARD = this.DATA + 'Card';
    this.FILE = this.id + 'File';
    this.VIEWER = this.id + 'Viewer';
  }

  build() {
    let fileDiv = this.getElement(this.FILE);
    fileDiv.style.display = 'block';
    fileDiv.style.width = this.isMobile() ? '100%' : '50%';
    fileDiv.style.height = '100%';
    
    let dataDiv = this.getElement(this.DATA);
    dataDiv.style.width = this.isMobile() ? '100%' : '50%';
    dataDiv.style.height = '100%';
		if(localStorage.getItem('aon_solutions') === undefined || localStorage.getItem('aon_solutions') === null) {
      let offset1 = fileDiv.getBoundingClientRect();
      fileDiv.style.height = `calc(100vh - ${offset1.top + 2}px)`;

      let offset2 = dataDiv.getBoundingClientRect();
  		dataDiv.style.height = `calc(100vh - ${offset2.top + 2}px)`;
    }

    this.buildData();
    this.reloadFile();
  }

  save() {
    savePrintInvoiceConfiguration(this.printConfiguration).then(r => this.reloadFile());
  }

  reloadFile() {
    let viewer = this.getElement(this.VIEWER);
    if(!viewer){
      let fileDiv = this.getElement(this.FILE);
      viewer = new AonViewer();
      let objeto = {
        domain_id: LS.getDomainId(),
        domain_name: LS.getDomainName(),
        login: LS.getDomainLogin()
      };
      let json = btoa(JSON.stringify(objeto));
      viewer.id = this.VIEWER;
      viewer.type = 'application/pdf';
      viewer.width = fileDiv.offsetWidth;
      viewer.file = '/ms/api/download_invoice_pdf_ak?json=' + json;
      this.clearElement(fileDiv);
      fileDiv.appendChild(viewer);
    } else viewer.printPdf();
  }

  buildData() {
    let card = this.getElement(this.DATA_CARD);
    card.setContentHTML('');
		let table = document.createElement('table');
		table.style.width = '100%';
		card.setContent(table);

    let tr0 = document.createElement('tr');
    table.appendChild(tr0);

    let tdFondo = document.createElement('td');
    tdFondo.setAttribute('colspan', '2');

    let uploadFondo = new AonUpload();
    uploadFondo.id = this.id + 'Upload';
    uploadFondo.setMessage(MSG.ATTACH_FILES_DRAGGING_DROPPING_BACKGROUND);
    uploadFondo.setDeleteMessage(MSG.DELETE_BACKGROUND_CONFIRM);
    uploadFondo.setShowDeleteButton(this.printConfiguration.background);
   
    let filter = {
      attachType: 'data',
      source: 17
    };
    getAttach(filter).then(attach => {
      this.logo = attach;
      if(this.logo.id)
        uploadFondo.setAttach(attach);
    });
    
    uploadFondo.addEventListener(EVENT.UPLOAD, (e) => {
      getReader(e.detail).then(f => {
        this.printConfiguration.backgroundRemove = false;
        this.printConfiguration.backgroundAttach = f;
        this.save();
      });
    });

    uploadFondo.addEventListener(EVENT.DELETE, (e) => {
      this.printConfiguration.backgroundRemove = true;
      this.save();
    });

    tdFondo.appendChild(uploadFondo);
		tr0.appendChild(tdFondo);

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
    });

    let tr3 = document.createElement('tr');
    table.appendChild(tr3);

    let tdAdjust = document.createElement('td');
    tdAdjust.setAttribute('colspan', '1');
    tdAdjust.style.height = '60px';
    tdAdjust.innerHTML = `<aon-switch id="aonInvoicePrintConfigurationAdjust" title="${MSG.BACKGROUND_ADJUST}"></aon-switch>`;
    tr3.appendChild(tdAdjust);
    let adjust = document.getElementById('aonInvoicePrintConfigurationAdjust');
    adjust.checked = this.printConfiguration.adjust;
    adjust.setWidth('135px');
    adjust.addEventListener('change', () => {
      this.printConfiguration.adjust = adjust.checked;
      this.save();
    });

    let tdLogo = document.createElement('td');
    tdLogo.setAttribute('colspan', '1');
    tdLogo.style.height = '60px';
    tdLogo.innerHTML = `<aon-switch id="aonInvoicePrintConfigurationLogo" title="${MSG.INCLUDE_LOGO}"></aon-switch>`;
    tr3.appendChild(tdLogo);
    let logo = document.getElementById('aonInvoicePrintConfigurationLogo');
    logo.checked = this.printConfiguration.logo;
    logo.setWidth('135px');
    logo.addEventListener('change', () => {
      this.printConfiguration.logo = logo.checked;
      this.save();
    });

    let tr5 = document.createElement('tr');
    table.appendChild(tr5);

    let tdDetailed= document.createElement('td');
    tdDetailed.setAttribute('colspan', '1');
    tdDetailed.style.height = '60px';
    tdDetailed.innerHTML = `<aon-switch id="aonInvoicePrintConfigurationDetailed" title="${MSG.DETAILED}"></aon-switch>`;
    tr5.appendChild(tdDetailed);
    let detailed = document.getElementById('aonInvoicePrintConfigurationDetailed');
    detailed.checked = this.printConfiguration.detailed;
    detailed.setWidth('135px');
    detailed.addEventListener('change', () => {
      this.printConfiguration.detailed = detailed.checked;
      this.save();
    });

    let tdCompany= document.createElement('td');
    tdCompany.setAttribute('colspan', '1');
    tdCompany.style.height = '60px';
    tdCompany.innerHTML = `<aon-switch id="aonInvoicePrintConfigurationCompanyData" title="${MSG.INCLUDE_COMPANY_DATA}"></aon-switch>`;
    tr5.appendChild(tdCompany);
    let company = document.getElementById('aonInvoicePrintConfigurationCompanyData');
    company.checked = this.printConfiguration.company;
    company.setWidth('135px');
    company.addEventListener('change', () => {
      this.printConfiguration.company = company.checked;
      this.save();
    });

    let tr6 = document.createElement(TAG.TR);
    table.appendChild(tr6);

    let tdRecordData= document.createElement(TAG.TD);
    tdRecordData.setAttribute('colspan', '1');
    tdRecordData.style.height = '60px';
    tdRecordData.innerHTML = `<aon-switch id="aonInvoicePrintConfigurationRecordData" title="${MSG.INCLUDE_REGISTRATION_DATA}"></aon-switch>`;
    tr6.appendChild(tdRecordData);
    let recordData = document.getElementById('aonInvoicePrintConfigurationRecordData');
    recordData.checked = this.printConfiguration.recordData;
    recordData.setWidth('135px');
    recordData.addEventListener('change', () => {
      this.printConfiguration.recordData = recordData.checked;
      this.save();
    });

    let tdContactData = document.createElement(TAG.TD);
    tdContactData.setAttribute('colspan', '1');
    tdContactData.style.height = '60px';
    tdContactData.innerHTML = `<aon-switch id="aonInvoicePrintConfigurationContactData" title="${MSG.INCLUDE_CONTACT_DATA}"></aon-switch>`;
    tr6.appendChild(tdContactData);
    let contactData = document.getElementById('aonInvoicePrintConfigurationContactData');
    contactData.checked = this.printConfiguration.contactData;
    contactData.setWidth('135px');
    contactData.addEventListener('change', () => {
      this.printConfiguration.contactData = contactData.checked;
      this.save();
    });

    let tr7 = document.createElement(TAG.TR);
    table.appendChild(tr7);

    const languages = [
      {value: Language.SPANISH, name: MSG.SPANISH},
      {value: Language.ENGLISH, name: MSG.ENGLISH},
      {value: Language.DEUTSCH, name: MSG.DEUTSCH},
      {value: Language.BASQUE, name: MSG.BASQUE},
      {value: Language.CATALAN, name: MSG.CATALAN},
      {value: Language.GALICIAN, name: MSG.GALICIAN},
    ];

    let tdLanguage = document.createElement(TAG.TD);
    tdLanguage.setAttribute('colspan', '2');
    tdLanguage.style.height = '60px';
    let language = new AonSelect() ;
    language.id = 'aonInvoicePrintConfigurationLanguage';
    language.title = MSG.LANGUAGE;
    language.setOptions(languages);
    language.value = this.printConfiguration.language;
    language.addEventListener('change', () => {
      this.printConfiguration.language = language.value;
      this.save();
    });
    tdLanguage.appendChild(language);
    tr7.appendChild(tdLanguage);

    let tr8 = document.createElement(TAG.TR);
    table.appendChild(tr8);

    const themes = [
      {value: Theme.WHITE_AND_WHITE, name: MSG.WHITE_AND_WHITE},
      {value: Theme.AON_BLUE, name: MSG.AON_BLUE},
      // {value: Theme.PERSONALIZATED, name: MSG.PERSONALIZATED}
    ];

    let tdTheme = document.createElement(TAG.TD);
    tdTheme.setAttribute('colspan', '2');
    tdTheme.style.height = '60px';
    let themeSelect = new AonSelect() ;
    themeSelect.id = 'aonInvoicePrintConfigurationTheme';
    themeSelect.title = MSG.THEME;
    themeSelect.setOptions(themes);
    themeSelect.value = this.printConfiguration.theme.theme;
    themeSelect.addEventListener('change', () => {
      this.printConfiguration.theme.theme = themeSelect.value;
      this.save();
    });
    tdTheme.appendChild(themeSelect);

    // let colorInput = new AonIconButton();
    // colorInput.id = 'aonInvoicePrintConfigurationThemeColor';
    // colorInput.icon = 'palette';
    // colorInput.disabled = this.printConfiguration.theme.theme !== Theme.PERSONALIZATED;
    // colorInput.addEventListener(EVENT.CLICK, () => {
    //   alert("color");
    // });

    // tdTheme.appendChild(colorInput);
    tr8.appendChild(tdTheme);
    
    
  }

}

window.customElements.define('aon-invoice-print',  AonInvoicePrint);
