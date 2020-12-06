import {AonElement} from '../../components/AonElement.js';
import {ToolbarType} from '../../models/enums.js';

import * as CONSTANT from "../../environments/constants.js";
import * as MSG from "../../environments/msg.js";
import * as MATERIAL_ICONS from "../../environments/materialIcons.js";

export class AonMobileDocument extends AonElement {

  TOOLBAR;
  DATA;
  DATA_CARD;
  FILE;

  constructor () {
    super();
    this.id = this.id || 'aonDocumentalSheet';
    this.TOOLBAR = this.id + 'Toolbar';
    this.DATA = this.id + 'Data';
    this.DATA_CARD = this.DATA + 'Card';
    this.File = this.id + 'File';
  }

  connectedCallback () {
    let document = {name: 'aaa'};

    this.innerHTML = `
      <aon-toolbar id="${this.TOOLBAR}" type="${ToolbarType.SECONDARY}" title="${document.name}"> </aon-toolbar>
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
    this.buildData();

    if(!this.isMobile())
      this.buildDocumentToolbar();
  }

  buildData() {

  }

  buildDocumentToolbar() {
    let aonDocumental = this.getElement('aonDocumental');
    let documentToolbar = this.getElement(this.TOOLBAR);
    documentToolbar.removeButtons();

    documentToolbar.addButton2(DocumentalAction.NEXT, () => this.next());
    documentToolbar.addButton2(DocumentalAction.PREVIOUS, () => this.previous());

    documentToolbar.addSeparator();

    documentToolbar.addButton2(DocumentalAction.DELETE, () => this.remove());
//  documentToolbar.addButton2(DocumentalAction.SEND, () => this.send());
    documentToolbar.addButton2(DocumentalAction.DOWNLOAD, () => this.download());
    documentToolbar.addButton2(DocumentalAction.BACK, () => this.back());
  }


  back() {

  }

  next() {

  }

  previous() {

  }

  send() {

  }

  remove() {

  }

  download() {

  }


}

window.customElements.define('aon-mobile-document',  AonMobileDocument);
