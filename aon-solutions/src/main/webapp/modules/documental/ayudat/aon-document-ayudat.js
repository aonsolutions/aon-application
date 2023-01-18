import { AonElement } from '../../../components/AonElement.js';
import { ToolbarType } from '../../../models/enums.js';
import {  openFileUrl } from '../../../services/service.js';
import { AonSelect } from '../../../components/aon-select.js';
import { CONSTANT, CSS, EVENT, MSG, TAG } from '../../../environments/environments.js';
import * as ACTION from '../../actions.js';
import { extensionsType } from '../../../services/extensionsEnums.js';
import { DOCUMENTAL_VIEWS } from '../DocumentalEnums.js';
import { postBidoq } from '../../../services/bidoqService.js';
import { setAttributes } from '../../../services/utilsComponents.js';
import { AonViewer } from '../../../components/aon-viewer.js';
import { AonToolbar } from '../../../components/aon-toolbar.js';
import { AonCard } from '../../../components/aon-card.js';
import { AonInput } from '../../../components/aon-input.js';
import { AonDateUtils } from '../../utils/AonDateUtils.js';


export class AonDocumentAyudat extends AonElement {

  doc;
  _tags;
  TOOLBAR;
  DATA;
  DATA_CARD;
  FILE;

  get data() {
    return JSON.parse(this.getAttribute(CONSTANT.DATA));
  }

  set data(value) {
    this.setAttribute(CONSTANT.DATA, JSON.stringify(value));
  }

  constructor () {
    super();
  }

  connectedCallback () {
    this.initialize();
    this.build();
  }

  initialize() {
    this.id = this.id || 'aonDocumentalSheet';
    this.TOOLBAR = this.id + 'Toolbar';
    this.DATA = this.id + 'Data';
    this.DATA_CARD = this.DATA + 'Card';
    this.FILE = this.id + 'File';
    this.doc = this.data;
    this._tags = this.doc.tags || [];
    this.applicationEl = this.getApplication();
    this.applicationParentEl = this.applicationEl.getParent();
    this.applicationEl.removeToolbarOptions();
  }


  build() {
    this.paintView();
    this.buildData();
    this.buildDocumentToolbar();
  }

  paintView(){
    let aonToolbar = setAttributes(new AonToolbar(),{
      id: this.TOOLBAR,
      type: ToolbarType.SECONDARY,
      title: this.data.title
    });
    this.appendChild(aonToolbar);

    let div = this.createElement(TAG.DIV);
    div.className = CSS.AON_FLEX;
    this.appendChild(div);

    let dataDiv = setAttributes(this.createElement(TAG.DIV),{
      id: this.DATA,
      class: CSS.AON_SUB_CONTENT
    });
    dataDiv.style.width = "50%";
    div.appendChild(dataDiv);

    let aonCard = setAttributes(new AonCard(),{
      id: this.DATA_CARD,
      title: MSG.FILE_DATA
    });
    dataDiv.appendChild(aonCard);

    let fileDiv = setAttributes(this.createElement(TAG.DIV),{
      id: this.FILE,
      class: CSS.AON_SUB_CONTENT
    });
    fileDiv.style.display = 'block';
    fileDiv.style.width = '50%';
    div.appendChild(fileDiv);

    const type = this.getContentType();
    const aonViewer = setAttributes(new AonViewer(),{
      type,
      file: this.data.image, 
      width: fileDiv.offsetWidth
    });
    fileDiv.appendChild(aonViewer);

    if(type.indexOf("pdf")>=0){
      aonViewer.createIframe();
    }

		if(localStorage.getItem('aon_solutions') === undefined || localStorage.getItem('aon_solutions') === null){
      const offset1 = fileDiv.getBoundingClientRect();
      fileDiv.style.height = `calc(100vh - ${offset1.top + 2}px)`;

      const offset2 = dataDiv.getBoundingClientRect();
  		dataDiv.style.height = `calc(100vh - ${offset2.top + 2}px)`;
    }
  }

  getContentType(){
    return extensionsType(this.data.stored_file_name.split(".").reverse()[0]);
  }

  buildData() {
    let card = this.getElement(this.DATA_CARD);

    let spanDate = this.createElement(TAG.SPAN);
    spanDate.style.fontSize = "13px";
    spanDate.innerHTML = this.convertTimeStamp(this.data.date)
    card.getCardTitle2().appendChild(spanDate);

    card.setContentHTML('');
		let table = this.createElement(TAG.TABLE);
		table.style.width = '100%';
		card.setContent(table);

    let tr2 = this.createElement(TAG.TR);
    table.appendChild(tr2);

    let tdName = this.createElement(TAG.TD);
    tdName.setAttribute('colspan', '2');
    let inp = setAttributes(new AonInput(),{
      id: "name",
      description: MSG.NAME
    })
    tdName.appendChild(inp)
		tr2.appendChild(tdName);
		let name = this.getElement('name');
    name.value = this.data.title;
		name.addEventListener(EVENT.CHANGE, () => this.updateName(name.value));

    let tr3 = this.createElement(TAG.TR);
    table.appendChild(tr3);

    // CATEGORY
    let tdCategory = this.createElement(TAG.TD);
    tdCategory.setAttribute('colspan', '1');
    let categorySelect = new AonSelect();
    categorySelect.id = 'category';
    categorySelect.readonly = true;
    categorySelect.disabled = CONSTANT.DISABLED;
    categorySelect.title = MSG.CATEGORY;
    tdCategory.appendChild(categorySelect);
    tr3.appendChild(tdCategory);
    const categories = this.applicationParentEl._folders;
    if(categories){
      categorySelect.setOptions(categories.map(c => ({ value: c.carpetaID, name: c.carpeta})));
      if(this.data.service){
        categorySelect.value = parseInt(this.data.service);
      }
      categorySelect.addEventListener(EVENT.CHANGE, () => this.updateCategory(categorySelect.value));
    }


    // TAG
    let tdTag = this.createElement(TAG.TD);
    tdTag.setAttribute('colspan', '1');
    tr3.appendChild(tdTag);

    let tagSelect = new AonSelect();
    tagSelect.id = 'tag';
    tagSelect.title = MSG.TAG;
    tdTag.appendChild(tagSelect);
   
    tagSelect.addEventListener(EVENT.SELECT, ({detail}) => {
      if(detail){
        this.addTag({id:detail.value, name:detail.name});
        this.setTagsAvaible();
        tagSelect.clear();
      }
    });
  
    let tr5 = this.createElement(TAG.TR);
    table.appendChild(tr5);

    let containerTags = this.createElement(TAG.DIV);
    containerTags.style.display = "flex";
    containerTags.style.flexWrap = "wrap";
    containerTags.id = "containerTags";
    card.setContent(containerTags);
    this.data.tags.forEach((item) => {
      this.addTag(item);
    });

    //set tags avaibles
    this.setTagsAvaible();
  }

  addTag({id, name}){
    const containerTags = this.getElement("containerTags");
    if(containerTags && id && name){
      this._tags.push({id, name});
      let divTag = this.createElement(TAG.DIV);
      divTag.id = 'tag' + id;
      divTag.innerHTML = `
        <span style="background-color: #eee;padding:3px;"> ${name}</span>
        <i id='closeTag${id}'class="material-icons" style="font-size:1rem;cursor: pointer;">close</i>
      `;
      containerTags.appendChild(divTag);
      this.getElement('closeTag' + id).addEventListener(EVENT.CLICK, () => {
        this._tags = this._tags.filter(tag => tag.id !== id );
        const tagEl = this.getElement(`tag${id}`);
        if(tagEl) tagEl.remove();
        this.setTagsAvaible();
      });
    }
  }

  buildDocumentToolbar() {
    let documentToolbar = this.getElement(this.TOOLBAR);
    documentToolbar.removeButtons();
    documentToolbar.addButton2(ACTION.DELETE_FILE, () => this.remove());
    documentToolbar.addButton2(ACTION.SAVE, () => this.save());
    documentToolbar.addButton2(ACTION.DOWNLOAD_FILE, () => this.download());
    documentToolbar.addButton2(ACTION.BACK, () => this.back());
  }


  back() {
   this.applicationParentEl.showView(DOCUMENTAL_VIEWS.AON_DOCUMENTAL_LIST_AYUDAT);
  }

  setTagsAvaible(){
    const tagSelect = this.getElement("tag");
    if(tagSelect){
      const tags = this.applicationParentEl._tags;
      const newTags = tags.filter(({tagsID}) => !this._tags.some(it=> it.id.includes(tagsID))).map(c => ({value: c.tagsID,name: c.tag}));
      tagSelect.setOptions(newTags);
    }
  }

  remove() {
    this.applicationEl.confirmDialog(MSG.DELETE_FILE, `Estás seguro de eliminar el Fichero ${this.data.title}`, async ()=>{
        this.applicationEl.startLoading();
        try {
          let {message} = await postBidoq({
            method:"delete",
            id: this.doc.id,
            type: this.doc.type,
            service: this.doc.service
          });
          if(CONSTANT.SUCCESS  === message){
            message = MSG.DELETED_DATA;
            this.back();
          } 
          this.showToast({message})
        } catch (error) {
          this.showError(error);
        }
        this.applicationEl.stopLoading();
    })
  }

  async download() {
    this.applicationEl.startLoading();
    try {
      await openFileUrl(this.data.image);
    } catch (error) {}

    this.applicationEl.stopLoading();
  }

  updateCategory(carpetaID) {
      this.doc.service = carpetaID;
  }

  updateName(name) {
    this.doc.name = name;
  }

  async save() {
    this.applicationEl.startLoading();
    try {
      const {message} = await postBidoq({
        method:"editar_doc",
        id: this.doc.id,
        tipo: this.doc.type,
        nombre: this.doc.name,
        tags: this._tags.map(t => t.id || t.value).join(",")
      });
      if(CONSTANT.SUCCESS  === message){
        this.showToast({message: MSG.SAVED_DATA, type: CONSTANT.SUCCESS});
      } else {
        this.showToast({message});
      }
    } catch (error) {
      this.showError(error);
    }
    this.applicationEl.stopLoading();
  }

  convertTimeStamp(timestamp){
    return AonDateUtils.setDateTimestamp(new Date(timestamp * 1000));
  }

}

window.customElements.define('aon-document-ayudat',  AonDocumentAyudat);
