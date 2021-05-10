import {AonDocumentAyudat} from './aon-document-ayudat.js';
import {ToolbarType} from '../../../models/enums.js';
import { MSG } from '../../../environments/environments.js';
import { DOCUMENTAL_VIEWS } from '../DocumentalEnums.js';
import '../../../components/aon-card.js';
import '../../../components/aon-viewer.js';

export class AonMobileDocumentAyudat extends AonDocumentAyudat {

  FILE_CARD;

  constructor () {
    super();
  }

  connectedCallback () {
    this.initialize();
    this.id = DOCUMENTAL_VIEWS.AON_DOCUMENT_MOBILE_AYUDAT;
    this.FILE_CARD = this.FILE + 'Card';
    this.innerHTML = `
      <aon-toolbar id="${this.TOOLBAR}" type="${ToolbarType.SECONDARY}" title="DOCUMENTO"> </aon-toolbar>
      <div>
        <aon-card id="${this.FILE_CARD}" title="${MSG.FILE}" style="display:none;"> </aon-card>
        <aon-card id="${this.DATA_CARD}" title="${MSG.FILE_DATA}"> </aon-card>
      </div>
    `;

    this.buildData();
    this.buildDocumentToolbar();
    if(this.getContentType().includes('pdf') || this.getContentType().includes('image')) {
      let fileCard = this.getElement(this.FILE_CARD);
      fileCard.style.display = 'block';
      this.getElement(fileCard.TITLE).style.marginBottom = '0px';
      fileCard.cleanSection2();
      fileCard.addTitleButton('Visualizar', 'visibility', false, () => this.openFileCard());
    }
  }

  openFileCard() {
    let fileCard = this.getElement(this.FILE_CARD);
    let w = this.getElement(fileCard.CONTENT).offsetWidth;
    fileCard.setContentHTML(`<aon-viewer type="${this.getContentType()}" file="${this.data.image}" width="${w}"></aon-viewer>`);
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

window.customElements.define('aon-mobile-document-ayudat',  AonMobileDocumentAyudat);
