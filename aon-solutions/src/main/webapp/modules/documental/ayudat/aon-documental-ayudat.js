import { AonApplication } from '../../../components/aon-application.js';
import { AonElement } from '../../../components/AonElement.js';
import { CONSTANT, MSG } from '../../../environments/environments.js';
import { getAccessBidoq, getTypeUserBidoq, postBidoq, setClienteId, setSessionId } from  '../../../services/bidoqService.js';
import {  DOCUMENTAL_VIEWS } from '../DocumentalEnums.js';
import { AonDocumentAyudat } from './aon-document-ayudat.js';
import { AonDocumentalListAyudat } from './aon-documental-list-ayudat.js';
import { AonMobileDocumentAyudat } from './aon-mobile-document-ayudat.js';

export class AonDocumentalAyudat extends AonElement {
    AON_DOCUMENTA_AYUDAT;
    _filter;
    _folders;
    _users;
    _tags;
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

    build() {
        this.paintView();
        this.buildData();
    }

    async buildData(){
        this.applicationEl.startLoader();
        await getAccessBidoq().then(async ({datos, message})=>{
            if(CONSTANT.SUCCESS === message && datos){
                const {usuarios} = datos;
                if(usuarios){
                    setClienteId(usuarios[0].uid);
                    setSessionId(usuarios[0].sesion);
                    this.setUsers(usuarios);
                    await this.getFolders();
                    await this.getTags();
                    this.showView(DOCUMENTAL_VIEWS.AON_DOCUMENTAL_LIST_AYUDAT);
                }
            }
        }).catch(e=>{
            console.error(e);
        });
        this.applicationEl.stopLoader();
    }
    paintView(){
        this.createApplication(this.AON_DOCUMENTA_AYUDAT, MSG.DOCUMENTARY, new AonApplication() );
        this.applicationEl = this.getApplication();
    }

    setUsers(users) {
        try {
            const usersOptions = users.map((user) => {
                const typeUser = getTypeUserBidoq(user.tipoID);
                return {
                    name: typeUser,
                    icon: 'person',
                    fn: () => {
                        setClienteId(user.uid);
                        setSessionId(user.sesion);
                        this.applicationEl.addToolbarTitle(typeUser);
                        this.showView(DOCUMENTAL_VIEWS.AON_DOCUMENTAL_LIST_AYUDAT);
                    }
                }
            });
    
            this.applicationEl.addSidenavOptions('TIPO DE USUARIO', usersOptions);
            this._users = users;
        } catch (error) {
            console.error('Ocurrió un error: ' + error.message);
        }
    }

    async getFolders() {
        try {
            const {datos, message} = await postBidoq({"method": "carpetas"});
            if(CONSTANT.SUCCESS === message && datos){
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
                this._folders = datos;
            }
        } catch (error) {
            console.error('Ocurrió un error: ' + error.message);
        }
    }

    async getTags() {
        try {
            const {message, datos} = await postBidoq({method:"tags"});
            if(CONSTANT.SUCCESS === message && datos) {
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
                this._tags = datos;
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
