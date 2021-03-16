import {AonDocument} from './aon-document.js';
import {DocumentalAction} from './DocumentalEnums.js';
import {getDomainUserRoles} from '../../services/service.js';
import {DomainUserRoles} from '../../models/DomainUserRoles.js';
import {ToolbarType} from '../../models/enums.js';

import '../../components/aon-card.js';
import '../../components/aon-viewer.js';

import * as CONSTANT from "../../environments/constants.js";
import * as MSG from "../../environments/msg.js";
import * as MATERIAL_ICONS from "../../environments/materialIcons.js";

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
      <div>
        <aon-card id="${this.FILE_CARD}" title="${MSG.AON_MSG_FILE}" style="display:none;"> </aon-card>
        <aon-card id="${this.DATA_CARD}" title="${MSG.AON_MSG_FILE_DATA}"> </aon-card>
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

      let send = DocumentalAction.SEND;
      send.fn = () => this.send();

      let download = DocumentalAction.DOWNLOAD;
      download.fn = () => this.download();

      let remove = DocumentalAction.DELETE;
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
