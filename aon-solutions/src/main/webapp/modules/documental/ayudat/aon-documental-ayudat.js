import { AonApplication } from '../../../components/aon-application.js';
import { AonElement } from '../../../components/AonElement.js';
import { MSG } from '../../../environments/environments.js';
import { getAccessBidoq, postBidoq, setClienteId, setSessionId } from  '../../../services/bidoqService.js';
import {  DOCUMENTAL_VIEWS } from '../DocumentalEnums.js';
import { AonDocumentAyudat } from './aon-document-ayudat.js';
import { AonDocumentalListAyudat } from './aon-documental-list-ayudat.js';
import { AonMobileDocumentAyudat } from './aon-mobile-document-ayudat.js';

export class AonDocumentalAyudat extends AonElement {
    _filter;
    AON_DOCUMENTA_AYUDAT;
    constructor () {
        super();
    }

    connectedCallback () {
        this.initialize();
        this.build();
    }

    initialize(){
        this._filter = {
            carpeta:5,
            busca: ""
        };
        this.AON_DOCUMENTA_AYUDAT = DOCUMENTAL_VIEWS.AON_DOCUMENTAL_AYUDAT;
    }

    async build() {
        this.paintView();
        await this.getFolders();
        await this.getTags();
        getAccessBidoq().then(res=>{
            console.log(res);
        }).catch(e=>{
            console.error(e);
        })
        this.showView(DOCUMENTAL_VIEWS.AON_DOCUMENTAL_LIST_AYUDAT);
    }

    paintView(){
        this.createApplication(this.AON_DOCUMENTA_AYUDAT, MSG.DOCUMENTARY, new AonApplication() );
        this.applicationEl = this.getApplication();
    }

    async getFolders() {
        try {
            setClienteId("e688cab2-04fe-44cc-9771-e934ad63f5fb");
            setSessionId("a0dNR2V2RlhkUlZzLWctTA==");
            
            const {datos} = await postBidoq({"method": "carpetas"});
            if(datos){
                const categoryOptions = datos.map((folder) => {
                    const option = {
                        name: folder.carpeta,
                        icon: 'folder',
                        fn: () => {
                            this._filter.busca = "";
                            delete this._filter.tagID;
                            this._filter.carpeta = folder.carpetaID;
                            this.applicationEl.addToolbarTitle(folder.carpeta);
                            this.showView(DOCUMENTAL_VIEWS.AON_DOCUMENTAL_LIST_AYUDAT);
                        }
                    };
        
                    return option;
                });
        
                this.applicationEl.addSidenavOptions('CATEGORIAS', categoryOptions);
                this.applicationEl.dataset['folders'] = JSON.stringify(datos);
            }
        } catch (error) {
            console.error('Ocurrió un error: ' + error.message);
        }
    }

    async getTags() {
        try {
            const {datos} = await postBidoq({method:"tags"});
            if(datos) {
                const tagsOptions = datos.map((tag) => ({
                    name: tag.tag,
                    icon: 'label',
                    fn: () => {
                        this._filter.busca = "";
                        delete this._filter.carpeta;
                        this._filter.tagID = tag.tagsID;
                        this.showView(DOCUMENTAL_VIEWS.AON_DOCUMENTAL_LIST_AYUDAT);
                    }
                }));
                this.applicationEl.addSidenavOptions(MSG.TAGS, tagsOptions);
                this.applicationEl.dataset['tags'] = JSON.stringify(datos);
            }
        } catch (error) {
            console.log(error);
        }

    }

    showView(view, data = undefined){
        return new Promise(async(resolve)=>{
            let aonView = undefined;
            switch(view){
                case DOCUMENTAL_VIEWS.AON_DOCUMENTAL_LIST_AYUDAT:
                    aonView = new AonDocumentalListAyudat();
                    break;
                case DOCUMENTAL_VIEWS.AON_DOCUMENT_AYUDAT:
                    aonView = new AonDocumentAyudat();
                    break; 
                case DOCUMENTAL_VIEWS.AON_DOCUMENT_MOBILE_AYUDAT:
                    aonView = new AonMobileDocumentAyudat();
                    break; 
            }
            if(aonView){
                aonView.id = view;
                if(data) aonView.data = data;
                this.applicationEl.setContent(aonView);
            }
            resolve(aonView);
        });
    }

}

window.customElements.define('aon-documental-ayudat', AonDocumentalAyudat);
