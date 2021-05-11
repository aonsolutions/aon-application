import { AonMobileList } from '../../../components/aon-mobile-list.js';
import { AonTable } from '../../../components/aon-table.js';
import { AonElement } from '../../../components/AonElement.js';
import { CONSTANT, EVENT, MSG } from '../../../environments/environments.js';
import { postBidoq } from  '../../../services/bidoqService.js';
import {  formatBytes, formatDate, getReader, newComponent } from '../../../services/utils.js';
import { DOCUMENTAL_VIEWS } from '../DocumentalEnums.js';
import * as ACTION from '../../actions.js';

export class AonDocumentalListAyudat extends AonElement {
    _tags;
    page = null;
    TABLE_ID;
    INPUTFILE;
    applicationEl;
    get id() {
        return this.getAttribute(CONSTANT.ID);
    }

    set id(id) {
        this.setAttribute(CONSTANT.ID, id);
    }
    
    constructor () {
        super();
    }

    connectedCallback () {
        this.initialize();
        this.build();
    }

    disconnectedCallback(){
        this.removeToolbar();
    }

    initialize(){
        this.id = DOCUMENTAL_VIEWS.AON_DOCUMENTAL_LIST_AYUDAT;
        this.TABLE_ID = this.id+ "Table";
        this.INPUTFILE = this.id + 'InputFile';
    }

    async build() {
        this.paintView();
        this.getTable();
        this.eventListener();
    }

    paintView(){
        this.applicationEl = this.getApplication();
        this.applicationParentEl = this.applicationEl.getParent();
       
        const content = this.getApplication().getContent();
        let aonTable = this.isMobile() ? new AonMobileList() : new AonTable();
        aonTable.id = this.TABLE_ID;
        content.appendChild(aonTable);

        const input = newComponent({
            type:"input",
            id:this.INPUTFILE,
            styles:{
                display:"none",
            },
            attributes:{
                name:"file",
                type:"file",
                multiple:true,
            }
        });
        input.appendTo(content);
        this.buildToolbar();
    }

    buildToolbar(){
        const input = this.getElement(this.INPUTFILE);
        if(input){
            if(this.isMobile()) {
                this.applicationEl.addFloatOption(ACTION.UPLOAD_FILE, () => input.click());
            } else {
                this.applicationEl.addToolbarOption2(ACTION.UPLOAD_FILE, () => input.click());
            }
            this.applicationEl.addSearchOption();
            this.applicationEl.addEventListener(EVENT.SEARCH, ({detail}) => this.search(detail));
        }
    }

    removeToolbar(){
        if(this.applicationEl){
            this.applicationEl.removeFloatOption();
            this.applicationEl.removeToolbarOptions();
        }
    }

    eventListener(){
        let input = this.getElement(this.INPUTFILE);
        if(input) {
            input.addEventListener(EVENT.CHANGE, () => this.upload(input.files));
        }
    }

    search(detail){
        this.applicationParentEl._filter.busca = detail;
        this.getTable();
    }

    async getTable() {
        if (this.isMobile()) {
            await this.getTableMobile();
        } else {
            await this.getTableDesk();
        }
    }
    
    async getTableDesk() {
        const aonTable = this.getElement(this.TABLE_ID);
        if (aonTable) {
          aonTable.removeColumns();
          aonTable.addColumn(MSG.DATE, 'date', 'dateParse', '20%');
          aonTable.addColumn(MSG.NAME, 'string', 'title', '60%');
          aonTable.addColumn(MSG.SIZE, 'string', 'size', '15%');
          try {
            const resp = await this.getData();
            aonTable.removeRows();
            resp.map((doc) => {
              aonTable.addRow(doc, () => this.aonEvent(doc));
            });
          } catch (e) {
            console.log(e);
          }
        }
      }
    
    async getTableMobile() {
        const aonTable = this.getElement(this.TABLE_ID);
        if (aonTable) {
            try {
                const resp = await this.getData();
                aonTable.removeAllLi();
                resp.map((doc, idx) => {
                    aonTable.addLi({
                        // ...doc,
                        aonIcon: "aon_file",
                        title: doc.title,
                        subtitle:  doc.dateParse + ' - ' + doc.size
                    }, idx, (el) => this.aonEvent(doc));
                });
            } catch (e) {
                console.log(e);
            }
        }
    }

    async getData() {
        let data = [];
        try {
            let filter = this.applicationParentEl._filter;
            filter.method  = "list_docs";
            const {datos} = await postBidoq(filter);
            if(datos){
                data = datos.map(resp =>({
                    ...resp,
                    title: resp.name,
                    dateParse: formatDate(new Date(resp.date * 1000)),
                    size: formatBytes(resp.size)
                }));
            }
        } catch (error) {
            console.log(error);
        }
        return data;
      }

    async upload(files) {
    this.applicationEl.startLoader();
    if(files.length){
        try {
            let folders = [];
            await Promise.all([...files].map(async (file)=>{
                const {content, name, size} = await getReader(file).catch(e=>({}));
                if(name){
                    const nameSplit = name.split('.');
                    folders.push({
                        image_content: content,
                        image_name: nameSplit[0],
                        image_type: nameSplit.reverse()[0],
                        image_size: size
                    });
                }
            }));
            await this.attach(folders);
        } catch (error) {
            console.log(error);
        }
    }
    this.applicationEl.stopLoader();
    }

    async attach(files){
        try {
            if(files.length){
                const services = this.applicationParentEl._filter.carpeta;
                await postBidoq({method:"upload", services, files: JSON.stringify(files)});
                this.getTable();           
            }
        } catch (error) {
            console.log(error);
        }
    }

    aonEvent(doc){
        const {AON_DOCUMENT_AYUDAT, AON_DOCUMENT_MOBILE_AYUDAT} = DOCUMENTAL_VIEWS;
        this.applicationParentEl.showView(
            this.isMobile() ? AON_DOCUMENT_MOBILE_AYUDAT : AON_DOCUMENT_AYUDAT
        , doc);
    }

}

window.customElements.define('aon-documental-ayudat-list', AonDocumentalListAyudat);
