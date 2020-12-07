import {AonDocument} from './aon-document.js';
import {DocumentalAction} from './DocumentalEnums.js';

import * as CONSTANT from "../../environments/constants.js";
import * as MSG from "../../environments/msg.js";
import * as MATERIAL_ICONS from "../../environments/materialIcons.js";

export class AonMobileDocument extends AonDocument {

  FILE_CARD;

  constructor () {
    super();
    this.FILE_CARD = this.FILE + 'Card';
  }

  connectedCallback () {
    this.innerHTML = `
      <aon-card id="${this.FILE_CARD}" title="${MSG.AON_MSG_FILE}" style="display:none;"> </aon-card>
      <aon-card id="${this.DATA_CARD}" title="${MSG.AON_MSG_FILE_DATA}"> </aon-card>
    `;

    this.buildOptions();
    this.buildData();

    if(this.document.file.type.includes('pdf') || this.document.file.type.includes('image')) {
      let fileCard = this.getElement(this.FILE_CARD);
      fileCard.style.display = 'block';
      this.getElement(fileCard.TITLE).style.marginBottom = '0px';
      fileCard.addTitleButton('visibility', () => this.openFileCard());
    }
  }

  buildOptions() {
    let aonDocumental = this.getElement('aonDocumental');
    let aonDocumentalToolbar = this.getElement(aonDocumental.TOOLBAR);
    aonDocumentalToolbar.removeButtons();
    aonDocumental.addToolbarOption('Options', 'more_vert', () => {
      let button = this.getElement(aonDocumentalToolbar.TOOL_SECTION + 'OptionsButton');
      const top  = button.getBoundingClientRect().top;
      const left = button.getBoundingClientRect().left;

      let d = document.getElementById(aonDocumental.OPTION_DIALOG);

      let send = DocumentalAction.SEND;
      send.fn = () => this.send();

      let download = DocumentalAction.DOWNLOAD;
      download.fn = () => this.download();

      let remove = DocumentalAction.DELETE;
      remove.fn = () => this.remove();

      let actions = [send, download, remove];
      d.setMenuOptions(actions, top, left);
      d.open();
    });
  }

  openFileCard() {
    let fileCard = this.getElement(this.FILE_CARD);
    let w = this.getElement(fileCard.CONTENT).offsetWidth;
    fileCard.setContentHTML(`<aon-viewer type="${this.document.file.type}" file="${this.document.file.url}" width="${w}"><aon-viewer>`);
    fileCard.addTitleButton('visibility_off', () => this.closeFileCard());
  }

  closeFileCard() {
    let fileCard = this.getElement(this.FILE_CARD);
    fileCard.setContentHTML('');
    fileCard.addTitleButton('visibility', () => this.openFileCard());
  }
}

window.customElements.define('aon-mobile-document',  AonMobileDocument);
