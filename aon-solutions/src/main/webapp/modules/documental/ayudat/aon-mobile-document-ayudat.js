import {AonDocumentAyudat} from './aon-document-ayudat.js';
import {ToolbarType} from '../../../models/enums.js';
import { MSG, TAG } from '../../../environments/environments.js';
import { DOCUMENTAL_VIEWS } from '../DocumentalEnums.js';
import { AonToolbar } from '../../../components/aon-toolbar.js';
import { AonCard } from '../../../components/aon-card.js';
import { setAttributes } from '../../../services/utils.js';
import { AonViewer } from '../../../components/aon-viewer.js';

export class AonMobileDocumentAyudat extends AonDocumentAyudat {

  FILE_CARD;

  constructor () {
    super();
  }

  connectedCallback () {
    this.initialize();
    this.id = DOCUMENTAL_VIEWS.AON_DOCUMENT_MOBILE_AYUDAT;
    this.FILE_CARD = this.FILE + 'Card';
    this.paintView();

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

  paintView(){
    let aonToolbar = setAttributes(new AonToolbar(),{
      id: this.TOOLBAR,
      type: ToolbarType.SECONDARY,
      title: "DOCUMENTO"
    });
    this.appendChild(aonToolbar);

    let div = this.createElement(TAG.DIV);
    this.appendChild(div);

    let aonCard = setAttributes(new AonCard(),{
      id: this.FILE_CARD,
      title: MSG.FILE
    });
    aonCard.style.display = "none";
    div.appendChild(aonCard);

    aonCard = setAttributes(new AonCard(),{
      id: this.DATA_CARD,
      title: MSG.FILE_DATA
    });
    div.appendChild(aonCard);

    
  }
  
  openFileCard() {
    let fileCard = this.getElement(this.FILE_CARD);
    const width = this.getElement(fileCard.CONTENT).offsetWidth;
    const type = this.getContentType();
    const aonViewer = setAttributes(new AonViewer(),{
      type,
      file: this.data.image, 
      width
    });
    fileCard.setContent(aonViewer);
    fileCard.cleanSection2();
    fileCard.addTitleButton('Visualizar', 'visibility_off', false, () => this.closeFileCard());

    if(type.indexOf("pdf")>=0){
      aonViewer.createIframe();
    }
  }

  closeFileCard() {
    let fileCard = this.getElement(this.FILE_CARD);
    fileCard.setContentHTML('');
    fileCard.cleanSection2();
    fileCard.addTitleButton('Visualizar', 'visibility', false, () => this.openFileCard());
  }

}

window.customElements.define('aon-mobile-document-ayudat',  AonMobileDocumentAyudat);
