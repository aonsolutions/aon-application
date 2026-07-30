import {AonElement} from '../../components/AonElement.js';

import { CONSTANT, CSS, EVENT, MSG, TAG } from "../../environments/environments.js";
import { getPrintInvoiceConfiguration, savePrintInvoiceConfiguration } from '../../services/invoiceService.js';
import { getReader } from '../../services/utils.js';

import * as LS from '../../services/localStorageService.js';
import { AonUpload } from '../../components/aon-upload.js';
import { AonViewer } from '../../components/aon-viewer.js';
import { getAttach } from '../../services/fileService.js';
import { Language } from '../../models/Language.js';
import { Theme } from './Themes.js';
import { AonIconButton } from '../../components/aon-icon-button.js';
import { AonBasicTable } from '../../components/aon-basic-table.js';
import { AonColor } from '../../components/aon-color.js';
import { AonSwitch } from '../../components/aon-switch.js';
import { AonCard } from '../../components/aon-card.js';
import { AonSlider } from '../../components/aon-slider.js';
import { AonDialog } from '../../components/aon-dialog.js';
import { createSelect } from '../../components/CreateComponent.js';

export class AonInvoicePrint extends AonElement {

  DATA;
  DATA_CARD;
  DATA_TABLE;
  LOGO;
  COMPANY;
  HEADER;
  FOOTER;

  FILE;
  VIEWER;
  DIALOG;
  printConfiguration;

  get id() {
    return this.getAttribute('id');
  }

  set id(id) {
    this.setAttribute('id', id);
  }

	get autosave() {
		return this.getAttribute(CONSTANT.AUTOSAVE);
  	}

  	set autosave(autosave) {
		this.setAttribute(CONSTANT.AUTOSAVE, autosave);
	}

  constructor () {
    super();
  }

  connectedCallback () {
    this.initialize();
    if(!this.printConfiguration) {
      getPrintInvoiceConfiguration().then(r => {
        this.printConfiguration = r;
        this.build();
      });
    } else this.build();
  }

  initialize() {
    this.id = this.id || 'aonInvoicePrintConfiguration';
    this.DATA = this.id + CONSTANT.DATA.initCap();
    this.DATA_CARD = this.DATA + CONSTANT.CARD.initCap();
    this.DATA_TABLE = this.DATA_CARD + CONSTANT.TABLE.initCap();
    this.LOGO = this.id + CONSTANT.LOGO.initCap();
    this.COMPANY = this.id + CONSTANT.COMPANY.initCap();
    this.HEADER = this.id + CONSTANT.HEADER.initCap();
    this.FOOTER = this.id + CONSTANT.FOOTER.initCap();
    this.BORDER = this.id + 'Border';
    this.REGISTRATION_DATA = this.id + 'RegistrationData';
    this.CONTACT_DATA = this.id + 'ContactData';
    this.LANGUAGE = this.id + 'Language';
    this.THEME = this.id + 'Theme';
    this.DETAILED = this.id + 'Detailed';
    this.BACKGROUND = this.id + 'Background';
    this.BACKGROUND_ADJUST = this.id + 'BackgroundAdjust';
    this.PERSONALIZED = this.id + 'Personalized';
    this.FILE = this.id + 'File';
    this.VIEWER = this.id + 'Viewer';
    this.DIALOG = this.id + "Dialog";
  }

  build() {
    let div = this.createElement(TAG.DIV);
    this.appendChild(div);
    if(!this.isMobile()){
      div.style.display = 'flex';
      div.style.height = '100%';
    }
    
    let dataDiv = this.createElement(TAG.DIV, this.DATA, CSS.AON_SUB_CONTENT);
    dataDiv.style.width = this.isMobile() ? '100%' : '50%';
    dataDiv.style.height = '100%';
    div.appendChild(dataDiv);

    let fileDiv = this.createElement(TAG.DIV, this.FILE, CSS.AON_SUB_CONTENT);
    fileDiv.style.display = 'block';
    fileDiv.style.width = this.isMobile() ? '100%' : '50%';
    fileDiv.style.height = '100%';
    div.appendChild(fileDiv);
    
		if(localStorage.getItem('aon_solutions') === undefined || localStorage.getItem('aon_solutions') === null) {
      let offset1 = fileDiv.getBoundingClientRect();
      fileDiv.style.height = `calc(100vh - ${offset1.top + 2}px)`;

      let offset2 = dataDiv.getBoundingClientRect();
  		dataDiv.style.height = `calc(100vh - ${offset2.top + 2}px)`;
    }

    this.buildData(dataDiv);
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
      this.clearElement(fileDiv);
      fileDiv.appendChild(viewer);
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
    } else viewer.printPdf();
  }

  buildData(parent) {
    let card = this.createAonElement(new AonCard(), this.DATA_CARD, MSG.CUSTOMIZE_INVOICE);
    parent.appendChild(card);

    let table = this.createAonElement(new AonBasicTable(), this.DATA_TABLE);
		card.setContent(table);

    table.addRow();

    let logo = this.createAonElement(new AonSwitch(), this.LOGO, MSG.INCLUDE_LOGO);
    logo.checked = this.printConfiguration.logo;
    table.addCell(logo, 1).style.height = '60px';;
    logo.setWidth('135px');
    logo.onChange(() => {
      this.printConfiguration.logo = logo.checked;
      // if(this.autosave) 
        this.save();
      this.dispatchEvent(new Event(EVENT.CHANGE));
    });

    let company = this.createAonElement(new AonSwitch(), this.COMPANY, MSG.INCLUDE_COMPANY_DATA);
    company.checked = this.printConfiguration.company;
    table.addCell(company, 1).style.height = '60px';
    company.setWidth('135px');
    company.onChange(() => {
      this.printConfiguration.company = company.checked;
      // if(this.autosave) 
        this.save();
      this.dispatchEvent(new Event(EVENT.CHANGE));
    });
    table.addRow();

    let header = this.createAonElement(new AonSlider(), this.HEADER, MSG.HEADER);
    header.min = 0;
    header.max = 200;
    table.addCell(header, 2).style.height = '60px';;
    header.setValue(this.printConfiguration.header);
    header.onChange(() => {
      this.printConfiguration.header = header.value;
      // if(this.autosave) 
        this.save();
      this.dispatchEvent(new Event(EVENT.CHANGE));
    });

    table.addRow();

    let footer = this.createAonElement(new AonSlider(), this.FOOTER, MSG.FOOTER);
    footer.min = 0;
    footer.max = 200;
    table.addCell(footer, 2).style.height = '60px';;
    footer.setValue(this.printConfiguration.footer);
    footer.onChange(() => {
      this.printConfiguration.footer = footer.value;
      // if(this.autosave) 
        this.save();
      this.dispatchEvent(new Event(EVENT.CHANGE));
    });

    table.addRow();

    let detailed = this.createAonElement(new AonSwitch(), this.DETAILED, MSG.DETAILED);
    detailed.checked = this.printConfiguration.detailed;
    table.addCell(detailed, 1).style.height = '60px';;
    detailed.setWidth('135px');
    detailed.onChange(() => {
      this.printConfiguration.detailed = detailed.checked;
      // if(this.autosave) 
        this.save();
      this.dispatchEvent(new Event(EVENT.CHANGE));
    });

    let borderOptions = [
      {value: "0", name: 'Sin Bordes'},
      {value: "1", name: 'Solo Títulos'},
      {value: "2", name: 'Títulos y Cuerpo'}, 
    ]
    let border = createSelect(this.BORDER, MSG.BORDER);
    border.setOptions(borderOptions);
    border.value = this.printConfiguration.border;
    table.addCell(border, 1).style.height = '60px';
    border.onChange(() => {
      this.printConfiguration.border = border.value;
      // if(this.autosave) 
        this.save();
      this.dispatchEvent(new Event(EVENT.CHANGE));
    });

    table.addRow();

    let registrationData =  this.createAonElement(new AonSwitch(), this.REGISTRATION_DATA, MSG.INCLUDE_REGISTRATION_DATA);
    registrationData.checked = this.printConfiguration.recordData;
    table.addCell(registrationData, 1).style.height = '60px';;
    registrationData.setWidth('135px');
    registrationData.onChange(() => {
      this.printConfiguration.recordData = registrationData.checked;
      // if(this.autosave) 
        this.save();
      this.dispatchEvent(new Event(EVENT.CHANGE));
    });

    let contactData = this.createAonElement(new AonSwitch(), this.CONTACT_DATA, MSG.INCLUDE_CONTACT_DATA);
    contactData.checked = this.printConfiguration.contactData;
    table.addCell(contactData, 1).style.height = '60px';;
    contactData.setWidth('135px');
    contactData.onChange(() => {
      this.printConfiguration.contactData = contactData.checked;
      // if(this.autosave) 
        this.save();
      this.dispatchEvent(new Event(EVENT.CHANGE));
    });

    table.addRow();

    const languages = [
      {value: Language.SPANISH, name: MSG.SPANISH},
      {value: Language.ENGLISH, name: MSG.ENGLISH},
      {value: Language.DEUTSCH, name: MSG.DEUTSCH},
      {value: Language.BASQUE, name: MSG.BASQUE},
      {value: Language.CATALAN, name: MSG.CATALAN},
      {value: Language.GALICIAN, name: MSG.GALICIAN},
    ];

    let language = createSelect(this.LANGUAGE, MSG.LANGUAGE);
    language.setOptions(languages);
    language.value = this.printConfiguration.language;
    language.onChange(() => {
      this.printConfiguration.language = language.value;
      // if(this.autosave) 
        this.save();
      this.dispatchEvent(new Event(EVENT.CHANGE));
    });
    table.addCell(language, 1).style.height = '60px';

    const themes = [
      {value: Theme.BLACK_AND_WHITE, name: MSG.BLACK_AND_WHITE},
      {value: Theme.AON_BLUE, name: MSG.AON_BLUE},
      {value: Theme.PERSONALIZED, name: MSG.PERSONALIZED}
    ];

    let bt = new AonBasicTable();
    bt.id = 'themeTable';
    table.addCell(bt, 1);
    bt.addRow();

    let themeSelect = createSelect(this.THEME, MSG.THEME) ;
    themeSelect.setOptions(themes);
    themeSelect.value = this.printConfiguration.theme.theme;
    themeSelect.addEventListener('change', () => {
      this.printConfiguration.theme.theme = themeSelect.value;
      this.getElement(this.PERSONALIZED)
        .setDisabled(this.printConfiguration.theme.theme !== Theme.PERSONALIZED);
      // if(this.autosave) 
        this.save();
      this.dispatchEvent(new Event(EVENT.CHANGE));
    });
    bt.addCell(themeSelect);

    let personalized = this.createAonElement(new AonIconButton(), this.PERSONALIZED, MSG.PERSONALIZED);
    personalized.icon = 'palette';
    personalized.onClick(() => {
      if(this.printConfiguration.theme.theme === Theme.PERSONALIZED) {
        this.buildThemeDialog();
      }
    });
    bt.addCell(personalized);    
    personalized.disabled = this.printConfiguration.theme.theme !== Theme.PERSONALIZED;

    table.addRow();

    let div = this.createElement(TAG.DIV, this.LEGAL);
    let divTitle = this.createElement(TAG.DIV, this.LEGAL_TITLE);
    divTitle.style.fontSize = '14px';
    divTitle.innerHTML = 'Aviso Legal';
    div.appendChild(divTitle);

    let textarea = this.createElement('textarea', this.LEGAL_TEXTAREA);
    textarea.style.width = '100%';
    textarea.style.height = '100px';
    textarea.value = this.printConfiguration.legal;
    textarea.addEventListener(EVENT.CHANGE, () => {
      this.printConfiguration.legal = textarea.value;
      this.save();
    });
    div.appendChild(textarea)
    table.addCell(div, 2);

    table.addRow(); 

    let background = this.createAonElement(new AonSwitch(), this.BACKGROUND, MSG.BACKGROUND);
    background.checked = this.printConfiguration.background;
    table.addCell(background, 1).style.height = '60px';;
    background.setWidth('135px');
    background.onChange(() => {
      if(background.checked == 'true') {
        this.getElement(this.BACKGROUND_ADJUST).classList.remove(CSS.AON_NONE);
        this.getElement( this.id + 'Upload').classList.remove(CSS.AON_NONE);
      } else {
        this.getElement(this.BACKGROUND_ADJUST).classList.add(CSS.AON_NONE);
        this.getElement( this.id + 'Upload').classList.add(CSS.AON_NONE);
        this.printConfiguration.backgroundRemove = true;
        // if(this.autosave)
          this.save();
        this.dispatchEvent(new Event(EVENT.CHANGE));
      }
    });

    let adjust = this.createAonElement(new AonSwitch(), this.BACKGROUND_ADJUST, MSG.BACKGROUND_ADJUST);
    adjust.checked = this.printConfiguration.adjust;
    table.addCell(adjust, 1);
    if(!this.printConfiguration.background) {
      adjust.classList.add(CSS.AON_NONE);
    }
    adjust.setWidth('135px');
    adjust.onChange(() => {
      this.printConfiguration.adjust = adjust.checked;
      // if(this.autosave) 
        this.save();
      this.dispatchEvent(new Event(EVENT.CHANGE));
    });

    table.addRow();

    let uploadFondo = new AonUpload();
    uploadFondo.id = this.id + 'Upload';
    if(!this.printConfiguration.background) {
      uploadFondo.classList.add(CSS.AON_NONE);
    }
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
				// if(this.autosave) 
          this.save();
        this.dispatchEvent(new Event(EVENT.CHANGE));
      });
    });

    uploadFondo.addEventListener(EVENT.DELETE, (e) => {
      this.printConfiguration.backgroundRemove = true;
      // if(this.autosave) 
        this.save();
      this.dispatchEvent(new Event(EVENT.CHANGE));
    });

    table.addCell(uploadFondo, 2);
  }

  buildThemeDialog() {
    let div = this.createElement(TAG.DIV);
    let d = this.getElement(this.DIALOG);
    if(!d) {
      d = this.createAonElement(new AonDialog(), this.DIALOG);
      this.appendChild(d);
    }
		d.clear();
		if(!this.isMobile()) d.width = '450px';
		d.setTitle(MSG.PERSONALIZED_THEME);
		d.setContent(div);
		d.addAcceptAction(() => {
      // if(this.autosave) 
        this.save();
      this.dispatchEvent(new Event(EVENT.CHANGE));
		});
		d.open();

    let div1 = this.createElement(TAG.DIV);
    div1.style.marginTop = '15px';
    let span = this.createElement(TAG.SPAN);
    span.style.color = 'gray';
    span.innerHTML = 'Factura';
    div1.appendChild(span);
    div.appendChild(div1);

    let t1 = new AonBasicTable();
    t1.id = 'aonTable1';
    div.appendChild(t1);
    t1.addRow();

    let textColor = new AonColor();
    textColor.id = 'aonColorTextColor';
    textColor.title = 'Texto de la Factura';
    textColor.value = this.printConfiguration.theme.textColor;
    textColor.addEventListener(EVENT.CHANGE, () => this.printConfiguration.theme.textColor = textColor.value);
    t1.addCell(textColor);

    t1.addRow();

    let customerBackgroundColor = new AonColor();
    customerBackgroundColor.id = 'aonColorCustomerBackgroundColor';
    customerBackgroundColor.title = 'Fondo Datos Cliente';
    customerBackgroundColor.value = this.printConfiguration.theme.customerBackgroundColor;
    customerBackgroundColor.addEventListener(EVENT.CHANGE, () => this.printConfiguration.theme.customerBackgroundColor = customerBackgroundColor.value);
    t1.addCell(customerBackgroundColor);

    t1.addRow();

    let titelTextColor = new AonColor();
    titelTextColor.id = 'aonColorTitleTextColor';
    titelTextColor.title = 'Texto Preimpreso';
    titelTextColor.value = this.printConfiguration.theme.titleTextColor;
    titelTextColor.addEventListener(EVENT.CHANGE, () => this.printConfiguration.theme.titleTextColor = titelTextColor.value);
    t1.addCell(titelTextColor);

    let div2 = this.createElement(TAG.DIV);
    div2.style.marginTop = '15px';
    let span2 = this.createElement(TAG.SPAN);
    span2.style.color = 'gray';
    span2.innerHTML = 'Tablas';
    div2.appendChild(span2);
    div.appendChild(div2);

    let t2 = new AonBasicTable();
    t2.id = 'aonTable2';
    div.appendChild(t2);
    t2.addRow();

    let boxTitleBackground = new AonColor();
    boxTitleBackground.id = 'aonColorBoxTitleBackgroundColor';
    boxTitleBackground.title = 'Fondo Título';
    boxTitleBackground.value = this.printConfiguration.theme.boxTitleBackgroundColor;
    boxTitleBackground.addEventListener(EVENT.CHANGE, () => this.printConfiguration.theme.boxTitleBackgroundColor = boxTitleBackground.value);
    t2.addCell(boxTitleBackground);

    t2.addRow();

    let boxTitleText = new AonColor();
    boxTitleText.id = 'aonColorBoxTitleTextColor';
    boxTitleText.title = 'Texto Título';
    boxTitleText.value = this.printConfiguration.theme.boxTitleTextColor;
    boxTitleText.addEventListener(EVENT.CHANGE, () => this.printConfiguration.theme.boxTitleTextColor = boxTitleText.value);
    t2.addCell(boxTitleText);

    t2.addRow();

    let boxBodyBackground = new AonColor();
    boxBodyBackground.id = 'aonColorBoxBodyBackgroundColor';
    boxBodyBackground.title = 'Color del Fondo';
    boxBodyBackground.value = this.printConfiguration.theme.boxBodyBackgroundColor;
    boxBodyBackground.addEventListener(EVENT.CHANGE, () => this.printConfiguration.theme.boxBodyBackgroundColor = boxBodyBackground.value);
    t2.addCell(boxBodyBackground);

    t2.addRow();

    let boxBorder = new AonColor();
    boxBorder.id = 'aonColorBoxBorderColor';
    boxBorder.title = 'Color del Borde';
    boxBorder.value = this.printConfiguration.theme.boxBorderColor;
    boxBorder.addEventListener(EVENT.CHANGE, () => this.printConfiguration.theme.boxBorderColor = boxBorder.value);
    t2.addCell(boxBorder);
  }

  getPrintConfiguration() {
    return this.printConfiguration;
  }

  setPrintConfiguration(printConfiguration) {
    this.printConfiguration = printConfiguration;
  }

}

window.customElements.define('aon-invoice-print',  AonInvoicePrint);
