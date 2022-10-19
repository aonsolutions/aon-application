import {AonDocument} from './aon-document.js';
import {getDomainUserRoles} from '../../services/service.js';
import {DomainUserRoles} from '../../models/DomainUserRoles.js';
import {ToolbarType} from '../../models/enums.js';

import '../../components/aon-card.js';
import '../../components/aon-viewer.js';

import { MSG, CSS } from '../../environments/environments.js';

import * as ACTION from '../actions.js';

import {DOCUMENTAL} from  '../../services/app.js';

export class AonMobileDocument extends AonDocument {

  FILE_CARD;

  constructor () {
    super();
  }

  connectedCallback () {
    this.initialize();
    this.FILE_CARD = this.FILE + 'Card';
    this.innerHTML = `
      <aon-toolbar id="${this.TOOLBAR}" type="${ToolbarType.SECONDARY}" title="DOCUMENTO"> </aon-toolbar>
      <div class="${CSS.AON_MOBILE_SUB_CONTENT}">
        <aon-card id="${this.FILE_CARD}" title="${MSG.FILE}" style="display:none;"> </aon-card>
        <aon-card id="${this.DATA_CARD}" title="${MSG.FILE_DATA}"> </aon-card>
      </div>
    `;

    getDomainUserRoles({}).then(r => {
      this._roles = new DomainUserRoles(r);
      this.buildOptions();
      this.buildData();
      this.buildDocumentToolbar();
      if(this.document.file.type.includes('pdf') || this.document.file.type.includes('image')) {
        let fileCard = this.getElement(this.FILE_CARD);
        fileCard.style.display = 'block';
        this.getElement(fileCard.TITLE).style.marginBottom = '0px';
        fileCard.cleanSection2();
        fileCard.addTitleButton('Visualizar', 'visibility', false, () => this.openFileCard());
      }
    });
  }

  buildOptions() {
    let aonDocumental = this.getApplication();
    let aonDocumentalToolbar = this.getElement(aonDocumental.TOOLBAR);
    aonDocumentalToolbar.removeButtons();
    aonDocumental.addToolbarOption('Options', 'more_vert', () => {
      let button = this.getElement(aonDocumentalToolbar.TOOL_SECTION + 'OptionsButton');
      const top  = button.getBoundingClientRect().top;
      const left = button.getBoundingClientRect().left;

      let d = document.getElementById(aonDocumental.OPTION_DIALOG);

      let send = ACTION.SEND_FILE;
      send.permission = true;
			send.backgroundColor = DOCUMENTAL.color;
      send.fn = () => this.send();

      let download = ACTION.DOWNLOAD_FILE;
      download.permission = true;
			download.backgroundColor = DOCUMENTAL.color;
      download.fn = () => this.download();

      let remove = ACTION.DELETE_FILE;
      remove.permission = true;
			remove.backgroundColor = DOCUMENTAL.color;
      remove.fn = () => this.remove();

      let actions = [send, download];
      if(this._roles.isDocumentalManager() || this._roles.isDocumentalPortal()) {
        actions.push(remove);
      }
      d.setMenuOptions(actions, top, left);
      d.open();
    });
  }

  openFileCard() {
    let fileCard = this.getElement(this.FILE_CARD);
    let w = this.getElement(fileCard.CONTENT).offsetWidth;
    fileCard.setContentHTML(`<aon-viewer type="${this.document.file.type}" file="${this.document.file.url}" width="${w}"></aon-viewer>`);
    fileCard.cleanSection2();
    fileCard.addTitleButton('Visualizar', 'visibility_off', false, () => this.closeFileCard());
  }

  closeFileCard() {
    let fileCard = this.getElement(this.FILE_CARD);
    fileCard.setContentHTML('');
    fileCard.cleanSection2();
    fileCard.addTitleButton('Visualizar', 'visibility', false, () => this.openFileCard());
  }

}

window.customElements.define('aon-mobile-document',  AonMobileDocument);
